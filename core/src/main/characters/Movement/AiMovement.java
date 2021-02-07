package characters.Movement;

import characters.Entities.Entity;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import map.Distance;
import map.Map;
import map.Node;
import map.Path;
import org.json.simple.JSONObject;
import screen.Gameplay;
import screen.LoadGame;

import java.util.ArrayList;
import java.util.Random;

/**
 * AI Character object for the game.
 */
public class AiMovement extends Movement {

    public float destX;
    public float destY;

    private PathFinder<Node> pathFinder;
    private Path path;
    private int pathIndex;

    /**
     * Converts a User Input Based movement system to an Ai controlled one
     *
     * @param userData The user data of the entity to alter
     * @param movement The existing movement system to inherit from
     */
    public AiMovement(Entity userData, UserMovement movement) {
        super(userData, movement.world, movement.getPosition().x, movement.getPosition().y);
        this.destX = movement.getPosition().x;
        this.destY = movement.getPosition().y;

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
    public AiMovement(Entity userData, World world, float x, float y) {
        super(userData, world, x, y);
        this.destX = x;
        this.destY = y;

        path = new Path();
        pathFinder = new IndexedAStarPathFinder<Node>(Map.graph);

    }

    /**
     * Builds an Ai movement system from the provided JSON Object
     *
     * @param object The JSON object to load from
     */
    public AiMovement(Entity userData, World world, JSONObject object) {
        super(userData, world, object);
        LoadGame.validateAndLoadObject(object, "movement_type", "ai_movement");
        this.destX = LoadGame.loadObject(object, "dest_x_position", Float.class);
        this.destY = LoadGame.loadObject(object, "dest_y_position", Float.class);
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
        this.setPosition(b2body.getPosition().x - size.x / 1,
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
        findValidPath(destX, destY);
    }

    /**
     * Calculates if there is a path to the given position.
     *
     * @param x x coordinate of destination in pixels
     * @param y y coordinate of destination in pixels
     * @return true if there is a path between character and destination, false otherwise
     */
    private boolean isValidPath(float x, float y) {
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
        return path.getCount() != 0;
    }

    /**
     * As the pathfinder may occasionally fail at finding a path between two points
     * This function, adjusts the start and end points to get a path that is "close enough" to the required direction
     *
     * @param x The x coordinate to target
     * @param y The y coordinate to target
     * @return true if we managed to find a valid path
     */
    private boolean findValidPath(float x, float y) {
        MapObjects spawns = Gameplay.getInstance().map.getLayers().get("npcSpawns").getObjects();
        ArrayList<Vector2> positions = new ArrayList<>();
        Random random = new Random();
        int count = 0;
        for (MapObject spawn : spawns) {
            Rectangle rect = ((RectangleMapObject) spawn).getRectangle();
            positions.add(new Vector2(rect.x, rect.y));
        }
        while (!this.isValidPath(x, y)) {
            Vector2 position = positions.get(random.nextInt(positions.size()));
            x = position.x;
            y = position.y;
            count += 1;
            if (count > 5) {
                return false;
            }
        }

        return true;
/*        float targeted_x_position = x;
        float targeted_y_position = y;
        float original_x = getPosition().x;
        float original_y = getPosition().y;
        int startScale = 1;
        // Expands the circle of backup paths each time, we fail
        int endScale = 1;
        // The different positions to check
        int startOffsetIndex = 0;
        int endOffsetIndex = 0;
        int[][] offsetOptions = new int[][]{new int[]{0, 0}, new int[]{-1, 0}, new int[]{-1, 1}, new int[]{0, 1}, new int[]{1, 1}, new int[]{1, 0}, new int[]{1, -1}, new int[]{0, -1}, new int[]{-1, -1}};
        // Adjust the end position, until a valid path is found, or we fail
        while (!this.isValidPath(targeted_x_position, targeted_y_position)) {
            getPosition().x = original_x + (startScale * offsetOptions[startOffsetIndex][0]);
            getPosition().y = original_y + (startScale * offsetOptions[startOffsetIndex][1]);
            targeted_x_position = x + (endScale * offsetOptions[endOffsetIndex][0]);
            targeted_y_position = y + (endScale * offsetOptions[endOffsetIndex][1]);
            startOffsetIndex += 1;
            if (startOffsetIndex >= offsetOptions.length) {
                startOffsetIndex = 0;
                startScale += 1;
                // If we have failed to find a solution, break and try adjusting the start position instead
                if (startScale >= 5) {
                    endOffsetIndex += 1;
                    if (endOffsetIndex >= offsetOptions.length) {
                        endOffsetIndex = 0;
                        endScale += 1;
                        if (endScale >= 5) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;*/
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

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        System.out.println(state);
        state.put("movement_type", "ai_movement");
        state.put("dest_x_position", this.destX);
        state.put("dest_y_position", this.destY);
        return state;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AiMovement)) {
            return false;
        }
        AiMovement otherAi = (AiMovement) other;
        if (this.destX == otherAi.destX && this.destY == otherAi.destY) {
            return false;
        }
        return ((Movement) this).equals(other);
    }
}

