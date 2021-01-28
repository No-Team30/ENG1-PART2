package characters.Entities.abilities;

import java.util.Random;

/**
 *  the factory to create different abilities
 */
public class AbilityFactory {

    /**
     * To create new abilities for enemies with random method,could be one of:
     * @see AttackPlayerAbility
     * @see GhostModeAbility
     * @see SlowDownPlayerAbility
     * @see SpeedingUpAbility
     * @see StopPlayerAbility
     * @see HigherSystemDamagerAbility
     *
     */
    public static AbsAbility randomAbility() {
        Random random = new Random();
        switch (random.nextInt(6)) {
            case 0:
                return new AttackPlayerAbility();
            case 1:
                return new GhostModeAbility();
            case 2:
                return new SlowDownPlayerAbility();
            case 3:
                return new SpeedingUpAbility();
            case 4:
                return new StopPlayerAbility();
            case 5:
                return new HigherSystemDamagerAbility();
        }
        return null;
    }
}
