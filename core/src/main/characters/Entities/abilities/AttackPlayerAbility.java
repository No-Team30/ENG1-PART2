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
    protected float deltaChanged = 0f;

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
     * @param player the player who sould be attacked by enemy
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
     * @param delta delta time to update the use time
     * @param enemy  enemy who can attack the player
     */
    @Override
    public void update(float delta, Enemy enemy) {
        super.update(delta, enemy);
        if (isDisabled) return;
        if (contact && useTime > 0) {
            this.deltaChanged += delta;
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
        if (deltaChanged <= 0) return;

        float currentHp = target.health;
        float damage = DAMAGE * deltaChanged;

        target.health = currentHp - damage;
        deltaChanged = 0;
    }

}
