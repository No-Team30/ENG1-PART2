package characters.Entities.abilities;

import characters.Entities.Enemy;
import characters.Entities.Player;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * Create ability to make damage to player.
 */
public class AttackPlayerAbility extends AbsAbility {
    public final float DAMAGE = 10f;
    public boolean contact = false;
    /**
     * This time is used to scale the damage applied to Auber,
     * by the time elapsed since damage was last applied.
     * Ensuring that the total damage applied。
     */
    protected float  timeElapsedSinceDamageApplied = 0f;

    public AttackPlayerAbility() {
        useTime = 30f;
    }


    public AttackPlayerAbility(JSONObject object) {
        super(object);
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "attack_player");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "attack_player");
        return state;
    }

    /**
     * Determining whether to use attack ability.
     *
     * @param player the player who would be attacked by enemy
     * @param enemy  enemy who can attack the player
     */
    @Override
    public void provokeAbility(Enemy enemy, Player player) {
        if (isDisabled) return;
        contact = true;
        super.provokeAbility(enemy, player);
    }

    /**
     * Update the usage time of attacking ability
     *
     * @param delta the time elapsed since this function was last called
     * @param enemy  enemy who can attack the player
     */
    @Override
    public void update(float delta, Enemy enemy) {
        super.update(delta, enemy);
        if (isDisabled) return;
        if (contact && useTime > 0) {
            this. timeElapsedSinceDamageApplied += delta;
            useAbility(enemy, target);
        }
    }

    /**
     * Enemy to use attack player ability .
     *
     * @param player the player who sould be attacked by enemy ability
     * @param enemy  enemy who can attack the player
     */
    public void useAbility(Enemy enemy, Player player) {
        target = player;
        if (isDisabled) return;
        if ( timeElapsedSinceDamageApplied <= 0) return;

        target.health -= DAMAGE *  timeElapsedSinceDamageApplied;
        timeElapsedSinceDamageApplied = 0;
    }

    @Override
    public void removeAbility(Enemy enemy) {
    }

}
