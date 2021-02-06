package saves;

import characters.Entities.Enemy;
import characters.Entities.Player;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import map.Map;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import screen.LoadGame;

import static org.junit.jupiter.api.Assertions.*;
import static saves.HelperTestFunctions.*;

@RunWith(GdxTestRunner.class)
class LoadGameTest {

/*    private Gameplay gameplay;
    public LoadGameTest(){
        GdxTestRunner runner =new GdxTestRunner()
        GameMain game =new GameMain();
        Application test=new HeadlessApplication(game);
        //gameplay=new Gameplay(game);
        System.out.println("Geberated test");
    }*/

    @Test
    @DisplayName("Load Object - Exist and Correct Type")
    void LoadObjectThatExistsAndCorrectType() {
        // Test Data
        JSONObject someData = new JSONObject();
        Object[][] testData = {{"Key1", "SomeStringValue"}, {"Key2", 'a'}, {"Key3", 5}, {
                "Key4", 5.0}, {"Key5", false}};
        for (Object[] data : testData) {
            someData.put(data[0], data[1]);
        }
        for (Object[] data : testData) {
            Object someValue = LoadGame.loadObject(someData, (String) data[0], data[1].getClass());
            assertEquals(data[1], someValue);
        }
    }

    @Test
    @DisplayName("Load Object - Exist and Invalid Type")
        /*
         * Checks that objects loaded to a different type raise an IllegalArgumentException
         */
    void LoadObjectThatExistsAndInvalidType() {
        JSONObject someData = new JSONObject();
        // Test Data
        // The given types should be different from the type of the matching index in testData
        Class[] types = {Integer.class, String.class, Float.class, Boolean.class, String.class};
        Object[][] testData = {{"Key1", "SomeStringValue"}, {"Key2", 'a'}, {"Key3", 5}, {
                "Key4", 5.0}, {"Key5", false}};
        assertEquals(testData.length, types.length);

        for (Object[] data : testData) {
            someData.put(data[0], data[1]);
        }
        for (int index = 0; index < testData.length; index++) {
            int finalIndex = index;
            assertThrows(IllegalArgumentException.class, () -> LoadGame.loadObject(someData,
                    (String) testData[finalIndex][0], types[finalIndex]));
        }
    }

    @Test
    @DisplayName("Load Object - Doesn't Exist")
        /*
         * Checks that attempting to load an object that doesn't exist raises an IllegalArgumentException
         */
    void LoadObjectThatDoesntExist() {
        JSONObject someData = new JSONObject();
        // Test Data
        Object[][] testData = {{"Key1", "SomeStringValue"}, {"Key2", 'a'}, {"Key3", 5}, {
                "Key4", 5.0}, {"Key5", false}};
        for (Object[] data : testData) {
            assertThrows(IllegalArgumentException.class, () -> LoadGame.loadObject(someData,
                    (String) data[0], data[1].getClass()));
        }
    }

    @Test
    @DisplayName("Validate Object - Exist, Correct Type and Correct Value")
    void ValidateObjectThatExistsAndCorrectTypeAndCorrectValue() {
        // Test Data
        JSONObject someData = new JSONObject();
        Object[][] testData = {{"Key1", "SomeStringValue"}, {"Key2", 'a'}, {"Key3", 5}, {
                "Key4", 5.0}, {"Key5", false}};
        for (Object[] data : testData) {
            someData.put(data[0], data[1]);
        }

        for (Object[] data : testData) {
            assertEquals(data[1], LoadGame.validateAndLoadObject(someData,
                    (String) data[0], data[1]));
        }
    }

