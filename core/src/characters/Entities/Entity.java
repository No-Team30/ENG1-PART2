package characters.Entities;

import characters.Movement.Movement;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import tools.CharacterRenderer;

/**
 * Main player object for the game.
 */
public abstract class Entity {
    public Movement movementSystem;
    protected CharacterRenderer renderer;
    public static int numberOfEntities;
    private static CharacterRenderer.Sprite[] sprites = new CharacterRenderer.Sprite[]{
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
     * Updates the position and animation of the entity.<br>
     * Should be called every update cycle.
     *
     * @param delta The time in seconds since the last update
     */
    public void update(float delta) {
        Vector2 direction = this.movementSystem.update(delta);
        renderer.update(delta, direction);
    }

    public void draw(SpriteBatch batch) {
        renderer.render(this.movementSystem.position, batch);
    }
}
