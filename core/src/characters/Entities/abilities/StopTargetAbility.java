package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Entity;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to stop player moving.
 */
public class StopTargetAbility extends AbilityBase<Entity, Entity> {
    public StopTargetAbility() {
        useTime = 5;
    }

    public StopPlayerAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "stop_player");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "stop_player");
        return state;
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
