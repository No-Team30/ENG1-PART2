package characters.Entities.abilities;

import characters.Entities.Entity;
import org.json.simple.JSONObject;
import screen.LoadGame;

public abstract class AbilityBase<THost extends Entity, TTarget extends Entity> implements IAbility<THost, TTarget> {

    protected TTarget target;
    protected THost host;

    protected boolean isDisabled;

    public float useTime = 30;
    public float useTimeTiming = 0;
    public float cooldownTime = 30;
    public float cooldownTimeTiming = 0;

    public boolean isReady;
    public boolean inUse;

    /**
     * Special Ability Enemy should have.
     */
    public AbilityBase() {

        inUse = false;
        isReady = true;
        isDisabled = false;
    }

    /**
     * Builds an ability from a JSON Object
     *
     * @param object The data to load from
     */
    AbilityBase(JSONObject object) {
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
    public static AbilityBase loadAbility(JSONObject object) {
        String abilityType = LoadGame.loadObject(object, "ability_type", String.class);
        switch (abilityType) {
            case "stop_player":
                return new StopTargetAbility(object);
            case "speed_up":
                return new SpeedingUpAbility(object);
            case "slow_player":
                return new SlowDownTargetAbility(object);
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


    public float getUseTime() {
        return useTime;
    }

    public float getUseTimeTiming() {
        return useTimeTiming;
    }

    public float getCooldownTime() {
        return cooldownTime;
    }

    public float getCooldownTimeTiming() {
        return cooldownTimeTiming;
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
     * try to start use ability,checking ability whether cooldown and in provoke ability status or not and use ability
     */
    @Override
    public void tryUseAbility() {
        if (isReady && !isDisabled) {
            useTimeTiming = useTime;
            inUse = true;
            isReady = false;
            beginUseAbility();
        }
    }

    /**
     * @return get if this ability is in use
     */
    @Override
    public boolean inUse() {
        return inUse;
    }

    /**
     * get if this ability is ready to use
     *
     * @return
     */
    @Override
    public boolean isReady() {
        return isReady;
    }

    /**
     * return if this ability is disable
     *
     * @return
     */
    @Override
    public boolean isDisable() {
        return isDisabled;
    }

    /**
     * set this ability's host entity.
     *
     * @param tHost
     */
    @Override
    public void setHost(THost tHost) {
        this.host = tHost;
    }

    /**
     * get this ability's host entity.
     *
     * @return this ability's host entity.
     */
    @Override
    public THost getHost() {
        return host;
    }

    /**
     * set this ability's host entity.
     *
     * @param tTarget ability target
     */
    @Override
    public void setTarget(TTarget tTarget) {
        this.target = tTarget;
    }

    /**
     * get this ability's host entity.
     *
     * @return this ability's host entity.
     */
    @Override
    public TTarget getTarget() {
        return target;
    }

    /**
     * begin to use ability
     */
    @Override
    public void beginUseAbility() {

    }

    /**
     * Use ability,this will be called in update when useTiming > 0 ;
     */
    @Override
    public void useAbility(float delta) {

    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {

    }

    /**
     * Updates the duration the ability has been active for,
     * and removes it if it has expired. Will also update the cooldown time.
     *
     * @param delta the time elapsed since this function was last called
     */
    @Override
    public void update(float delta) {
        if (inUse) {
            if (useTimeTiming >= delta) {
                useTimeTiming -= delta;
                useAbility(delta);
            } else {
                endUseAbility();
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
}
