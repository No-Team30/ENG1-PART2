package characters.Entities.abilities;

import characters.Entities.Entity;
import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.team3.game.GameMain;
import screen.Gameplay;

/**
 * Create ability for player which is used to slow down npc and enemies.
 */
public class GlobalSlowDownAbility extends AbilityBase<Player, Entity> {
    public static final float SLOWDOWN =2f;

    public GlobalSlowDownAbility() {
        useTime = 5;
        cooldownTime = 60;
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

        for (Entity entity : gameplay.player.enemyManager.getActiveEnemies()) {
            entity.movementSystem.speed /= SLOWDOWN;
        }
        for (Entity entity : gameplay.npcManager.npcs) {
            entity.movementSystem.speed /= SLOWDOWN;
        }
    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        GameMain gameMain = (GameMain) Gdx.app.getApplicationListener();
        if (gameMain == null) return;

        Gameplay gameplay = (Gameplay) gameMain.getScreen();
        if (gameplay == null) return;

        for (Entity entity : gameplay.player.enemyManager.getActiveEnemies()) {
            entity.movementSystem.speed *= SLOWDOWN;
        }
        for (Entity entity : gameplay.npcManager.npcs) {
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
