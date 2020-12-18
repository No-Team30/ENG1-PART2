package characters.Movement;

import characters.Entities.Entity;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import map.Distance;
import map.Map;
import map.Node;
import map.Path;

/**
 * AI Character object for the game.
 */
public class AiMovement extends Movement {

    public float destX;
    public float destY;

    private PathFinder<Node> pathFinder;
    private Path path;
    private int pathIndex;

    private static int numberOfAiEntities = 0;


    /**
     * Converts a User Input Based movement system to an Ai controlled one
     *
     * @param entity   The entity to alter
     * @param movement The existing movement system to inherit from
     */
    public AiMovement(Entity entity, UserMovement movement) {
        super(entity, movement.world, movement.position.x, movement.position.y);
        this.destX = movement.position.x;
        this.destY = movement.position.y;
        // Maybe not needed?
        numberOfAiEntities++;

        path = new Path();
        pathFinder = new IndexedAStarPathFinder<Node>(Map.graph);
    }

    /**
     * creates an semi-initalised AI character the physics body is still uninitiated.
     *
     * @param world The game world
     * @param x     The inital x location of the character
     * @param y     The inital y location of the character
     */
    public AiMovement(Entity entity, World world, float x, float y) {
        super(entity, world, x, y);
        this.destX = x;
        this.destY = y;
        // Maybe not needed?
        numberOfAiEntities++;

        path = new Path();
        pathFinder = new IndexedAStarPathFinder<Node>(Map.graph);

    }

    /**
     * Creates the physics bodies for the character Sprite.
     */
    @Override
    public void createBody() {
        super.createBody();
        b2body.getFixtureList().get(0).setSensor(true);
    }


    /**
     * Updates the character, should be called every update cycle.
     *
     * @param delta The time in secconds since the last update
     */
    @Override
    public Vector2 update(float delta) {
        Vector2 direction = this.decideDirection();
        this.move(delta, direction);
        // position sprite properly within the box
        position.set(b2body.getPosition().x - size.x / 1,
                b2body.getPosition().y - size.y / 1 + 4);
        return direction;
    }

    /**
     * applies the move to the character.
     *
     * @param delta     secconds since last update
     * @param direction direction to move character
     */
    public void move(float delta, Vector2 direction) {

        // applies a velocity of direction * time delta * speed
        Vector2 vel = direction.scl(delta * this.speed);
        this.b2body.applyLinearImpulse(vel, this.b2body.getWorldCenter(), true);
    }

    /**
     * Decides the direction to be made by the AI.
     *
     * @return a unit vector representing direction
     */
    private Vector2 decideDirection() {
        if (this.isMoving()) {
            Node target = path.get(pathIndex);
            int targetY = (target.getIndex() / Map.mapTileWidth) * Map.tilePixelHeight + 16;
            int targetX = (target.getIndex() % Map.mapTileWidth) * Map.tilePixelWidth + 16;

            float x = this.b2body.getPosition().x;
            float y = this.b2body.getPosition().y;

            float xcomp = 0;
            float ycomp = 0;

            // if the difference in x values between character and node is above 1
            // move in x directio n towards node
            if (Math.abs(x - targetX) > 1) {
                xcomp = targetX - x;
            }
            // if the difference in y values between character and node is above 1
            // move in y directio n towards node
            if (Math.abs(y - targetY) > 1) {
                ycomp = targetY - y;
            }

            // if the character is in the bounds of the node
            // target the next node
            if (Math.abs(y - targetY) < 4 && Math.abs(x - targetX) < 4) {
                this.pathIndex++;
                return new Vector2(0, 0);
            }

            float abs = (float) Math.sqrt(Math.pow(xcomp, 2) + Math.pow(ycomp, 2));
            return new Vector2(xcomp / abs, ycomp / abs);
        }
        return new Vector2(0, 0);
    }

    /**
     * Set the destination position to target
     *
     * @param x x component
     * @param y y component
     */
    public void setDestination(float x, float y) {
        destX = x;
        destY = y;
    }

    /**
     * Set the destination position to target
     *
     * @param position The coordinates to target
     */
    public void setDestination(Vector2 position) {
        destX = position.x;
        destY = position.y;
    }

    /**
     * move to destination.
     */
    public void moveToDestination() {
        goTo(destX, destY);
    }

    /**
     * Calculates if there is a path to the given position.
     *
     * @param x x coordinate of destination in pixels
     * @param y y coordinate of destination in pixels
     * @return true if there is a path between character and destination, false otherwise
     */
    public boolean goTo(float x, float y) {


        Vector2 position = this.b2body.getPosition();

        Node startNode = Map.graph.getNodeByXy((int) position.x, (int) position.y);
        Node endNode = Map.graph.getNodeByXy((int) x, (int) y);

        // if the character is already at or moving to the destination
        if (this.path.getCount() > 0) {
            int currentEndNode = this.path.get(this.path.getCount() - 1).getIndex();
            if (startNode.getIndex() == endNode.getIndex()
                    || endNode.getIndex() == currentEndNode) {
                return true;
            }
        }


        // resets the path
        this.path = new Path();
        this.pathFinder = new IndexedAStarPathFinder<Node>(Map.graph);
        pathIndex = 1;


        // A* search between character and destination
        pathFinder.searchNodePath(startNode, endNode, new Distance(), path);

        // if the path is empty
        if (path.getCount() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * if the character is currently moving along a path.
     *
     * @return whether the character is currently following a path
     */
    public boolean isMoving() {
        return this.pathIndex < this.path.getCount();
    }

    /**
     * stops the character from following its path.
     */
    public void stop() {
        this.pathIndex = this.path.getCount();
    }
}

