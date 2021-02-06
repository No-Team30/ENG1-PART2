package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Entity;
import characters.Entities.Player;
import characters.Movement.AiMovement;

/**
 * Create ability to stop player moving.
 */
public class StopTargetAbility extends AbilityBase<Entity, Entity> {
    public StopTargetAbility() {
        useTime = 5;
    }

    /**
     * begin to use ability,Entity use ability to let target stop moving
     */
    @Override
    public void beginUseAbility() {
        target.cantMove = true;
        target.cantMoveTime += useTime;
    }

    /**
     * target can start to move when can't move time is over
     */
    @Override
    public void endUseAbility() {
        target.cantMoveTime -= useTime;
        if (target.cantMoveTime <= 0) {
            target.cantMoveTime = 0;
            target.cantMove = false;
        }
    }
}
