package characters.Movement;

import characters.Entities.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.json.simple.JSONObject;
import screen.LoadGame;

public abstract class Movement {
    private final Entity userData;
    public World world;
    public Body b2body;
    public float speed;

    private Vector2 position;

    public Vector2 getSize() {
        return size;
    }

    protected Vector2 size;


    /**
     * creates an semi-initialised player the physics body is still uninitiated.
     *
     * @param world The game world
     * @param x     The initial x location of the player
     * @param y     The initial y location of the player
     */
    public Movement(Entity userData, World world, float x, float y) {
        this.userData = userData;
        this.world = world;
        this.position = new Vector2(x, y);
        size = new Vector2(24, 24);
        this.speed = 1000.0f;
        this.createBody();

    }

    Movement(Entity userData, World world, JSONObject object) {
        LoadGame.validateAndLoadObject(object, "object_type", "movement");
        this.world = world;
        this.userData = userData;
        this.position = new Vector2(LoadGame.loadObject(object, "x_position", Float.class), LoadGame.loadObject(object,
                "y_position", Float.class));
        this.size = new Vector2(LoadGame.loadObject(object, "x_size", Float.class), LoadGame.loadObject(object,
                "y_size", Float.class));
        this.speed = LoadGame.loadObject(object, "speed", Float.class);
        this.createBody();
        this.b2body.setUserData(LoadGame.loadObject(object, "user_data", Object.class));
    }

    /**
     * Builds a new movement_type from the JSON Object
     *
     * @param object The JSON Object to build the movement system from
     * @throws IllegalArgumentException if 'movement_type' parameter, does not match any known movement system
     */
    public static Movement loadMovement(Entity userData, World world, JSONObject object) {
        String movementType = LoadGame.loadObject(object, "movement_type", String.class);
        switch (movementType) {
            case "user_movement":
                return new UserMovement(userData, world, object);
            case "ai_movement":
                return new AiMovement(userData, world, object);
            default:
                throw new IllegalArgumentException("movement_type parameter, does not match any known movement " +
                        "types + (" + movementType + ")");
        }
    }


    /**
     * Updates the player, should be called every update cycle.
     *
     * @param delta The time in seconds since the last update
     */
    public abstract Vector2 update(float delta);

    /**
     * Exports all movement based information to a json object(for save games)
     *
     * @return The movement based information
     */
    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "movement");
        state.put("x_position", this.getPosition().x);
        state.put("y_position", this.getPosition().y);
        state.put("speed", this.speed);
        state.put("x_size", this.size.x);
        state.put("y_size", this.size.y);
        state.put("user_data", this.b2body.getUserData());
        return state;
    }

    /**
     * Creates the physics bodies for the player Sprite.
     */
    public void createBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x + size.x, position.y + size.y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / 2, size.y / 2);

        fdef.shape = shape;

        b2body.setLinearDamping(20f);
        b2body.createFixture(fdef).setUserData(this.userData);

        shape.dispose();
    }

    public void setPosition(Vector2 vector) {
        this.position = vector;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public Vector2 getPosition() {
        return this.position;
    }
}
