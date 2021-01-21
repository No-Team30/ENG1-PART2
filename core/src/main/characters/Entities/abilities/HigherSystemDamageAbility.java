package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to damage system with higher.
 */
public class HigherSystemDamageAbility extends AbsAbility {
    private float systemDamage = 0;

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
     * Enemy to attack systems with higher damage .
     *
     * @param player player
     * @param enemy  the enemy who can sabotage system with higher damage.
     */
    @Override
    public void useAbility(Enemy enemy, Player player) {
        systemDamage = enemy.systemDamage;
        enemy.systemDamage = systemDamage * 2;
    }

    /**
     * Enemy's damage back to normal
     *
     * @param enemy the enemy whose high damage becomes to normal
     */
    @Override
    public void removeAbility(Enemy enemy) {
        enemy.systemDamage = systemDamage * 0.5f;
    }
}
