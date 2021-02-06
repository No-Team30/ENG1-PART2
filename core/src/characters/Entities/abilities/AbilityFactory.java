package characters.Entities.abilities;

import characters.Entities.Entity;

import java.util.Random;

/**
 * the factory to create different abilities
 */
public class AbilityFactory {

    /**
     * To create new abilities for enemies with random method,could be one of:
     *
     * @see AttackPlayerAbility
     * @see GhostModeAbility
     * @see SlowDownTargetAbility
     * @see SpeedingUpAbility
     * @see StopTargetAbility
     * @see HigherSystemDamagerAbility
     */
    public static AbilityBase<? extends Entity, ? extends Entity> randomAbility() {
        Random random = new Random();
        switch (random.nextInt(6)) {
            case 0:
                return new AttackPlayerAbility();
            case 1:
                return new GhostModeAbility();
            case 2:
                return new SlowDownTargetAbility();
            case 3:
                return new SpeedingUpAbility();
            case 4:
                return new StopTargetAbility();
            default:
                return new HigherSystemDamagerAbility();
        }

    }
}
