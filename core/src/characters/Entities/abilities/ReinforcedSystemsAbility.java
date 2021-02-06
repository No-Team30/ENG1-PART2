package characters.Entities.abilities;

import characters.Entities.Entity;
import characters.Entities.Player;
import screen.Gameplay;
import sprites.Systems;

public class ReinforcedSystemsAbility extends AbilityBase<Player, Entity> {

    public ReinforcedSystemsAbility() {
        useTime = 5;
        this.cooldownTime = Float.MAX_VALUE;
    }

    /**
     * begin to use ability
     */
    @Override
    public void beginUseAbility() {
        for (Systems systems : Gameplay.systems) {
            systems.reinforced = true;
        }
    }

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    @Override
    public void endUseAbility() {
        for (Systems systems : Gameplay.systems) {
            systems.reinforced = false;
        }
    }
}
