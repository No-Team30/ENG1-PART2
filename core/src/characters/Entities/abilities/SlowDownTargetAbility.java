package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Entity;
import characters.Entities.Player;

/**
 * Create ability to slow down player's speed.
 */
public class SlowDownTargetAbility extends AbilityBase<Entity, Entity> {
    public static final float SLOWDOWN = 0.5f;

    /**
     * begin to use ability
     */
    @Override
    public void beginUseAbility() {
        if (target != null) {
            target.movementSystem.speed *= SLOWDOWN;
        }
    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        if (target != null) {
            target.movementSystem.speed /= SLOWDOWN;
        }
    }
}
