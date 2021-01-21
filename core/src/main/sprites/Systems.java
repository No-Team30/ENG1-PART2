package sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import org.json.simple.JSONObject;
import screen.LoadGame;

public class Systems extends InteractiveTileObject {

    public String sysName;
    public float hp;
    /**
     * Whether there is an enemy targeting this system
     */
    private boolean isTargetedByEnemy;

    /**
     * Creates a new instantiated System object.
     *
     * @param world  Physics world the teleport should query
     * @param map    Tiled map object will be placed in
     * @param bounds The bounds of where the object will interact with entities
     * @param name   They name of the system
     */
    public Systems(World world, TiledMap map, Rectangle bounds, String name) {
        super(world, map, bounds);
        this.isTargetedByEnemy = false;
        sysName = name;
        hp = 100;
        // use the fixture.userdata to store the system object.
        this.fixture.setUserData(this);
        // use the body.userdata to store the saboage status. used for contact listener
        this.fixture.getBody().setUserData("system_not_sabotaged");
        this.fixture.setSensor(true);
        // check whether is a healing pod or not
        isHealing_pod(name);
        isDoors(name);

    }

    Systems(World world, TiledMap map, Rectangle bounds, String name, Float hp, String userData, Boolean isTargetedByEnemy) {
        super(world, map, bounds);
        this.isTargetedByEnemy = isTargetedByEnemy;
        sysName = name;
        this.hp = hp;
        // use the fixture.userdata to store the system object.
        this.fixture.setUserData(this);
        // use the body.userdata to store the saboage status. used for contact listener
        this.fixture.getBody().setUserData(userData);
        this.fixture.setSensor(true);
        // check whether is a healing pod or not
        isHealing_pod(name);
        isDoors(name);
    }

    public static Systems loadFromJSON(World world, TiledMap map, JSONObject object) {
        LoadGame.validateAndLoadObject(object, "object_type", "system");
        String name = LoadGame.loadObject(object, "sys_name", String.class);
        Float hp = LoadGame.loadObject(object, "hp", Float.class);
        String userData = LoadGame.loadObject(object, "user_data", String.class);
        Boolean targetedEnemy = LoadGame.loadObject(object, "is_targeted_enemy", Boolean.class);

        Float x_position = LoadGame.loadObject(object, "x_position", Float.class);
        Float y_position = LoadGame.loadObject(object, "y_position", Float.class);
        Float width = LoadGame.loadObject(object, "width", Float.class);
        Float height = LoadGame.loadObject(object, "height", Float.class);
        Rectangle bounds = new Rectangle(x_position, y_position, width, height);
        return new Systems(world, map, bounds, name, hp, userData, targetedEnemy);
    }

    /**
     * If the system is a healing pod userdata accordingly.
     *
     * @param name The name of the system
     */
    public void isHealing_pod(String name) {
        // if system is healingPod, set the fixture to sensor
        if (name.equals("healingPod")) {
            this.fixture.getBody().setUserData("healingPod_not_sabotaged");
            this.fixture.setSensor(true);
        }
    }

    /**
     * If the system is a door set userdata accordingly.
     *
     * @param name The name of the system
     */
    public void isDoors(String name) {
        // if system is healingPod, set the fixture to sensor
        if (name.equals("doors")) {
            this.fixture.getBody().setUserData("doors_not_sabotaged");
            this.fixture.setSensor(true);
        }
    }

    public String getSystemName() {
        return sysName;
    }

    /**
     * sabotage status.
     *
     * @return sabotage status
     */
    public String getSabotage_status() {
        return (String) this.body.getUserData();
    }

    public float[] getposition() {
        return new float[]{this.body.getPosition().x, this.body.getPosition().y};
    }

    /**
     * set system to sabotaged.
     */
    public void set_sabotaged() {
        body.setUserData("system_sabotaged");
    }

    /**
     * set system to not sabotaged.
     */
    public void set_not_sabotaged() {
        body.setUserData("system_not_sabotaged");
    }

    /**
     * set system to sabotaging.
     */
    public void set_sabotaging() {
        body.setUserData("system_sabotaging");
    }

    /**
     * check system is sabotaged or not.
     *
     * @return true if system is sabotaged
     */
    public boolean is_sabotaged() {
        return body.getUserData().equals("system_sabotaged");
    }

    /**
     * check system is under sabotaging or not.
     *
     * @return return true if is sabotaging
     */
    public boolean is_sabotaging() {
        return body.getUserData().equals("system_sabotaging");
    }

    /**
     * check system is not sabotaged or not.
     *
     * @return true if system is not sabotaged and not sabotaging
     */
    public boolean is_not_sabotaged() {
        return body.getUserData().equals("system_not_sabotaged");
    }

    public boolean isTargetedByEnemy() {
        return this.isTargetedByEnemy;
    }

    public void setTargetedByEnemy(boolean value) {
        this.isTargetedByEnemy = value;
    }

    /**
     * Saves the system to a json object
     *
     * @return A json object of the basic system data
     */
    public JSONObject save() {
        JSONObject state = new JSONObject();
        state.put("object_type", "system");
        state.put("sys_name", this.sysName);
        state.put("hp", this.hp);
        state.put("user_data", this.body.getUserData());
        state.put("is_targeted_enemy", this.isTargetedByEnemy);
        state.put("x_position", this.bounds.x);
        state.put("y_position", this.bounds.y);
        state.put("width", this.bounds.width);
        state.put("height", this.bounds.height);
        return state;
    }

}
