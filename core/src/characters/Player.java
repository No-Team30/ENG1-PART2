package characters;

import characters.ai.Enemy;
import characters.movement.AiMovement;
import characters.movement.Movement;
import com.badlogic.gdx.physics.box2d.World;
import tools.CharacterRenderer;
import tools.Controller;

import java.util.ArrayList;


/**
 * Main player object for the game.
 */
public class Player extends Entity {
    public Enemy nearbyEnemy;
    public float health;
    public boolean ishealing;
    public boolean arrestPressed;
    public int arrestedCount = 0;
    public ArrayList<Enemy> arrestedEnemy = new ArrayList<>();

    /**
     * creates an semi-initalised player the physics body is still uninitiated.
     *
     * @param world The game world
     * @param x     The inital x location of the player
     * @param y     The inital y location of the player
     */
    public Player() {
        super(CharacterRenderer.Sprite.AUBER);
        //WARN This can be null
        this.movementSystem = null;

        this.health = 100f;
        this.ishealing = false;
        arrestPressed = false;

    }

    public void setMovementSystem(Movement movement) {
        this.movementSystem = movement;
        this.movementSystem.b2body.setUserData("auber");
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
        // TODO arrestPressed can never be false?
        if (Controller.isArrestPressed()) {
            arrestPressed = true;
        }

        if (nearbyEnemy != null && arrestPressed) {
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
        // stop enemy's sabotaging if it does
        enemy.set_ArrestedMode();
        // set enemy destination to auber's left,enemy should follow auber until it is in jail
        ((AiMovement) enemy.movementSystem).setDest(this.movementSystem.position.x, this.movementSystem.position.y);
        ((AiMovement) enemy.movementSystem).moveToDest();

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
     * @return True if enemy is not in arrested enemy arraylist
     */
    public boolean not_arrested(Enemy enemy) {
        return !arrestedEnemy.contains(enemy);
    }

}
