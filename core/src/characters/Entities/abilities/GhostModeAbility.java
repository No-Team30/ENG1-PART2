package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * Create ability to let enemy become invisible
 */
public class GhostModeAbility extends AbsAbility {
    public Enemy enemy;

    public GhostModeAbility() {
        useTime = 10;
    }

    /**
     * Enemy to use invisible ability .
     *
     * @param player player
     * @param enemy the enemy who can use invisible ability
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        this.enemy = enemy;
        enemy.ghostMode = true;
    }

    /**
     * The enemy is no longer invisible
     *
     * @param enemy the enemy who can use invisible ability
     */
    @Override
    public void removeAbility(Enemy enemy) {
        enemy.ghostMode = false;
    }
}
