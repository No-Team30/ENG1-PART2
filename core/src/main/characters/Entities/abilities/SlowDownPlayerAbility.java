package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to slow down player's speed.
 */
public class SlowDownPlayerAbility extends AbsAbility {
    public static final float SLOWDOWN = 0.5f;

    public SlowDownPlayerAbility() {
        super();
    }

    public SlowDownPlayerAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "slow_player");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "slow_player");
        return state;
    }

    /**
     * enemy use ability to let player speed become slower
     *
     * @param player player's speed become slower
     * @param enemy  enemy
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        if (target != null) {
            target = player;
            target.movementSystem.speed *= SLOWDOWN;
        }
    }

    /**
     * player's speed back to normal
     *
     * @param enemy enemy
     */
    @Override
    public void removeAbility(Enemy enemy) {
        if (target != null) {
            target.movementSystem.speed /= SLOWDOWN;
        }
    }
}
