package characters.Entities;

import characters.Movement.AiMovement;
import characters.Movement.UserMovement;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONObject;
import tools.CharacterRenderer;
import tools.Controller;

import static screen.Gameplay.TILE_SIZE;


/**
 * Main player object for the game.
 */
public class Player extends Entity {
    public final EnemyManager enemyManager;
    public boolean ishealing;

    /**
     * The time since the target for the AI movement system was last updated
     */
    //TODO Can we remove this?
    private final float timeSinceEnemyTargetUpdated = 0;
    /**
     * The x and y coordinates of the Jail
     */
    public float health;
    private boolean arrestPressed;
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
    public Player(World world, float x, float y, TiledMap map) {
        super(CharacterRenderer.Sprite.AUBER);
        //WARN This can be null
        this.movementSystem = new UserMovement(this, world, x, y);
        this.enemyManager = new EnemyManager(world, map);
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
        if (this.movementSystem instanceof AiMovement) {
            // Find the closest enemy, and arrest if in range
            Enemy closest_enemy = this.enemyManager.getClosestActiveEnemy(this.getPosition());
            if (closest_enemy != null && this.distanceTo(closest_enemy) < AI_ARREST_RANGE) {
                enemyManager.arrestEnemy(closest_enemy);
                closest_enemy = null;
            }
            // If we have arrested an enemy, and are not at the jail, go to the jail
            // Otherwise go to the closest enemy
            if (this.enemyManager.haveEnemiesBeenArrested() && this.distanceTo(this.enemyManager.getJailPosition()) > TILE_SIZE * 2) {
                ((AiMovement) this.movementSystem).setDestination(this.enemyManager.getJailPosition());
                ((AiMovement) this.movementSystem).moveToDestination();
            } else if (closest_enemy != null) {
                ((AiMovement) this.movementSystem).setDestination(closest_enemy.getPosition());
                ((AiMovement) this.movementSystem).moveToDestination();
            }
        }
        if (Controller.isArrestPressed()) {
            arrestPressed = true;
        }
        // should be called each loop of rendering
        healing(delta);

    }


    public boolean isArrestPressed() {
        return this.arrestPressed;
    }

    public void setArrestPressed(boolean value) {
        this.arrestPressed = value;
    }

    @Override
    public JSONObject save() {
        JSONObject state = new JSONObject();
/*        state.put("entity_type", "auber");
        state.put("arrestedCount", this.arrestedCount);
        state.put("jailPosition", this.jailPosition);
        state.put("health", this.health);
        state.put("nearbyEnemy", this.nearbyEnemy);
        state.put("ishealing", this.ishealing);
        state.put("arrestPressed", this.arrestPressed);
        state.put("arrestedCount", this.arrestedCount);
        state.put("movement", this.movementSystem.save());*/
        return state;
    }
}
