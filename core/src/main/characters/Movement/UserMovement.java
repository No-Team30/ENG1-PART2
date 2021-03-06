package characters.Movement;

import characters.Entities.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONObject;
import screen.LoadGame;
import tools.Controller;

public class UserMovement extends Movement {

    /**
     * creates an semi-initialised player the physics body is still uninitiated.
     *
     * @param world The game world
     * @param x     The initial x location of the player
     * @param y     The initial y location of the player
     */
    public UserMovement(Entity userData, World world, float x, float y) {
        super(userData, world, x, y);
    }


    /**
     * Builds an user controlled movement system from the provided JSON Object
     *
     * @param object The JSON object to load from
     */
    public UserMovement(Entity userData, World world, JSONObject object) {
        super(userData, world, object);
        LoadGame.validateAndLoadObject(object, "movement_type", "user_movement");
    }

    /**
     * Updates the player, should be called every update cycle.
     *
     * @param delta The time in seconds since the last update
     */
    @Override
    public Vector2 update(float delta) {
        Vector2 direction = new Vector2(0, 0);
        if (Controller.isLeftPressed()) {
            direction.add(-speed, 0);
        }
        if (Controller.isRightPressed()) {
            direction.add(speed, 0);
        }
        if (Controller.isUpPressed()) {
            direction.add(0, speed);
        }
        if (Controller.isDownPressed()) {
            direction.add(0, -speed);
        }
        b2body.applyLinearImpulse(direction, b2body.getWorldCenter(), true);

        // position sprite properly within the box
        this.setPosition(b2body.getPosition().x - size.x / 1,
                b2body.getPosition().y - size.y / 1 + 6);
        return direction;
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("movement_type", "user_movement");
        return state;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UserMovement;
    }
}
