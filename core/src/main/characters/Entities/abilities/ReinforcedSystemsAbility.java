package characters.Entities.abilities;

import characters.Entities.Entity;
import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.team3.game.GameMain;
import org.json.simple.JSONObject;
import screen.Gameplay;
import screen.LoadGame;
import sprites.Systems;

/**
 * Create ability for player which is used to keep the system from being damaged.
 */
public class ReinforcedSystemsAbility extends AbilityBase<Player, Entity> {

    public ReinforcedSystemsAbility() {
        useTime = 5;
        this.cooldownTime = Float.MAX_VALUE;
    }

    public ReinforcedSystemsAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "reinforced_systems");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "reinforced_systems");
        return state;
    }

    /**
     * begin to use ability
     */
    @Override
    public void beginUseAbility() {
        GameMain gameMain = (GameMain) Gdx.app.getApplicationListener();
        if (gameMain == null) return;

        Gameplay gameplay = (Gameplay) gameMain.getScreen();
        if (gameplay == null) return;

        for (Systems systems : Gameplay.getInstance().systems) {
            systems.setReinforced(true);
        }
    }

    /**
     * end to use ability when ability useTiming is from n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        GameMain gameMain = (GameMain) Gdx.app.getApplicationListener();
        if (gameMain == null) return;

        Gameplay gameplay = (Gameplay) gameMain.getScreen();
        if (gameplay == null) return;

        for (Systems systems : Gameplay.getInstance().systems) {
            systems.setReinforced(false);
        }
    }

    @Override
    public String toString() {
        String str = "Reinforced Systems ";
        if (this.inUse()) {
            str += "Using";}
        else str += this.isReady() ? "Ready" : "Used";
        return str;
    }
}
