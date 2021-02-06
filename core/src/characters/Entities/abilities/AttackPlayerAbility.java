package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;

/**
 * Create ability to make damage to player.
 */
public class AttackPlayerAbility extends AbilityBase<Enemy, Player> {
    public final float DAMAGE = 10f;
    public boolean contact = false;
    /**
     * This time is used to scale the damage applied to Auber,
     * by the time elapsed since damage was last applied.
     * Ensuring that the total damage applied。
     */
    protected float timeElapsedSinceDamageApplied = 0f;

    public AttackPlayerAbility() {
        useTime = 30f;
    }

    /**
     * Determining whether to use attack ability.
     */
    @Override
    public void tryUseAbility() {
        if (isDisabled) return;
        contact = true;
        super.tryUseAbility();
    }

    /**
     * Update the usage time of attacking ability
     *
     * @param delta the time elapsed since this function was last called
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (isDisabled) return;
        if (contact && useTime > 0) {
            this.timeElapsedSinceDamageApplied += delta;
        }
    }

    /**
     * Enemy to use attack player ability .
     */
    @Override
    public void useAbility(float delta) {
        super.useAbility(delta);
        if (isDisabled) return;

        if (timeElapsedSinceDamageApplied <= 0) return;

        target.health -= DAMAGE * timeElapsedSinceDamageApplied;
        timeElapsedSinceDamageApplied = 0;
    }
}
