package characters.Entities;

import characters.Entities.abilities.*;
import characters.Movement.AiMovement;
import characters.Movement.UserMovement;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import tools.CharacterRenderer;
import tools.Controller;

import java.util.ArrayList;
import java.util.List;

import static screen.Gameplay.TILE_SIZE;


/**
 * Main player object for the game.
 */
public class Player extends Entity {
    public final EnemyManager enemyManager;
    public boolean ishealing;
    private List<IAbility> abilities;
    public GlobalSlowDownAbility globalSlowDownAbility;
    public ReinforcedSystemsAbility reinforcedSystemsAbility;
    public MarkInfiltratorAbility markInfiltratorAbility;

    /**
     * The time since the target for the AI movement system was last updated
     */
    private final float timeSinceEnemyTargetUpdated = 0;
    /**
     * The x and y coordinates of the Jail
     */
    //private final Vector2 jailPosition;
    public float health;
    //public int arrestedCount = 0;
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
        creatAbilities();
    }

    private void creatAbilities() {


        globalSlowDownAbility = new GlobalSlowDownAbility();
        globalSlowDownAbility.setHost(this);
        globalSlowDownAbility.setTarget(this);

        reinforcedSystemsAbility = new ReinforcedSystemsAbility();
        reinforcedSystemsAbility.setHost(this);


        markInfiltratorAbility = new MarkInfiltratorAbility();
        markInfiltratorAbility.setHost(this);

        abilities = new ArrayList<>();
        abilities.add(globalSlowDownAbility);
        abilities.add(reinforcedSystemsAbility);
        abilities.add(markInfiltratorAbility);
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
        if (Controller.isAbility1Pressed()) {
            globalSlowDownAbility.setTarget(this);
            globalSlowDownAbility.tryUseAbility();
        }
        if (Controller.isAbility2Pressed()) {
            reinforcedSystemsAbility.setTarget(this);
            reinforcedSystemsAbility.tryUseAbility();
        }
        if (Controller.isAbility3Pressed()) {
            markInfiltratorAbility.setTarget(this);
            markInfiltratorAbility.tryUseAbility();
        }
        // should be called each loop of rendering
        healing(delta);

        for (IAbility ability : abilities) {
            ability.update(delta);
        }
    }


    public boolean isArrestPressed() {
        return this.arrestPressed;
    }

    public void setArrestPressed(boolean value) {
        this.arrestPressed = value;
    }
}
