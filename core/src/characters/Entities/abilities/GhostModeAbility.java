package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * (NEW)Create ability to let enemy become invisible
 */
public class GhostModeAbility extends AbsAbility {

    /**
     * (NEW)Enemy to use invisible ability Increase speed for enemy.
     *
     * @param player player
     * @param enemy the enemy who can use invisible ability
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        enemy.ghostMode = true;
        enemy.ghostModeTime = Math.max(10, enemy.ghostModeTime);
    }

    @Override
    public void removeAbility(Enemy enemy) {

    }
}
