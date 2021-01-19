package characters.Entities;

import characters.Movement.Movement;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tools.CharacterRenderer;

/**
 * Main player object for the game.
 */
public abstract class Entity {
    public boolean cantMove = false;
    public float cantMoveTime = 0;
    public boolean ghostMode = false;
    public float ghostModeTime = 0;

    public Movement movementSystem;
    protected CharacterRenderer renderer;
    public static int numberOfEntities;
    private static final CharacterRenderer.Sprite[] sprites = new CharacterRenderer.Sprite[]{
            CharacterRenderer.Sprite.NPC1,
            CharacterRenderer.Sprite.NPC2,
            CharacterRenderer.Sprite.NPC3
    };

    /**
     * creates an semi-initialised player the physics body is still uninitiated.
     *
     * @param sprite The sprite the character should look like
     */
    public Entity(CharacterRenderer.Sprite sprite) {
        renderer = new CharacterRenderer(sprite);
        numberOfEntities += 1;
    }


    public Entity() {
        renderer = new CharacterRenderer(sprites[numberOfEntities % 3]);
        numberOfEntities += 1;
    }

    /**
     * (NEW)Updates the position and animation of the entity.<br>
     * Should be called every update cycle.
     *
     * @param delta The time in seconds since the last update
     */
    public void update(float delta) {
        if (cantMoveTime > 0) {
            cantMoveTime -= delta;
        }
        if (cantMoveTime <= 0) {
            cantMoveTime = 0;
            cantMove = false;
        }

        if (ghostModeTime > 0) {
            ghostModeTime -= delta;
        }
        if (ghostModeTime <= 0) {
            ghostModeTime = 0;
            ghostMode = false;
        }

        if (cantMove) return;

        Vector2 direction = this.movementSystem.update(delta);
        renderer.update(delta, direction);
    }

    /**
     * (NEW)draw the position and animation of the entity and some infiltrators who have ghost mode
     *
     * @param batch batch
     */
    public void draw(SpriteBatch batch) {

        if (ghostMode) {
            return;
        }
        renderer.render(this.getPosition(), batch);
    }

    /**
     * @return The Vector2 coordinate of the entity
     */
    public Vector2 getPosition() {
        return this.movementSystem.getPosition();
    }

    /**
     * Calculates the distance from this entity to the given coordinates
     *
     * @param other The coordinates to get the distance to
     * @return The integer distance
     */
    public int distanceTo(Vector2 other) {
        return (int) this.getPosition().dst(other);
    }

    /**
     * Calculates the distance from this entity to the other entity
     *
     * @param other The entity to calculate the distance to
     * @return The integer distance
     */
    public int distanceTo(Entity other) {
        return (int) this.getPosition().dst(other.getPosition());
    }
}