    @Test
    @DisplayName("Validate Object - Exist, Correct Type and Correct Value")
    void ValidateObjectThatExistsAndCorrectTypeAndIncorrectValue() {
        // Test Data
        JSONObject someData = new JSONObject();
        // Test Data and Validation data should be the same types
        Object[][] testData = {{"Key1", "SomeStringValue"}, {"Key2", 'a'}, {"Key3", 5}, {
                "Key4", 5.0}, {"Key5", false}};
        Object[] validationData = {"SomeDifferentString", 'b', 3, 2.0, true};
        assertEquals(testData.length, validationData.length);
        for (Object[] data : testData) {
            someData.put(data[0], data[1]);
        }

        for (int index = 0; index < testData.length; index++) {
            assertEquals(testData[index][1].getClass(), validationData[index].getClass());
            int finalIndex = index;
            assertThrows(IllegalArgumentException.class, () -> LoadGame.validateAndLoadObject(someData,
                    (String) testData[finalIndex][0], validationData[finalIndex]));
        }
    }

    @Test
    @DisplayName("Load Object - Exist and Correct Type")
        /*
         * Checks that objects loaded to a different type raise an IllegalArgumentException
         */
    void ValidateObjectThatExistsAndInvalidType() {
        JSONObject someData = new JSONObject();
        // Test Data
        // The given types should be different from the type of the matching index in testData
        Class[] types = {Integer.class, String.class, Float.class, Boolean.class, String.class};
        Object[][] testData = {{"Key1", "SomeStringValue"}, {"Key2", 'a'}, {"Key3", 5}, {
                "Key4", 5.0}, {"Key5", false}};
        assertEquals(testData.length, types.length);

        for (Object[] data : testData) {
            someData.put(data[0], data[1]);
        }
        for (int index = 0; index < testData.length; index++) {
            int finalIndex = index;
            assertThrows(IllegalArgumentException.class, () -> LoadGame.validateAndLoadObject(someData,
                    (String) testData[finalIndex][0], types[finalIndex]));
        }
    }

    @Test
    @DisplayName("Load Object - Exist and Correct Type")
        /*
         * Checks that attempting to load an object that doesn't exist raises an IllegalArgumentException
         */
    void ValidateObjectThatDoesntExist() {
        JSONObject someData = new JSONObject();
        // Test Data
        Object[][] testData = {{"Key1", "SomeStringValue"}, {"Key2", 'a'}, {"Key3", 5}, {
                "Key4", 5.0}, {"Key5", false}};
        for (Object[] data : testData) {
            assertThrows(IllegalArgumentException.class, () -> LoadGame.validateAndLoadObject(someData,
                    (String) data[0], data[1]));
        }
    }


    @Test
    @DisplayName("Null target System")
    void testNullTargetSystem() {
        World world = buildWorld();
        TiledMap map = buildMap();
        MapLayer layers = map.getLayers().get("spawn");
        Rectangle point = ((RectangleMapObject) layers.getObjects().get(0)).getRectangle();
        Enemy enemy = new Enemy(world, point.x, point.y);
        enemy.targetSystem = null;
        JSONObject save = enemy.save();
        Enemy loaded = new Enemy(world, save);
        assertNull(loaded.targetSystem);
    }

    @Test
    @DisplayName("Checks map exists")
    void TestMap() {
        TmxMapLoader maploader = new TmxMapLoader();
        assertNotNull(maploader);
        // load the tiled map
        TiledMap map = maploader.load("Map/Map.tmx");
        Map.create(map);
        assertNotNull(map);
    }

    @Test
    @DisplayName("Validates spawn locations exist")
    void TestSpawn() {
        TmxMapLoader maploader = new TmxMapLoader();
        // load the tiled map
        TiledMap map = maploader.load("/home/sam/Projects/ENG1-PART2/core/src/test/saves/Map.tmx");
        Map.create(map);
        assertNotNull(map);
        MapLayer layers = map.getLayers().get("spawn");
        assertNotNull(layers);
        assertNotNull(layers.getObjects().get(0));
    }

    @Test
    @DisplayName("Save and Load Npc")
    void TestPlayer() {
        Player player = buildPlayer();
        JSONObject savedPlayer = player.save();
        Player loadedPlayer = new Player(buildWorld(), buildMap(), savedPlayer);
        assertEquals(player, loadedPlayer);
    }

}