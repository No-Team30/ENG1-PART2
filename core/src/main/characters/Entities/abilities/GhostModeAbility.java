package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to let enemy become invisible
 */
public class GhostModeAbility extends AbilityBase<Enemy, Enemy> {
    public Enemy enemy;

    public GhostModeAbility() {
        useTime = 5;
        cooldownTime = 5;
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
     * Enemy begin to use invisible ability .
     */
    @Override
    public void beginUseAbility() {
        this.host.ghostMode = true;
    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        this.host.ghostMode = false;
    }
}
