package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * (NEW)Create ability to make damage to player.
 */
public class AttackPlayerAbility extends AbsAbility {

    /**
     *(NEW) Enemy to use attack player ability Increase speed for enemy.
     *
     * @param player the player who sould be attacked by enemy ability
     * @param enemy enemy
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        float currentHp = player.health;
        player.health = currentHp - 10f;
    }

    @Override
    public void removeAbility(Enemy enemy) {

    }
}
