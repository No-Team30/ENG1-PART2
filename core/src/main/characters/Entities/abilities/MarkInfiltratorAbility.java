package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability for player which is used to mark one of the closest enemies.
 */
public class MarkInfiltratorAbility extends AbilityBase<Player, Enemy> {

    /**
     * Special Ability Enemy should have.
     */
    public MarkInfiltratorAbility() {
        super();
        useTime = 5;
        cooldownTime = 30;
    }

    public MarkInfiltratorAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "mark_infiltrator");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "mark_infiltrator");
        return state;
    }

    /**
     * begin to use ability
     */
    @Override
    public void beginUseAbility() {
        Enemy enemy = host.enemyManager.getClosestActiveEnemy(host.getPosition());
        if (enemy != null) {
            enemy.beMarked = true;
            target = enemy;
        }
    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        if (target != null){
            target.beMarked = false;
        }
    }

    @Override
    public String toString() {
        String str = "Mark Infiltrators ";
        if (this.inUse()) {
            str += "Using";
        } else if (this.isReady()) {
            str += "Ready";
        } else {
            str += String.format(" CD:%.2f/%.2f", this.getCooldownTimeTiming(), this.getCooldownTime());
        }
        return str;
    }
}
