package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Entity;
import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.team3.game.GameMain;
import screen.Gameplay;
import tools.CharacterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
