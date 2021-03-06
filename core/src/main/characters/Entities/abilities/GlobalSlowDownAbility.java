package characters.Entities.abilities;

import characters.Entities.Entity;
import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.team3.game.GameMain;
import org.json.simple.JSONObject;
import screen.Gameplay;
import screen.LoadGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Create ability for player which is used to slow down npc and enemies.
 */
public class GlobalSlowDownAbility extends AbilityBase<Player, Entity> {
    public static final float SLOWDOWN = 2f;
    private List<Entity> influenceEntities = new ArrayList<>();

    public GlobalSlowDownAbility() {
        useTime = 5;
        cooldownTime = 60;
    }

    public GlobalSlowDownAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "global_slowdown");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "global_slowdown");
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
        GameMain gameMain = (GameMain) Gdx.app.getApplicationListener();
        if (gameMain == null) return;

        Gameplay gameplay = (Gameplay) gameMain.getScreen();
        if (gameplay == null) return;

        for (Entity entity : Gameplay.getInstance().player.enemyManager.getActiveEnemies()) {
            entity.movementSystem.speed /= SLOWDOWN;
            influenceEntities.add(entity);
        }
    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        for (Entity entity : influenceEntities) {
            entity.movementSystem.speed *= SLOWDOWN;
        }
    }

    @Override
    public String toString() {
        String str = "Slowdown ";
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
