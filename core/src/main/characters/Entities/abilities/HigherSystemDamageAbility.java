package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to damage system with higher damage.
 */
public class HigherSystemDamagerAbility extends AbilityBase<Enemy, Enemy> {
    private double damageConstant = 2;

public class HigherSystemDamageAbility extends AbsAbility {
    private final double damageConstant = 2;

    public HigherSystemDamageAbility() {
        super();
    }

    public HigherSystemDamageAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "higher_system_damage");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "higher_system_damage");
        return state;
    }
    /**
     * begin to use ability,Enemy to attack systems with higher damage .
     */
    @Override
    public void beginUseAbility() {
        host.systemDamage *= damageConstant;
    }

    /**
     * Enemy's damage back to normal
     */
    @Override
    public void endUseAbility() {
        host.systemDamage /= damageConstant;
    }
}
