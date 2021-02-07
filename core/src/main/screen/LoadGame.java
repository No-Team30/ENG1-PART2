package screen;

import characters.Entities.NpcManager;
import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.math.Vector2;
import com.team3.game.GameMain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sprites.Systems;

import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;

public class LoadGame extends Gameplay {
    public static final String saveName = "SaveGame.json";

    public LoadGame(GameMain game) {
        super(game, new Vector2(640, 360), false);
        if (!Gdx.files.isLocalStorageAvailable()) {
            throw new FileSystemNotFoundException("Local file access is unavailable!");
        }
        FileHandle handle = Gdx.files.local(saveName);
        // If no save exists, start a new game
        if (!handle.exists()) {
            game.setScreen(new Gameplay(game, false));

        }
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(handle.readString());
            Gameplay.systems = new ArrayList<>();
            for (Object systemObject : LoadGame.loadObject(jsonObject, "systems", JSONArray.class)) {
                if (!(systemObject instanceof JSONObject)) {
                    throw new IllegalArgumentException("system does not contain npc JSON Object");
                }
                JSONObject systemJSON = (JSONObject) systemObject;
                LoadGame.validateAndLoadObject(systemJSON, "object_type", "system");
                Gameplay.systems.add(Systems.loadFromJSON(world, this.map, systemJSON));
            }
            // Rebuild the hud for systems
            //this.systemStatusMenu.generate_systemLabels(Gameplay.systems);
            this.npcManager = new NpcManager(this.world, LoadGame.loadObject(jsonObject, "npc_manager",
                    JSONObject.class));
            player = new Player(this.world, this.map, LoadGame.loadObject(jsonObject, "player",
                    JSONObject.class));

            MapLayers layers = map.getLayers();
            buildWalls(layers);
            buildDoors(layers);
            buildTeleports(layers);
            buildJails(layers);
            // Build UI elements
            buildUI();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to load the given parameterName, from the JSONFILE
     *
     * @param object        The JSONObject to load the value from
     * @param parameterName The name of the parameter to load from the file
     * @param expectedValue What the value should equal
     * @return The value retrieved from the JSONObject (this also should equal the expectedValue)
     * @throws IllegalArgumentException, if it is null, not a string or does not match the expected value
     */
    public static <T> T validateAndLoadObject(JSONObject object, String parameterName,
                                              T expectedValue) {
        T parameter = (T) loadObject(object, parameterName, expectedValue.getClass());
        if (!parameter.equals(expectedValue)) {
            throw new IllegalArgumentException("Parameter (" + parameterName + ") does not equal (" +
                    expectedValue + ")\nObject: " + object);
        }
        return parameter;
    }

    /**
     * Attempts to load the given parameterName, from the JSONFILE
     *
     * @param object        The JSONObject to load the value from
     * @param parameterName The name of the parameter to load from the file
     * @return The value retrieved from the JSONObject
     * @throws IllegalArgumentException, if the value is null, or does not match the expected type
     */
    public static <T> T loadObject(JSONObject object, String parameterName, Class<T> type) {
        Object parameter = object.get(parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter (" + parameterName + ") does not exist in JSON Object\nObject: " + object);
        }
        if (!(type.isInstance(parameter))) {
            // As when exporting to JSON Floats can be converted to Doubles, and vide versa
            if (type == Float.class && parameter.getClass() == Double.class) {
                return (type.cast((((Double) parameter).floatValue())));
            }
            if (type == Double.class && parameter.getClass() == Float.class) {
                return (type.cast((((Float) parameter).doubleValue())));
            }
            if (type == Integer.class && parameter.getClass() == Long.class) {
                return (type.cast((((Long) parameter).intValue())));
            }
            if (type == Long.class && parameter.getClass() == Integer.class) {
                return (type.cast((((Integer) parameter).longValue())));
            }
            throw new IllegalArgumentException("Parameter: " + parameterName + " is of incorrect type. Expected: " + type +
                    " Received: " + parameter.getClass());
        }
        return (T) parameter;
    }
}
