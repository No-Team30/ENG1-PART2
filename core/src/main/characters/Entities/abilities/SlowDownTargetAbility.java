package characters.Entities.abilities;

import characters.Entities.Entity;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to slow down player's speed.
 */
public class SlowDownTargetAbility extends AbilityBase<Entity, Entity> {
    public static final float SLOWDOWN = 0.5f;

    public SlowDownTargetAbility() {
        super();
    }

    public SlowDownTargetAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "slow_target");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "slow_target");
        return state;
    }

    /**
     * begin to use ability
     */
    @Override
    public void beginUseAbility() {
        if (target != null) {
            target.movementSystem.speed *= SLOWDOWN;
        }
    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        if (target != null) {
            target.movementSystem.speed /= SLOWDOWN;
        }
    }
}
