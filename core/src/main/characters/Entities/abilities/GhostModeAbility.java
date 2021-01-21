package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to let enemy become invisible
 */
public class GhostModeAbility extends AbsAbility {
    public Enemy enemy;

    public GhostModeAbility() {
        useTime = 10;
    }


    public GhostModeAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "ghost_mode");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "ghost_mode");
        return state;
    }

    /**
     * Enemy to use invisible ability .
     *
     * @param player player
     * @param enemy  the enemy who can use invisible ability
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
