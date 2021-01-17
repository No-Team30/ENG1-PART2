package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import characters.Movement.AiMovement;

/**
 * (NEW)Create ability to stop player moving.
 */
public class StopPlayerAbility extends AbsAbility {

    /**
     * (NEW)enemy use ability to let player stop moving
     *
     * @param player player who can not move
     * @param enemy enemy
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        player.cantMove = true;
        player.cantMoveTime = Math.max(10, enemy.cantMoveTime);
    }

    @Override
    public void removeAbility(Enemy enemy) {
    }
}
