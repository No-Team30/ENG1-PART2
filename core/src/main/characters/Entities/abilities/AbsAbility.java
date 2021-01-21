package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Manage the abilities for enemies
 */
public abstract class AbsAbility {
    public boolean isDisabled;

    public Player target;
    public float useTime = 30;
    public float useTimeTiming = 0;
    public float cooldownTime = 30;
    public float cooldownTimeTiming = 0;

    public boolean isReady;
    public boolean inUse;

    /**
     * Special Ability Enemy should have.
     */
    public AbsAbility() {
        inUse = false;
        isReady = true;
        isDisabled = false;
    }

    /**
     * Builds an ability from a JSON Object
     *
     * @param object The data to load from
     */
    AbsAbility(JSONObject object) {
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        inUse = LoadGame.loadObject(object, "in_use", Boolean.class);
        isReady = LoadGame.loadObject(object, "is_ready", Boolean.class);
        isDisabled = LoadGame.loadObject(object, "is_disabled", Boolean.class);
        useTime = LoadGame.loadObject(object, "use_time", Float.class);
        useTimeTiming = LoadGame.loadObject(object, "use_time_timing", Float.class);
        cooldownTime = LoadGame.loadObject(object, "cooldown_time", Float.class);
        cooldownTimeTiming = LoadGame.loadObject(object, "cooldown_time_timing", Float.class);
    }

    /**
     * Builds a new movement_type from the JSON Object
     *
     * @param object The JSON Object to build the movement system from
     * @throws IllegalArgumentException if 'movement_type' parameter, does not match any known movement system
     */
    public static AbsAbility loadAbility(JSONObject object) {
        String abilityType = LoadGame.loadObject(object, "ability_type", String.class);
        switch (abilityType) {
            case "stop_player":
                return new StopPlayerAbility(object);
            case "speed_up":
                return new SpeedingUpAbility(object);
            case "slow_player":
                return new SlowDownPlayerAbility(object);
            case "higher_system_damage":
                return new HigherSystemDamageAbility(object);
            case "ghost_mode":
                return new GhostModeAbility(object);
            case "attack_player":
                return new AttackPlayerAbility(object);
            default:
                throw new IllegalArgumentException("ability_type parameter, does not match any known ability " +
                        "types (" + abilityType + ")");
        }
    }

    /**
     * Exports all ability based data to a json object(for save games)
     *
     * @return A JSON of ability data
     */
    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "ability");
        state.put("in_use", inUse);
        state.put("is_ready", isReady);
        state.put("is_disabled", isDisabled);
        state.put("use_time", useTime);
        state.put("use_time_timing", useTimeTiming);
        state.put("cooldown_time", cooldownTime);
        state.put("cooldown_time_timing", cooldownTimeTiming);
        return state;

    }

    /**
     * Disable ability.
     *
     * @param disable true when arrested
     */
    public void setDisable(boolean disable) {
        isDisabled = disable;
    }

    /**
     * set the target.
     *
     * @param auber Player
     */
    public void setTarget(Player auber) {
        this.target = auber;
    }

    /**
     * provoke ability status.
     */
    public void provokeAbility(Enemy enemy, Player player) {
        if (isReady && !isDisabled) {
            useTimeTiming = useTime;
            inUse = true;
            isReady = false;
            useAbility(enemy, player);
        }
    }

    /**
     * Use ability
     *
     * @param enemy  The enemy who should use their ability
     * @param player The player to target
     */
    public abstract void useAbility(Enemy enemy, Player player);

    /**
     * update cooldown time and use time
     *
     * @param delta delta time in the game world
     * @param enemy Enemy
     */
    public void update(float delta, Enemy enemy) {
        if (inUse) {
            if (useTimeTiming >= delta) {
                useTimeTiming -= delta;
            } else {
                removeAbility(enemy);
                inUse = false;
                cooldownTimeTiming = cooldownTime;
            }
        } else if (!isReady) {
            if (cooldownTimeTiming >= delta) {
                cooldownTimeTiming -= delta;
            } else {
                isReady = true;
            }
        }
    }

    /**
     * remove the ability effect.
     *
     * @param enemy Enemy
     */
    public void removeAbility(Enemy enemy) {

    }
}
