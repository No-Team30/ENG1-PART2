package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * (NEW)Create ability to slow down player's speed.
 */
public class SlowDownPlayerAbility extends AbsAbility {
    public static final float SLOWDOWN = 0.5f;

    /**
     * (NEW)enemy use ability to let player speed become slower
     *
     * @param player player's speed become slower
     * @param enemy enemy
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        float currentSpeed = player.movementSystem.speed;
        player.movementSystem.speed = currentSpeed * SLOWDOWN;
    }

    /**
     *(NEW) player's speed back to normal
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
