package characters.Entities;

import characters.Movement.AiMovement;
import com.badlogic.gdx.physics.box2d.World;

public class Npc extends Entity {
    public static int numberOfCrew;

    /**
     * NPC object.
     *
     * @param world The game world
     * @param x     the initial spawn position x
     * @param y     the initial spawn position y
     */
    public Npc(World world, float x, float y) {
        super();
        numberOfCrew++;
        this.movementSystem = new AiMovement(this,world, x, y);
        this.movementSystem.b2body.setUserData("crew" + numberOfCrew);
    }
}
