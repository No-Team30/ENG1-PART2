package characters.Entities;

import characters.Movement.AiMovement;
import characters.Movement.UserMovement;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import tools.CharacterRenderer;
import tools.Controller;

import java.util.ArrayList;


/**
 * Main player object for the game.
 */
public class Player extends Entity {
    /**
     * The x and y coordinates of the Jail
     */
    private final Vector2 jailPosition;
    public Enemy nearbyEnemy;
    public float health;
    public boolean ishealing;
    public boolean arrestPressed;
    public int arrestedCount = 0;
    public ArrayList<Enemy> jailedEnemies = new ArrayList<>();
    public ArrayList<Enemy> arrestedEnemies = new ArrayList<>();

    /**
     * The time since the target for the AI movement system was last updated
     */
    private float timeSinceEnemyTargetUpdated = 0;
    /**
     * How often to change the target of the ai system (in seconds)
     */
    private final float refreshAiEnemyTarget = 0.25f;

    /**
     * The range in which the AI Player can arrest Enemies (In pixels)
     */
    private final int AI_ARREST_RANGE = 64;

    /**
     * creates an semi-initalised player the physics body is still uninitiated.
     *
     * @param world The game world
     * @param x     The inital x location of the player
     * @param y     The inital y location of the player
     */
    public Player(World world, float x, float y, Vector2 jailPosition) {
        super(CharacterRenderer.Sprite.AUBER);
        //WARN This can be null
        this.movementSystem = new UserMovement(this, world, x, y);
        // Should only contain the x and y coordinates
        this.jailPosition = jailPosition;
        this.movementSystem.b2body.setUserData("auber");
        this.health = 100f;
        this.ishealing = false;
        arrestPressed = false;

    }

    /**
     * Sets whether or not Player is currently healing.
     *
     * @param isheal set ishealing to true or false
     */
    public void setHealing(boolean isheal) {
        ishealing = isheal;
    }

    /**
     * Healing auber.
     *
     * @param delta The time in secconds since the last update
     */
    public void healing(float delta) {
        // healing should end or not start if auber left healing pod or not contact with healing pod
        if (this.movementSystem.b2body.getUserData() == "auber") {
            setHealing(false);
            return;
        }
        // healing should start if auber in healing pod and not in full health
        if (this.movementSystem.b2body.getUserData() == "ready_to_heal" && health < 100f) {
            setHealing(true);
        } else if (this.movementSystem.b2body.getUserData() == "ready_to_heal" && health == 100f) {
            setHealing(false);
        }
        // healing process
        if (ishealing) {
            // adjust healing amount accordingly
            health += 20f * delta;
            if (health > 100f) {
                health = 100f;
            }
        }

    }


    @Override
    public void update(float delta) {
        super.update(delta);
        // Handles demo mode movement
        // Player AI movement, targets the closest nearby enemy
        this.timeSinceEnemyTargetUpdated += delta;
        if (this.movementSystem instanceof AiMovement && this.timeSinceEnemyTargetUpdated > this.refreshAiEnemyTarget) {
            this.timeSinceEnemyTargetUpdated = 0;
            Enemy closest_enemy = null;
            int closest_distance = Integer.MAX_VALUE;
            // Set all arrested enemies to follow the Player
            for (Enemy enemy : this.arrestedEnemies) {
                ((AiMovement) enemy.movementSystem).setDestination(this.movementSystem.position);
                ((AiMovement) enemy.movementSystem).moveToDestination();
            }

            // TODO Optimisation Could maybe store all non arrested enemies
            for (Enemy enemy : EnemyManager.enemies) {
                if (!enemy.isArrested()) {
                    int enemyDistance = (int) enemy.movementSystem.position.dst(this.movementSystem.position);
                    if (enemyDistance < AI_ARREST_RANGE) {
                        System.out.println("    Arresting Enemy: " + enemy.movementSystem.b2body.getUserData() + " at: " + enemy.movementSystem.position + " with distance: " + enemyDistance);
                        arrest(enemy);
                    } else if (enemyDistance < closest_distance) {
                        closest_distance = enemyDistance;
                        closest_enemy = enemy;
                    }
                }
            }
            // If we have arrested an enemy, move to the jail
            // Otherwise go to the closest enemy
            if (this.arrestedEnemies.size() > 0) {
                // If we are at the jail, put arrested enemies in jail (Within 2 squares distance of the jail)
                if (this.movementSystem.position.dst(this.jailPosition) < 64 * 2) {
                    while (arrestedEnemies.size() > 0) {
                        this.arrestedCount += 1;
                        Enemy enemy = arrestedEnemies.remove(0);
                        enemy.targetSystem = null;
                        enemy.movementSystem.b2body.setTransform(jailPosition, 0);
                        ((AiMovement) enemy.movementSystem).stop();
                    }
                } else {
                    ((AiMovement) this.movementSystem).setDestination(this.jailPosition);
                    ((AiMovement) this.movementSystem).moveToDestination();
                }
            } else if (closest_enemy != null) {
                System.out.println("Targeting Enemy: " + closest_enemy.movementSystem.b2body.getUserData() + " at: " + closest_enemy.movementSystem.position + " Player at: " + this.movementSystem.position + " With distance: " + closest_distance);
                ((AiMovement) this.movementSystem).setDestination(closest_enemy.movementSystem.position);
                ((AiMovement) this.movementSystem).moveToDestination();

            }

        }
        // TODO arrestPressed can never be false?
        if (Controller.isArrestPressed()) {
            arrestPressed = true;
        }
        if (nearbyEnemy != null && arrestPressed && !nearbyEnemy.isArrested()) {
            System.out.println("Arrested: " + nearbyEnemy.movementSystem.b2body.getUserData() + " arrestPressed: ");
            arrest(nearbyEnemy);
        }

        // should be called each loop of rendering
        healing(delta);

    }

    /**
     * Arrest enemy.
     *
     * @param enemy The enemy object
     */
    public void arrest(Enemy enemy) {
        arrestedEnemies.add(enemy);
        // stop enemy's sabotaging if it does
        enemy.set_ArrestedMode();
        // set enemy destination to auber's left,enemy should follow auber until it is in jail
        ((AiMovement) enemy.movementSystem).setDestination(this.movementSystem.position.x, this.movementSystem.position.y);
        ((AiMovement) enemy.movementSystem).moveToDestination();

    }

    public void inprisonEnemy(Enemy enemy) {
    }

    /**
     * set the nearby enemy.
     *
     * @param enemy The enemy object
     */
    public void setNearby_enemy(Enemy enemy) {
        nearbyEnemy = enemy;
    }

    /**
     * If auber is arresting an enemy.
     *
     * @return true if auber is currently arresting an enemy
     */
    public boolean is_arresting() {
        return nearbyEnemy != null;
    }

    /**
     * avoid arresting enemy already in jail twice.
     *
     * @param enemy The enemy object
     * @return True if enemy is not in arrested or jailed enemy arraylist
     */
    public boolean not_arrested(Enemy enemy) {
        return !jailedEnemies.contains(enemy) && !arrestedEnemies.contains(enemy);
    }

}
