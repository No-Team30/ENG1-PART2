package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import characters.Movement.AiMovement;

/**
 * (NEW)Create ability to increase speed for enemy.
 */
public class SpeedingUpAbility extends AbsAbility {

    /**
     * (NEW)enemy use ability to increase his speed
     *
     * @param player player
     * @param enemy enemy who speed becomes faster
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        float currentSpeed = enemy.movementSystem.speed;
        enemy.movementSystem.speed = currentSpeed * 3f;
        if (target != null) {
            ((AiMovement) enemy.movementSystem).setDestination(target.movementSystem.b2body.getPosition().x - 400, target.movementSystem.b2body.getPosition().y);
        }
    }
    /**
     * (NEW)enemy speed bonus removed
     *
     * @param enemy enemy who speed back to normal
     */
    @Override
    public void removeAbility(Enemy enemy) {
        enemy.movementSystem.speed /= 3f;
    }
}
