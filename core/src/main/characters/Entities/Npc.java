package characters.Entities;

import characters.Movement.AiMovement;
import characters.Movement.Movement;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONObject;
import screen.LoadGame;

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
        this.movementSystem = new AiMovement(this, world, x, y);
        this.movementSystem.b2body.setUserData("crew" + numberOfCrew);
    }

    /**
     * Builds a new npc from the JSON object
     *
     * @param world  The world to spawn the NPC in
     * @param object The JSON containing the npc data
     */
    public Npc(World world, JSONObject object) {
        super();
        LoadGame.validateAndLoadObject(object, "object_type", "entity");
        LoadGame.validateAndLoadObject(object, "entity_type", "npc");
        this.movementSystem = Movement.loadMovement(this, world, LoadGame.loadObject(object, "movement",
                JSONObject.class));
        this.movementSystem.b2body.setUserData("crew" + numberOfCrew);

    }

    @Override
    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "entity");
        state.put("entity_type", "npc");
        state.put("movement", this.movementSystem.save());
        return state;
    }
}
