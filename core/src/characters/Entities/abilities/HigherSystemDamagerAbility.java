package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * Create ability to damage system with higher damage.
 */
public class HigherSystemDamagerAbility extends AbilityBase<Enemy, Enemy> {
    private double damageConstant = 2;

    /**
     * begin to use ability,Enemy to attack systems with higher damage .
     */
    @Override
    public void beginUseAbility() {
        host.systemDamage *= damageConstant;
    }

    /**
     * Enemy's damage back to normal
     */
    @Override
    public void endUseAbility() {
        host.systemDamage /= damageConstant;
    }
}
