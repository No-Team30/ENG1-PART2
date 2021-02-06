package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Entity;
import characters.Entities.Player;
import characters.Movement.AiMovement;

/**
 * Create ability to increase speed for enemy.
 */
public class SpeedingUpAbility extends AbilityBase<Entity, Entity> {
    public double speedConstant = 3;
    /**
     * enemy use ability to increase his speed
     */
    @Override
    public void beginUseAbility() {
        host.movementSystem.speed *= speedConstant;
    }
    /**
     * enemy speed bonus removed
     */
    @Override
    public void endUseAbility() {
        host.movementSystem.speed /= speedConstant;
    }
}

