package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * Create ability to slow down player's speed.
 */
public class SlowDownPlayerAbility extends AbsAbility {
    public static final float SLOWDOWN = 0.5f;

    /**
     * enemy use ability to let player speed become slower
     *
     * @param player player's speed become slower
     * @param enemy enemy
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        target = player;
        float currentSpeed = target.movementSystem.speed;
        target.movementSystem.speed = currentSpeed * SLOWDOWN;
    }

    /**
     * player's speed back to normal
     *
     * @param enemy enemy
     */
    @Override
    public void removeAbility(Enemy enemy) {
        if (target != null) {
            target.movementSystem.speed /= SLOWDOWN;
        }
    }
}
