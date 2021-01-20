package characters.Entities;

import characters.Entities.abilities.AbilityFactory;
import characters.Entities.abilities.AbsAbility;
import characters.Movement.AiMovement;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONObject;
import sprites.Systems;

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
        createEdgeShape();
        mode = "";
    }

    public Enemy(World world, JSONObject a) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Create an EdgeShape for enemy to sense auber for special ability.
     */
    public void createEdgeShape() {

        EdgeShape sensoringArea = new EdgeShape();
        sensoringArea.set(new Vector2(64, 32), new Vector2(64, -32));
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = sensoringArea;
        fixtureDef.isSensor = true;
        // store ability in sensor userdata to retrieve it in contactListener
        this.movementSystem.b2body.createFixture(fixtureDef).setUserData(this);

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (ability!=null){
            ability.update(delta, this);
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
/*        state.put("entity_type", "enemy");
        state.put("mode", this.mode);
        state.put("ability", this.ability.save());
        state.put("using_ability", this.usingAbility);
        state.put("targetSystem", this.targetSystem.save());
        if (this.currentContactSystem != null) {
            state.put("currentContactSystem", this.currentContactSystem.save());
        }

        state.put("movement", this.movementSystem.save());*/
        return state;
    }

}
