package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Entity;
import characters.Entities.Player;
import com.badlogic.gdx.math.Vector2;
import screen.Gameplay;
import tools.CharacterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkInfiltratorAbility extends AbilityBase<Player, Entity> {
    public float markDistance = 1000000000;

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
        }
    }

    /**
     * Use ability,this will be called in update when useTiming > 0 ;
     */
    @Override
    public void useAbility(float delta) {
//        for (Enemy enemy : Gameplay.player.enemyManager.getActiveEnemies()) {
//            int distance = enemy.distanceTo(host);
//            enemy.beMarked = distance < markDistance;
//        }

    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        for (Enemy enemy : Gameplay.player.enemyManager.getActiveEnemies()) {
            int distance = enemy.distanceTo(host);
            enemy.beMarked = false;
        }
    }
}
