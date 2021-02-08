package saves;

import characters.Entities.Player;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import map.Map;

public class HelperTestFunctions {
    public static World buildWorld() {
        return new World(new Vector2(0, 0), true);
    }

    public static TiledMap buildMap() {
        TmxMapLoader maploader = new TmxMapLoader();
        // load the tiled map
        TiledMap map = maploader.load("Map/Map.tmx");
        Map.create(map);
        return map;
    }

    public static Player buildPlayer() {
        World world = buildWorld();
        TiledMap map = buildMap();
        Rectangle spawn = ((RectangleMapObject) map.getLayers().get("spawn").getObjects().get(0)).getRectangle();
        return new Player(world, spawn.x, spawn.y, map);
    }
}
