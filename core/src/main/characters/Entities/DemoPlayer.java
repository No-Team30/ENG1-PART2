/*
package characters.ai;

import characters.Entities.Player;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import map.Map;
import map.Node;
import map.Path;

public class DemoPlayer extends Player {
    //TODO Should probably have interface or something to reduce duplication in AiCharacter
    private EnemyManager enemyManager;

    public float destX;
    public float destY;

    private PathFinder<Node> pathFinder;
    private Path path;
    private int pathIndex;

    */
/**
     * creates an semi-initalised player the physics body is still uninitiated.
     *
     * @param world The game world
     * @param x     The inital x location of the player
     * @param y     The inital y location of the player
     *//*

    public DemoPlayer(World world, float x, float y) {
        super(world, x, y);
        this.arrestPressed = true;
        path = new Path();
        pathFinder = new IndexedAStarPathFinder<Node>(Map.graph);
    }

    public void assign_target() {
        int closestDistance = Integer.MAX_VALUE;
        Enemy targetEnemy = null;
        for (Enemy enemy : EnemyManager.enemies) {
            int enemyDistance = (int) this.position.dst(enemy.position);
            if (enemyDistance < closestDistance) {
                closestDistance = enemyDistance;
                targetEnemy = enemy;
            }
        }
    }


    @Override
    public void update(float delta) {
        super.update(delta);
        Vector2 direction = this.decideDirection();
        this.move(delta, direction);
        // position sprite properly within the box
        position.set(b2body.getPosition().x - size.x / 1,
                b2body.getPosition().y - size.y / 1 + 4);

        renderer.update(delta, direction);

    }

}
*/
