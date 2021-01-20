package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

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
