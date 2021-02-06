package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * Create ability to let enemy become invisible
 */
public class GhostModeAbility extends AbilityBase<Enemy, Enemy> {
    public Enemy enemy;

    public GhostModeAbility() {
        useTime = 5;
        cooldownTime = 5;
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
