package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 *(NEW) Create ability to increase speed for enemy.
 */
public class HigherSystemDamagerAbility extends AbsAbility {
    private float systemDamage = 0;

    /**
     * (NEW)Enemy to attack systems with higher damage .
     *
     * @param player player
     * @param enemy the enemy who can sabotage system with higher damage.
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        systemDamage = enemy.systemDamage;
        enemy.systemDamage = systemDamage * 2;
    }

    @Override
    public void removeAbility(Enemy enemy) {

    }
}
