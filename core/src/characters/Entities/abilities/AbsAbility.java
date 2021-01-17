package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * (NEW) Manage the abilities for infilltrators
 */
public abstract class AbsAbility {
    public boolean disabled;

    public Player target;
    public float useTime = 30;
    public float cooldownTime = 30;
    public boolean ready;
    public boolean inUse;

    /**
     * (NEW)Special Ability Enemy should have.
     */
    public AbsAbility() {

        inUse = false;
        ready = true;
        disabled = false;
    }

    /**
     * Disable ability.
     *
     * @param disable true when arrested
     */
    public void setDisable(boolean disable) {
        disabled = disable;
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
    public void provokeAbility() {
        if (ready && !disabled) {
            useTime = 30f;
            inUse = true;
            ready = false;
        }
    }

    /**
     * Generate a random ability for enemy.
     *
     * @param enemy  The enemy who should use their ability
     * @param player The player to target
     */
    public abstract void useAbility(Enemy enemy, Player player);

    /**(NEW)
     * cool down timer.
     *
     * @param delta delta time
     * @param enemy Enemy
     */
    public void update(float delta, Enemy enemy) {
        if (inUse) {
            if (useTime >= delta) {
                useTime -= delta;
            } else {
                removeAbility(enemy);
                inUse = false;
                cooldownTime = 30;
            }
        } else if (!ready) {
            if (cooldownTime >= delta) {
                cooldownTime -= delta;
            } else {
                ready = true;
            }
        }
    }

    /**
     * remove the ability effect.
     *
     * @param enemy Enemy
     */
    public abstract void removeAbility(Enemy enemy);
}
