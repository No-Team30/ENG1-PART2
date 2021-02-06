package characters.Entities;

import characters.Entities.abilities.AbilityFactory;
import characters.Entities.abilities.AbsAbility;
import characters.Entities.abilities.GhostModeAbility;
import characters.Entities.abilities.SpeedingUpAbility;
import characters.Movement.AiMovement;
import characters.Movement.Movement;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONObject;
import screen.LoadGame;
import sprites.Systems;

import java.util.Random;

public class Enemy extends Entity {

    public Systems targetSystem;
    public Systems currentContactSystem; // used for contact listener
    //TODO WHY NO ENUM?
    public String mode;
    public AbsAbility ability;
    public static int numberOfEnemies;
    public float systemDamage = .1f;
    public boolean ghostMode = false;

    /**
     * Enemy.
     *
     * @param world The game world
     * @param x     position x
     * @param y     position y
     */
    public Enemy(World world, float x, float y) {
        super();
        numberOfEnemies++;
        this.movementSystem = new AiMovement(this, world, x, y);
        this.movementSystem.b2body.setUserData("Enemy" + numberOfEnemies);
        ability = AbilityFactory.randomAbility();
        mode = "";
    }

    Random random = new Random();
    public static double randomUseAbilityRate = 0.5;


    public Enemy(World world, JSONObject object) {
        super();
        LoadGame.validateAndLoadObject(object, "entity_type", "enemy");
        this.mode = LoadGame.loadObject(object, "mode", String.class);
        this.ability = AbsAbility.loadAbility(LoadGame.loadObject(object, "ability", JSONObject.class));
        Object targetSystem = object.get("target_system");
        if (targetSystem instanceof JSONObject) {
            this.targetSystem = Systems.loadFromJSON(world, null, (JSONObject) targetSystem);
        } else {
            this.targetSystem = null;
        }

        this.movementSystem = Movement.loadMovement(this, world, LoadGame.loadObject(object, "movement",
                JSONObject.class));
    }

    /**
     * 1.call super's update
     * 2.if there is a ability for enemy, call the function update of ability to update,
     * used to support the continuous release,class ability, for example: attackPlayerAbility
     * 3.If ability is ready,
     * the ability is used randomly with a probability of randomUseAbilityRate per second.
     *
     * @param delta The time in seconds since the last update
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (ability != null) {
            ability.update(delta, this);
        }
        // random use ability
        if (ability.isReady
                && (ability instanceof GhostModeAbility || ability instanceof SpeedingUpAbility)
                && random.nextDouble() < randomUseAbilityRate * delta) {
            ability.provokeAbility(this, null);
        }
    }

    /**
     * draw the enemy who have ghost mode ability
     *
     * @param batch batch to draw the ghost mode enemy
     */
    @Override
    public void draw(SpriteBatch batch) {
        if (ghostMode) {
            return;
        }
        super.draw(batch);
    }

    /**
     * set sabotage system target.
     *
     * @param system Systems Arraylist
     */
    public void set_target_system(Systems system) {
        targetSystem = system;
    }

    /**
     * Get target system object.
     *
     * @return targeted system
     */
    public Systems get_target_system() {
        return targetSystem;
    }

    /**
     * ability to sabotage the system.
     *
     * @param system system object
     */
    public void sabotage(Systems system) {
        if (system.hp > 0) {
            system.hp -= systemDamage;
        } else {
            system.hp = 0;
            system.set_sabotaged();
        }
    }

    /**
     * set enemy to attcking mode.
     */
    public void set_attackSystemMode() {
        mode = "attacking_system";
    }

    /**
     * set enemy to standby mode.
     */
    public void set_standByMode() {
        mode = "";
    }

    /**
     * check enemy is attcking a system or not.
     *
     * @return true if it is in attacking mode
     */
    public boolean is_attacking_mode() {
        return mode.equals("attacking_system");
    }

    /**
     * check enemy is standby or not.
     *
     * @return true if it is in standby mode
     */
    public boolean is_standBy_mode() {
        return mode.equals("");
    }

    /**
     * set enemy to arrested.
     */
    public void set_ArrestedMode() {
        mode = "arrested";
        ability.setDisable(true);
    }

    /**
     * getter for arrested.
     *
     * @return bool if the enemy is arrested
     */
    public boolean isArrested() {
        return mode.equals("arrested");
    }


    // TO DO
    // Enemies special abilities
    // ...
    @Override
    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "entity");
        state.put("entity_type", "enemy");
        state.put("mode", this.mode);
        state.put("ability", this.ability.save());
        if (this.targetSystem == null) {
            state.put("targetSystem", "");
        } else {
            state.put("targetSystem", this.targetSystem.save());
        }
        if (this.currentContactSystem != null) {
            state.put("currentContactSystem", this.currentContactSystem.save());
        }

        state.put("movement", this.movementSystem.save());
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AiMovement) {
            return super.equals(o);
        }
        return true;
    }
}
