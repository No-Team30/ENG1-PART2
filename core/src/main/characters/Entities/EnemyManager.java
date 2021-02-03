package characters.Entities;

import characters.Movement.AiMovement;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import screen.Gameplay;
import screen.LoadGame;
import sprites.Systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static screen.Gameplay.TILE_SIZE;


/**
 * Manage enemies in the game.
 */
public class EnemyManager {

    public World world;
    /**
     * Enemies that are wandering around damaging systems
     */
    private final ArrayList<Enemy> activeEnemies;
    /**
     * Enemies that are in jail
     */
    private final ArrayList<Enemy> jailedEnemies;
    /**
     * Enemies that are following the Player
     */
    private final ArrayList<Enemy> arrestedEnemies;
    public ArrayList<float[]> spawn_position = new ArrayList<>();
    /**
     * Presumably a map containing which systems are targeted by which enemy
     **/
    //public static HashMap<Systems, Enemy> information;
    public ArrayList<Vector2> availableJailPositions;
    private float timeSinceEnemyTargetUpdated;

    /**
     * EnemyManager to manage enemies behavior.
     *
     * @param world box2D world
     * @param map   Tiled map
     */
    public EnemyManager(World world, TiledMap map) {
        this.world = world;
        this.availableJailPositions = generate_jail_positions(map);
        this.timeSinceEnemyTargetUpdated = 0;
        //information = new HashMap<>();
        this.activeEnemies = new ArrayList<>();
        this.arrestedEnemies = new ArrayList<>();
        this.jailedEnemies = new ArrayList<>();
        generate_spawn_position(map);
        generate_enemy(world);
    }

    public EnemyManager(World world, JSONObject object) {
        LoadGame.validateAndLoadObject(object, "object_type", "enemy_manager");
        this.world = world;

        this.activeEnemies = new ArrayList<>();
        for (Object enemyObject : LoadGame.loadObject(object, "active_enemies", JSONArray.class)) {
            if (!(enemyObject instanceof JSONObject)) {
                throw new IllegalArgumentException("Active enemies does not contain enemy JSON Object");
            }
            Enemy enemy = new Enemy(world, (JSONObject) enemyObject);
            generateNextTarget(enemy);
            activeEnemies.add(enemy);
        }

        this.arrestedEnemies = new ArrayList<>();
        for (Object enemyObject : LoadGame.loadObject(object, "arrested_enemies", JSONArray.class)) {
            if (!(enemyObject instanceof JSONObject)) {
                throw new IllegalArgumentException("Active enemies does not contain enemy JSON Object");
            }
            Enemy enemy = new Enemy(world, (JSONObject) enemyObject);
            enemy.ability.setDisable(true);
            enemy.set_ArrestedMode();
            arrestedEnemies.add(enemy);
        }

        this.jailedEnemies = new ArrayList<>();
        for (Object enemyObject : LoadGame.loadObject(object, "jailed_enemies", JSONArray.class)) {
            if (!(enemyObject instanceof JSONObject)) {
                throw new IllegalArgumentException("Active enemies does not contain enemy JSON Object");
            }
            Enemy enemy = new Enemy(world, (JSONObject) enemyObject);
            enemy.ability.setDisable(true);
            enemy.set_ArrestedMode();
            enemy.targetSystem = null;
            ((AiMovement) enemy.movementSystem).stop();
            jailedEnemies.add(enemy);
        }

        this.availableJailPositions = new ArrayList<>();
        for (Object jailObject : LoadGame.loadObject(object, "available_jail_positions", JSONArray.class)) {
            if (!(jailObject instanceof JSONObject)) {
                throw new IllegalArgumentException("Active enemies does not contain enemy JSON Object");
            }
            LoadGame.validateAndLoadObject((JSONObject) jailObject, "object_type", "jail_position");
            Float x = LoadGame.loadObject((JSONObject) jailObject, "x_position", Float.class);
            Float y = LoadGame.loadObject((JSONObject) jailObject, "y_position", Float.class);
            this.availableJailPositions.add(new Vector2(x, y));
        }
    }

    public static ArrayList<Vector2> generate_jail_positions(TiledMap map) {
        ArrayList<Vector2> positions = new ArrayList<>();
        MapLayers layers = map.getLayers();
        for (MapObject object : layers.get("jail").getObjects()) {
            Rectangle jail = ((RectangleMapObject) object).getRectangle();
            positions.add(new Vector2(jail.x, jail.y));
        }
        return positions;
    }

