package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Entity;
import characters.Entities.Player;
import screen.Gameplay;
import sprites.Systems;

public abstract class AbilityBase<THost extends Entity, TTarget extends Entity> implements IAbility<THost, TTarget> {

    protected TTarget target;
    protected THost host;

    protected boolean isDisabled;

    //Used to set the time that an ability can be used
    public float useTime = 30;
    //Used to calculate the time change by delta when the ability is being used
    public float useTimeTiming = 0;
    //Used to set the amount of time an ability needs to cool down
    public float cooldownTime = 30;
    //Used to calculate the time change by delta when the ability is being cooled down
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
