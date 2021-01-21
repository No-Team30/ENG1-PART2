package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to stop player moving.
 */
public class StopPlayerAbility extends AbsAbility {
    public StopPlayerAbility() {
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
     * enemy use ability to let player stop moving
     *
     * @param player player who can not move
     * @param enemy  enemy
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        target = player;
        target.cantMove = true;
        target.cantMoveTime += useTime;

    }

    /**
     * player can start to move when can't move time is over
     *
     * @param enemy enemy
     */
    @Override
    public void removeAbility(Enemy enemy) {
        target.cantMoveTime -= useTime;
        if (target.cantMoveTime <= 0) {
            target.cantMoveTime = 0;
            target.cantMove = false;
        }
    }
}
