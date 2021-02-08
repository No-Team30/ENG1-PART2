package screen;

import characters.Entities.NpcManager;
import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.team3.game.GameMain;
import map.Map;
import org.json.simple.JSONObject;
import screen.actors.*;
import sprites.Door;
import sprites.Jail;
import sprites.Systems;
import sprites.Teleport;
import tools.*;

import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Main gameplay object, holds all game data.
 */
public class Gameplay implements Screen {

    public static final int TILE_SIZE = 64;

    private final GameMain game;

    public static ArrayList<Door> doors = new ArrayList<>();

    public static ArrayList<Systems> systems = new ArrayList<>();

    public static Player player;
    /**
     * Whether the game is in demo mode
     */
    private final Boolean isDemo;

    public NpcManager npcManager;

    public OrthographicCamera camera;

    public Viewport viewport;

    public Hud hud;

    public TeleportProcess teleportProcess;

    public HealthBar healthBar;

    public Teleport_Menu teleportMenu;

    public SystemStatusMenu systemStatusMenu;

    public ArrestedHeader arrestedHeader;

    public AbilityFooter abilityFooter;

    private final TmxMapLoader maploader;

    protected final TiledMap map;

    private final OrthogonalTiledMapRenderer renderer;

    private final BackgroundRenderer backgroundRenderer;

    protected final World world;

    private static final int[] backgroundLayers = new int[]{0, 1, 2};

    private boolean paused = false;

    private final LightControl lightControl;

    public Gameplay(GameMain game, Boolean isDemo) {
        this(game, new Vector2(640, 360), isDemo);
        buildEntities();
        buildUI();
    }

    /**
     * Creates a new instantiated game.
     *
     * @param game       The game object used in Libgdx things
     * @param screenSize size of the rendered game screen, doesn't effect screen size
     */
    protected Gameplay(GameMain game, Vector2 screenSize, Boolean isDemo) {
        this.isDemo = isDemo;
        this.game = game;
        // create a box2D world
        this.world = new World(new Vector2(0, 0), true);
        // create map loader for tiled map
        maploader = new TmxMapLoader();
        // load the tiled map
        map = maploader.load("Map/Map.tmx");
        Map.create(map);
        renderer = new OrthogonalTiledMapRenderer(map);
        // load all textures for render
        CharacterRenderer.loadTextures();
        // create a light control object
        lightControl = new LightControl(world);
        // create a new orthographic camera
        camera = new OrthographicCamera();
        // set the viewport area for camera
        viewport = new FitViewport(screenSize.x, screenSize.y, camera);
        // create a new background Render
        backgroundRenderer = new BackgroundRenderer(game.getBatch(), viewport);

    }

    /**
     * Creates all the interactive objects and hooks them into the world physics.
     * Including:
     * Doors, Walls, Jails, Systems, Players, Npcs and Teleport
     */
    public void buildEntities() {
        // create 2d box world for objects , walls, teleport...
        // Get all layers of map
        MapLayers layers = map.getLayers();
        buildWalls(layers);

        // create systems <- this is interactive tiled map object
        systems = new ArrayList<>();
        for (MapObject object : layers.get("systems").getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            // create a new instantiated System object
            // stor system object in the systems Arraylist
            systems.add(new Systems(world, map, rect, object.getName()));
        }

        // Creates the player at the spawn point on the spawn layer of the map
        for (MapObject object : layers.get("spawn").getObjects()) {
            Rectangle point = ((RectangleMapObject) object).getRectangle();
            player = new Player(world, point.x, point.y, map);
            break;
        }
        buildDoors(layers);
        buildTeleports(layers);
        buildJails(layers);

        // create Npc_manager instance
        npcManager = new NpcManager(world, map);
    }

    /**
     * Builds all the door objects, from the provided map layer
     *
     * @param layers The map object containing door positions
     */
    public void buildDoors(MapLayers layers) {
        // create doors <- this is interactive tiled map object
        doors = new ArrayList<>();
        for (MapObject object : layers.get("doors").getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            // create a new instantiated door object
            // adds door object to the Doors Arraylist
            doors.add(new Door(world, map, rect, object.getName().equals("jailDoor")));
        }

    }

    /**
     * Builds all the teleport pads, from the provided map layer
     *
     * @param layers The map object containing teleport positions
     */
    public void buildTeleports(MapLayers layers) {
        //create teleport <- this is interactive tiled map object
        for (MapObject object : layers.get("teleports").getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            // create a new instantiated Teleport object
            new Teleport(world, map, rect, object.getName());
        }
    }

    /**
     * Builds all the jail positions, from the provided map layer
     *
     * @param layers The map object containing jail positions
     */
    public void buildJails(MapLayers layers) {
        // create jails
        int jailNumber = 0;
        for (MapObject object : layers.get("jail").getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Jail(world, map, rect, jailNumber);
            jailNumber++;
        }
    }


    /**
     * Builds all the wall objects, from the provided map layer
     *
     * @param layers The map object containing wall positions
     */
    public void buildWalls(MapLayers layers) {
        Body body;
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        // create the walls
        for (MapObject object : layers.get("walls").getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(rect.getX() + rect.getWidth() / 2,
                    rect.getY() + rect.getHeight() / 2);
            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fdef.shape = shape;
            body.createFixture(fdef).setUserData("walls");
            body.setUserData("walls");
        }
    }