    /**
     * generate random start position for enemies.
     *
     * @param map TiledMap object
     */
    public void generate_spawn_position(TiledMap map) {

        MapLayer enemySpawn = map.getLayers().get("npcSpawns");
        while (spawn_position.size() < 8) {
            for (MapObject object : enemySpawn.getObjects()) {
                Rectangle point = ((RectangleMapObject) object).getRectangle();
                float[] position = new float[]{point.x, point.y};
                double randomPro = Math.random();
                if (randomPro > 0.5 && !spawn_position.contains(position)) {
                    spawn_position.add(position);
                }
            }
        }

    }

    /**
     * create Enemy instance and store in Arraylist enemy.
     *
     * @param world World object
     */
    public void generate_enemy(World world) {
        for (int i = 0; i < 8; i++) {
            float[] position = spawn_position.get(i);
            // pic needs to be changed with enemy pic
            Enemy enemy = new Enemy(world, position[0], position[1]);
            generateNextTarget(enemy);
            activeEnemies.add(enemy);

        }

    }

    /**
     * render the enemy, should be called in gameplay render loop.
     *
     * @param batch SpriteBatch used in game
     */
    public void render(SpriteBatch batch) {
        for (Enemy enemy : activeEnemies) {
            enemy.draw(batch);
        }
        for (Enemy enemy : arrestedEnemies) {
            enemy.draw(batch);
        }
        for (Enemy enemy : jailedEnemies) {
            enemy.draw(batch);
        }

    }


    /**
     * update the enemy, should be called in gameplay update.
     *
     * @param delta The time in secconds since the last update
     */
    public void update(float delta, Vector2 playerPosition) {
        for (Enemy enemy : jailedEnemies) {
            enemy.update(delta);
        }
        // If we are at the jail, put arrested enemies in jail (Within 3 squares distance of the jail)
        if (haveEnemiesBeenArrested()) {
            if (this.availableJailPositions.size() == 0) {
                // TODO Change the error type
                throw new RuntimeException("There are no available jail positions!");
            }
            if (playerPosition.dst(this.availableJailPositions.get(0)) < TILE_SIZE * 2) {
                this.jailAllArrestedEnemies();
            } else {
                for (Enemy enemy : arrestedEnemies) {
                    if (enemy.get_target_system() != null) {
                        // remove it from information for other enemies to target that system.
                        if (enemy.get_target_system().is_not_sabotaged()) {
                            enemy.get_target_system().setTargetedByEnemy(false);
                            enemy.targetSystem = null;
                        }
                    }
                    this.timeSinceEnemyTargetUpdated += delta;
                    // Refreshes the target position (of the player) every second
                    if (this.timeSinceEnemyTargetUpdated > 1) {
                        ((AiMovement) enemy.movementSystem).setDestination(playerPosition);
                        ((AiMovement) enemy.movementSystem).moveToDestination();
                        this.timeSinceEnemyTargetUpdated = 0;
                    }
                    enemy.update(delta);
                }
            }
        }
        // Try and use special ability, otherwise try and damage systems
        for (Enemy enemy : activeEnemies) {
            enemy.update(delta);
            if (enemy.ability != null && !enemy.ability.inUse) {

                // get targeted system object
                Systems sys = enemy.get_target_system();
                // TODO If no system left to sabotage, should start attacking Auber
                if (sys == null) {
                    // Still have systems not sabotaged, should keep generating next target
                    generateNextTarget(enemy);

                } else {
                    if (enemy.is_attacking_mode()) {
                        // Damage system
                        enemy.sabotage(sys);
                    }
                    if (sys.is_sabotaged()) {
                        // generate next traget if system sabotaged
                        generateNextTarget(enemy);
                        System.out.println("Enemy: " + enemy + " is now targeting: " + enemy.get_target_system());
                    }
                }
            }
        }

    }

