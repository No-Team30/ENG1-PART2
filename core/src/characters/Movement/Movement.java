package characters.Movement;

import characters.Entities.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.json.simple.JSONObject;

public abstract class Movement {
    private final Entity entity;
    public World world;
    public Body b2body;
    public float speed = 60f;

    public Vector2 position;
    protected Vector2 size;


    /**
     * creates an semi-initialised player the physics body is still uninitiated.
     *
     * @param world The game world
     * @param x     The initial x location of the player
     * @param y     The initial y location of the player
     */
    public Movement(Entity entity, World world, float x, float y) {
        this.entity = entity;
        this.world = world;
        this.position = new Vector2(x, y);
        size = new Vector2(24, 24);
        this.speed = 1000.0f;
        this.createBody();

    }

    /**
     * Updates the player, should be called every update cycle.
     *
     * @param delta The time in seconds since the last update
     */
    public abstract Vector2 update(float delta);

    /**
     * Exports all movement based information to a json object(for save games)
     * <p>
     * Including the entity id
     *
     * @return The movement based information
     */
    public abstract JSONObject save();

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
        b2body.createFixture(fdef).setUserData(entity);

        shape.dispose();
    }

    public void setPosition(Vector2 vector) {
        this.position = vector;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

}
