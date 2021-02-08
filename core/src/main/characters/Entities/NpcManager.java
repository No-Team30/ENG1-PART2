package characters.Entities;

import characters.Movement.AiMovement;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import screen.LoadGame;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * manage npcs in the game.
 */
public class NpcManager {


    public World world;
    public TiledMap map;
    public ArrayList<Npc> npcs = new ArrayList<>();
    public ArrayList<Vector2> spawnPositions = new ArrayList<>();


    /**
     * Instantiates a new NPC Manager.
     *
     * @param world The game world
     * @param map   the tiled map
     */
    public NpcManager(World world, TiledMap map) {

        this.world = world;
        this.map = map;
        generate_initialPosition(map);
        generateNpc(world);
    }

    /**
     * Loads a new NPC Manager from a JSON Object.
     *
     * @param world  The game world
     * @param object The container for the NPC data
     */
    public NpcManager(World world, JSONObject object) {
        LoadGame.validateAndLoadObject(object, "object_type", "npc_manager");
        this.world = world;
        this.npcs = new ArrayList<>();
        for (Object npcObject : LoadGame.loadObject(object, "npcs", JSONArray.class)) {
            if (!(npcObject instanceof JSONObject)) {
                throw new IllegalArgumentException("npc_manager does not contain npc JSON Object");
            }
            npcs.add(new Npc(world, (JSONObject) npcObject));
        }
        spawnPositions = new ArrayList<>();
        for (Object spawnObject : LoadGame.loadObject(object, "spawn_positions", JSONArray.class)) {
            if (!(spawnObject instanceof JSONObject)) {
                throw new IllegalArgumentException("npc_manager does not contain npc JSON Object");
            }
            JSONObject spawnJSON = (JSONObject) spawnObject;
            LoadGame.validateAndLoadObject(spawnJSON, "object_type", "spawn_position");
            Vector2 spawn = new Vector2(LoadGame.loadObject(spawnJSON, "x_position", Float.class),
                    LoadGame.loadObject(spawnJSON, "y_position", Float.class));
            spawnPositions.add(spawn);
        }
    }

    /**
     * generate random spawn positions for npc.
     *
     * @param map The world map to generate inital position
     */
    public void generate_initialPosition(TiledMap map) {

        MapLayer npcSpawn = map.getLayers().get("npcSpawns");

        while (spawnPositions.size() < 20) {
            for (MapObject object : npcSpawn.getObjects()) {
                Rectangle point = ((RectangleMapObject) object).getRectangle();
                Vector2 position = new Vector2(point.x, point.y);
                double randomPo = Math.random();
                if (randomPo > 0.5 && !spawnPositions.contains(position)) {
                    spawnPositions.add(position);
                }
            }
        }
    }

    /**
     * create NPC in box2D world and set initial destination for npcs.
     *
     * @param world The game world
     */
    public void generateNpc(World world) {

        int destcount = 1;

        for (int i = 0; i < spawnPositions.size(); i++) {

            Vector2 spawn = spawnPositions.get(i);
            // set destination for npc
            if (i == spawnPositions.size() - 1) {
                destcount = 0;
            }
            Vector2 dest = spawnPositions.get(destcount);
            destcount += 1;
            // pic for NPC needed
            Npc npc = new Npc(world, spawn.x, spawn.y);
            ((AiMovement) npc.movementSystem).setDestination(dest.x, dest.y);
            ((AiMovement) npc.movementSystem).moveToDestination();
            npcs.add(npc);

        }

    }

    /**
     * render the npc, should be called in render loop.
     *
     * @param batch the SpriteBatch to draw the npc to
     */
    public void renderNpc(SpriteBatch batch) {
        for (Npc npc : npcs) {
            npc.draw(batch);
        }

    }


    /**
     * update npc, should be called in GamePlay update.
     *
     * @param delta The time in secconds since the last update
     */
    public void updateNpc(float delta) {

        for (Npc npc : npcs) {
            if (!((AiMovement) npc.movementSystem).isMoving()) {
                generateNextPosition(npc);
            }
            npc.update(delta);
        }

    }

    /**
     * Generates the next random position an npc will go to.
     *
     * @param npc the npc to randomly pathfind
     */
    public void generateNextPosition(Npc npc) {
        int index;
        Random random = new Random();
        index = random.nextInt(20);

        Vector2 destination = spawnPositions.get(index);
        ((AiMovement) npc.movementSystem).setDestination(destination);
        ((AiMovement) npc.movementSystem).moveToDestination();
    }

    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "npc_manager");
        state.put("npcs", npcs.stream().map(Entity::save).collect(Collectors.toList()));
        state.put("spawn_positions",
                spawnPositions.stream().map(position -> {
                    JSONObject exportPosition = new JSONObject();
                    exportPosition.put("object_type", "spawn_position");
                    exportPosition.put("x_position", position.x);
                    exportPosition.put("y_position", position.y);
                    return exportPosition;
                }).collect(Collectors.toList()));
        return state;
    }

}