    /**
     * If enemy successfully sabotage one target, generate next target for it.
     *
     * @param enemy enemy object
     */
    // Appears to find the first system not in information?, and sets that as the enemies target?
    public void generateNextTarget(Enemy enemy) {
        // Randomise the order systems are assigned
        List<Integer> systemOrder =
                IntStream.rangeClosed(0, Gameplay.systems.size() - 1).boxed().collect(Collectors.toList());
        Collections.shuffle(systemOrder);
        for (int index : systemOrder) {
            Systems system = Gameplay.systems.get(index);
            if (!system.isTargetedByEnemy() && !system.is_sabotaged()) {
                float endx = system.getposition()[0];
                float endy = system.getposition()[1];
                ((AiMovement) enemy.movementSystem).setDestination(endx, endy);
                enemy.set_target_system(system);
                system.setTargetedByEnemy(true);
                ((AiMovement) enemy.movementSystem).moveToDestination();
                // set enemy back to standBy mode before it contacts with the next target system,
                // otherwise the system will lose HP before contact
                enemy.set_standByMode();
                return;
            }
        }
        // if there is no systems left for sabotaging,
        // set enemy to standby mode and remove the target system
        enemy.set_standByMode();
        enemy.targetSystem = null;
    }

    /**
     * This moves all enemies that are arrested (following the Player), into the jail
     * And removes the target system for each enemy
     */
    public void jailAllArrestedEnemies() {
        while (this.haveEnemiesBeenArrested()) {
            Enemy enemy = this.arrestedEnemies.remove(0);
            enemy.targetSystem = null;
            if (this.availableJailPositions.size() == 0) {
                throw new RuntimeException("Not enough jail positions for enemies!");
            }
            enemy.movementSystem.b2body.setTransform(this.availableJailPositions.remove(0), 0);
            ((AiMovement) enemy.movementSystem).stop();
            this.jailedEnemies.add(enemy);
        }
    }

    /**
     * @return True, if one or more enemies have been arrested and are following the player
     */
    public boolean haveEnemiesBeenArrested() {
        return this.arrestedEnemies.size() > 0;
    }


    /**
     * Returns the active enemy that is closest to the given position
         * Could be null, if there are no active enemies
     *
     * @throws NullPointerException
     * @param position The position of the enemy
     */
    public Enemy getClosestActiveEnemy(Vector2 position) {
        Enemy closest_enemy = null;
        int closest_distance = Integer.MAX_VALUE;
        for (Enemy enemy : this.activeEnemies) {
            int distance = enemy.distanceTo(position);
            if (distance < closest_distance) {
                closest_distance = distance;
                closest_enemy = enemy;
            }
        }
        return closest_enemy;
    }

    /**
     * This will cause the given enemy, to become "arrested" which means they follow the Player around, until the player goes to the jail
     * <p>
     * This is done, by moving the enemy from "activeEnemies" to "arrestedEnemies"
     *
     * @param enemy The enemy object to arrest
     */
    public void arrestEnemy(Enemy enemy) {
        if (!this.activeEnemies.contains(enemy)) {
            return;
        }
        System.out.println("Arresting enemy: " + enemy);
        this.arrestedEnemies.add(enemy);
        this.activeEnemies.remove(enemy);
        enemy.ability.setDisable(true);
        // stop enemy's sabotaging if it does
        enemy.set_ArrestedMode();
        // set enemy destination to auber's left,enemy should follow auber until it is in jail
        ((AiMovement) enemy.movementSystem).stop();

    }

    public boolean hasPlayerWon() {
        return this.activeEnemies.isEmpty() && this.arrestedEnemies.isEmpty();
    }

    public Vector2 getJailPosition() {
        return this.availableJailPositions.get(0);
    }

    public int getJailedCount() {
        return this.jailedEnemies.size();
    }

    public int getArrestedCount() {
        return this.arrestedEnemies.size();
    }

    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "enemy_manager");
        state.put("active_enemies", activeEnemies.stream().map(Entity::save).collect(Collectors.toList()));
        state.put("arrested_enemies", arrestedEnemies.stream().map(Entity::save).collect(Collectors.toList()));
        state.put("jailed_enemies", jailedEnemies.stream().map(Entity::save).collect(Collectors.toList()));
        JSONArray jailPositions = new JSONArray();
        for (Vector2 coords : this.availableJailPositions) {
            JSONObject position = new JSONObject();
            position.put("object_type", "jail_position");
            position.put("x_position", coords.x);
            position.put("y_position", coords.y);
            jailPositions.add(position);
        }
        state.put("available_jail_positions", jailPositions);
        return state;
    }
}
