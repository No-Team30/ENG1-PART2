package screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.team3.game.GameMain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.FileSystemNotFoundException;

public class LoadGame extends Gameplay {
    public static final String saveName = "SaveGame.json";

    public LoadGame(GameMain game) {
        super(game);
        if (!Gdx.files.isLocalStorageAvailable()) {
            throw new FileSystemNotFoundException("Local file access is unavailable!");
        }
        FileHandle handle = Gdx.files.local(saveName);
        // If no save exists, start a new game
        if (!handle.exists()) {
            game.setScreen(new Gameplay(game));
        }
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(handle.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
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
                    expectedValue + ")");
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
            throw new IllegalArgumentException("Parameter (" + parameterName + ") does not exist in JSON Object");
        }
        if (!(type.isInstance(parameter))) {
            throw new IllegalArgumentException("Parameter: " + parameterName + " is of incorrect type. Expected: " + type +
                    " Received: " + parameter.getClass());
        }
        return (T) parameter;
    }
}
