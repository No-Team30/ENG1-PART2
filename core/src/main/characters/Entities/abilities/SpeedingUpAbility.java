package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to increase speed for enemy.
 */
public class SpeedingUpAbility extends AbsAbility {
    private double speedConstant = 3;

    public SpeedingUpAbility() {
        super();
    }

    public SpeedingUpAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "speed_up");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "speed_up");
        return state;
    }

    /**
     * enemy use ability to increase his speed
     *
     * @param player player
     * @param enemy enemy who speed becomes faster
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {

        enemy.movementSystem.speed *= speedConstant;
    }

    /**
     * enemy speed bonus removed
     *
     * @param enemy enemy who speed back to normal
     */
    @Override
    public void removeAbility(Enemy enemy) {
        enemy.movementSystem.speed /= speedConstant;
    }
}

