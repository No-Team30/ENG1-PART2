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
}
