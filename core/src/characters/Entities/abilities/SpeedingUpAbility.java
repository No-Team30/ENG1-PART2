package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import characters.Movement.AiMovement;

/**
 * Create ability to increase speed for enemy.
 */
public class SpeedingUpAbility extends AbsAbility {
    float originalSpeed;
    /**
     * enemy use ability to increase his speed
     *
     * @param player player
     * @param enemy enemy who speed becomes faster
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {

        originalSpeed = enemy.movementSystem.speed;
        enemy.movementSystem.speed = originalSpeed * 3f;
    }
    /**
     * enemy speed bonus removed
     *
     * @param enemy enemy who speed back to normal
     */
    @Override
    public void removeAbility(Enemy enemy) {
        enemy.movementSystem.speed = originalSpeed;
    }
}