    public void buildUI() {
        // set the contact listener for the world
        world.setContactListener(new ObjectContactListener());
        // create HUD
        hud = new Hud(game.getBatch());
        // teleportMenu
        teleportMenu = hud.teleportMenu;
        // healthBar
        healthBar = hud.healthBar;
        // create a teleport_process instance
        teleportProcess = new TeleportProcess(teleportMenu, player, map);
        // system_status_menu
        systemStatusMenu = hud.systemStatusMenu;
        // generate all systems labels for status menu
        systemStatusMenu.generate_systemLabels(systems);
        // create arrest_status header
        arrestedHeader = hud.arrestedHeader;
        abilityFooter = hud.abilityFooter;
        // create Npc_manager instance
        npcManager = new NpcManager(world, map);

    }

    /**
     * Updates the game, logic will go here called by libgdx GameMain.
     */
    public void update() {

        float delta = Gdx.graphics.getDeltaTime();
        backgroundRenderer.update(delta);
        world.step(delta, 8, 3);
        hud.stage.act(delta);
        player.update(delta);
        teleportProcess.validate();
        healthBar.updateHp(player);
        lightControl.light_update(systems);
        DoorControll.updateDoors(systems, delta);
        player.enemyManager.update(delta, player.getPosition());
        npcManager.updateNpc(delta);
        systemStatusMenu.update_status(systems);
        arrestedHeader.update_Arrested(player);
        abilityFooter.updateAbilities(player);
        // if escape is pressed pause the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.pause();
        }
        checkGameState();

    }

    private static final int[] forgroundLayers = new int[]{3};
    private static boolean isSaveRequested = false;

    public static void requestSave() {
        Gameplay.isSaveRequested = true;
    }

    @Override
    public void show() {
        // set hud to be the input processor
        Gdx.input.setInputProcessor(hud.stage);
    }

    /**
     * Saves the game state into a json file (LoadGame.saveName)
     */
    public void saveGame() {
        Gameplay.isSaveRequested = false;
        System.out.println("Saving game");
        if (!Gdx.files.isLocalStorageAvailable()) {
            throw new FileSystemNotFoundException("Local file access is unavailable!");
        }
        JSONObject state = new JSONObject();
        //state.put("EnemyManager", this.enemyManager.save());
        state.put("npc_manager", this.npcManager.save());
        state.put("player", Gameplay.player.save());
        state.put("systems", systems.stream().map(Systems::save).collect(Collectors.toList()));
        FileHandle handle = Gdx.files.local(LoadGame.saveName);
        handle.writeString(state.toJSONString(), false);
    }


    @Override
    public void resize(int width, int height) {

        viewport.update(width, height);

        hud.resize(width, height);
    }

    /**
     * to pause the game
     * set the paused flag and show the pause menu.
     */
    @Override
    public void pause() {
        this.paused = true;
        this.hud.pauseMenu.show();
    }

    /**
     * to resume the game
     * set the pause flag and hide the pause menu.
     */
    @Override
    public void resume() {
        this.paused = false;
        this.hud.pauseMenu.hide();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    /**
     * check whether game ends.
     */
    public void checkGameState() {

        if (player.enemyManager.hasPlayerWon()) {
            game.setScreen(new WinLoseScreen(game.getBatch(), "YOU WIN!!", this.isDemo));
        }
        int sabotagedCount = 0;
        for (Systems system : systems) {
            if (system.is_sabotaged()) {
                sabotagedCount++;
            }
        }
        if (sabotagedCount >= 15 || player.health <= 1) {
            game.setScreen(new WinLoseScreen(game.getBatch(), "YOU LOSE!!", this.isDemo));
        }

    }

    @Override
    public void render(float delta) {
        // Execute a save, if it is requested
        if (Gameplay.isSaveRequested) {
            this.saveGame();
        }
        // if the game is not paused, update it
        // else if the pause menu indicates resume, resume the game
        // else if the pause menu indicates exit, end the game
        if (!this.paused) {
            update();
        } else if (this.hud.pauseMenu.resume()) {
            resume();
        } else if (this.hud.pauseMenu.exit()) {
            Gdx.app.exit();
        }


        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // set camera follow the player
        camera.position.set(0, 0, 0);
        camera.update();

        // this is needed to be called before the batch.begin(), or screen will freeze
        game.getBatch().setProjectionMatrix(camera.combined);
        viewport.apply();
        backgroundRenderer.render();

        // set camera follow the player
        camera.position.set(player.movementSystem.b2body.getPosition().x, player.movementSystem.b2body.getPosition().y, 0);
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        // render the tilemap background
        renderer.setView(camera);
        renderer.render(backgroundLayers);

        // begin the batch
        game.getBatch().begin();
        // render player
        player.draw(game.getBatch());
        // render Enemies
        player.enemyManager.render(game.getBatch());
        // render NPC
        npcManager.renderNpc(game.getBatch());
        // end the batch
        game.getBatch().end();
        // render tilemap that should appear in front of the player
        renderer.render(forgroundLayers);
        // render the light
        lightControl.rayHandler.render();
        // render the hud
        hud.viewport.apply();
        hud.stage.draw();

    }
}
