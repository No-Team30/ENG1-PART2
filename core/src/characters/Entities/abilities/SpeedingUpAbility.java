package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import characters.Movement.AiMovement;

/**
 * Create ability to increase speed for enemy.
 */
public class SpeedingUpAbility extends AbsAbility {
    private double speedConstant = 3;
    /**
     * enemy use ability to increase his speed
     *
     * @param player player
     * @param enemy enemy who speed becomes faster
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {

        enemy.movementSystem.speed *= speedConstant;
    }
    /**
     * enemy speed bonus removed
     *
     * @param enemy enemy who speed back to normal
     */
    @Override
    public void removeAbility(Enemy enemy) {
        enemy.movementSystem.speed /= speedConstant;
    }
}

