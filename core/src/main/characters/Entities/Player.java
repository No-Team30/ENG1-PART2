package characters.Entities;

import characters.Entities.abilities.*;
import characters.Movement.AiMovement;
import characters.Movement.Movement;
import characters.Movement.UserMovement;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import screen.LoadGame;
import tools.CharacterRenderer;
import tools.Controller;

import java.util.Map;
import java.util.TreeMap;

import static screen.Gameplay.TILE_SIZE;


/**
 * Main player object for the game.
 */
public class Player extends Entity {
    /**
     * The handler for all enemy entities
     */
    public final EnemyManager enemyManager;
    /**
     * Whether the player is healing
     */
    public boolean isHealing;

    /**
     * A list of player abilities
     * (Indexed by the key press required to apply the ability)
     * Key S - globalSlowDownAbility
     * Key D - reinforcedSystemsAbility
     * Key F - markInfiltratorAbility
     */
    public Map<Integer, IAbility> abilityMap;

    /**
     * The health of the player
     */
    public float health;
    /**
     * Whether the arrest key has been pressed
     */
    private boolean isArrestPressed;

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
        this.isHealing = false;
        isArrestPressed = false;
        creatAbilities();
    }

    /**
     * Builds a Player instance from the provided JSON Object
     *
     * @param jsonObject The json object to load the data from
     */
    public Player(World world, TiledMap map, JSONObject jsonObject) {
        super(CharacterRenderer.Sprite.AUBER);
        LoadGame.validateAndLoadObject(jsonObject, "entity_type", "auber");
        this.health = LoadGame.loadObject(jsonObject, "health", Float.class);
        this.isHealing = LoadGame.loadObject(jsonObject, "is_healing", Boolean.class);
        this.isArrestPressed = LoadGame.loadObject(jsonObject, "is_arrest_pressed", Boolean.class);
        this.movementSystem = Movement.loadMovement(this, world, LoadGame.loadObject(jsonObject, "movement",
                JSONObject.class));
        this.enemyManager = new EnemyManager(world, LoadGame.loadObject(jsonObject, "enemy_manager", JSONObject.class));
        this.abilityMap = new TreeMap<>();
        for (Object abilityObject : LoadGame.loadObject(jsonObject, "abilities", JSONArray.class)) {
            if (!(abilityObject instanceof JSONObject)) {
                throw new IllegalArgumentException("Save file does not contain auber abilities");
            }
            JSONObject abilityJSON = (JSONObject) abilityObject;
            IAbility ability = AbilityBase.loadAbility(abilityJSON);
            ability.setHost(this);
            if (ability instanceof GlobalSlowDownAbility) {
                ability.setTarget(this);
                System.out.println("Set target for ability:" + abilityJSON);
            }
            Integer keyCode = LoadGame.loadObject(abilityJSON, "key", Integer.class);
            this.abilityMap.put(keyCode, ability);
        }
    }

    private void creatAbilities() {


        GlobalSlowDownAbility globalSlowDownAbility = new GlobalSlowDownAbility();
        globalSlowDownAbility.setHost(this);
        globalSlowDownAbility.setTarget(this);

        ReinforcedSystemsAbility reinforcedSystemsAbility = new ReinforcedSystemsAbility();
        reinforcedSystemsAbility.setHost(this);


        MarkInfiltratorAbility markInfiltratorAbility = new MarkInfiltratorAbility();
        markInfiltratorAbility.setHost(this);

        abilityMap = new TreeMap<>();
        abilityMap.put(Input.Keys.S, globalSlowDownAbility);
        abilityMap.put(Input.Keys.D, reinforcedSystemsAbility);
        abilityMap.put(Input.Keys.F, markInfiltratorAbility);
    }

    /**
     * Sets whether or not Player is currently healing.
     *
     * @param isheal set ishealing to true or false
     */
    public void setHealing(boolean isheal) {
        isHealing = isheal;
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
        if (isHealing) {
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
            isArrestPressed = true;
        }
        //TODO Change this to entrySet

        for (int key : abilityMap.keySet()) {
            if (Controller.isKeyPressed(key)) {
                IAbility ability = abilityMap.get(key);
                //ability.setTarget(this);
                ability.tryUseAbility();
            }
        }

        // should be called each loop of rendering
        healing(delta);

        for (IAbility ability : abilityMap.values()) {
            ability.update(delta);
        }
    }

    @Override
    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "entity");
        state.put("entity_type", "auber");
        state.put("health", this.health);
        state.put("is_healing", this.isHealing);
        state.put("is_arrest_pressed", this.isArrestPressed);
        state.put("movement", this.movementSystem.save());
        state.put("enemy_manager", this.enemyManager.save());
        JSONArray abilities = new JSONArray();
        for (Map.Entry<Integer, IAbility> abilityEntry : this.abilityMap.entrySet()) {
            JSONObject abilityJSON = abilityEntry.getValue().save();
            abilityJSON.put("key", abilityEntry.getKey());
            abilities.add(abilityJSON);
        }
        state.put("abilities", abilities);
        return state;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Player)) {
            return false;
        }
        Player other = (Player) o;
        if (!((Entity) this).equals(o)) {
            return false;
        }
        if (this.movementSystem.equals(other.movementSystem)) {
            return false;
        }
        if (this.health != other.health) {
            return false;
        }
        if (this.isHealing != other.isHealing) {
            return false;
        }
        return this.isArrestPressed() == other.isArrestPressed();
    }

    public boolean isArrestPressed() {
        return this.isArrestPressed;
    }

    public void setArrestPressed(boolean value) {
        this.isArrestPressed = value;
    }
}
