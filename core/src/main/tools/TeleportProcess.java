package tools;

import characters.Entities.Player;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import screen.actors.Teleport_Menu;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Teleport_process.
 */
public class TeleportProcess {

    public Teleport_Menu selectedRoom;
    private final Player auber;
    public HashMap<String, ArrayList<Float>> teleporterPosition;
    public HashMap<Integer, ArrayList<Float>> jailPosition;


    /**
     * Create a new instantiated teleport_process.
     *
     * @param selectedRoom the selectBox object from the stage
     * @param auber        the player object
     * @param map          tiled map used to get positions of teleports
     */
    public TeleportProcess(Teleport_Menu selectedRoom, Player auber, TiledMap map) {
        this.selectedRoom = selectedRoom;
        this.auber = auber;
        MapLayers layers = map.getLayers();
        teleporterPosition = new HashMap<>();
        jailPosition = new HashMap<>();
        generate_position(layers);
    }

    /**
     * store the positions of the teleports in Hashmap.
     *
     * @param layers layers of the tiled map
     */
    public void generate_position(MapLayers layers) {
        for (MapObject object : layers.get("teleports").getObjects()) {
            Rectangle tele = ((RectangleMapObject) object).getRectangle();
            ArrayList<Float> teleport = new ArrayList<>();
            teleport.add(tele.x);
            teleport.add(tele.y);
            teleporterPosition.put(object.getName(), teleport);
        }
    }

    /**
     * validate the teleport process.
     */
    public void validate() {

        // if auber not in contact with a teleporter, the menu(selected box) should be disabled
        if (auber.movementSystem.b2body.getUserData().equals("auber")) {
            selectedRoom.setDisabled(true);
        }
        // if auber's contact with teleporter detected, enable the selectBox
        if (auber.movementSystem.b2body.getUserData().equals("ready_to_teleport")
                && selectedRoom.isDisabled()) {
            selectedRoom.setDisabled(false);
        } else if ((!(selectedRoom.getSelected()).equals("Teleport")
                && !selectedRoom.getSelected().equals("Jail"))
                && ((auber.movementSystem.b2body.getUserData()).equals("ready_to_teleport"))) {
            transform();
        } else if ((selectedRoom.getSelected()).equals("Jail") && auber.enemyManager.haveEnemiesBeenArrested()) {
            auber.enemyManager.jailAllArrestedEnemies();
            selectedRoom.setSelected("Teleport");
            selectedRoom.setDisabled(true);
        } else if ((selectedRoom.getSelected()).equals("Jail")) {
            selectedRoom.setSelected("Teleport");
        }
    }

    /**
     * transfrom auber.
     */
    public void transform() {
        // get the room name to be teleported form the selectBox
        String room = selectedRoom.getSelected();
        // get the X cord from Data.teleporter_Position(HashMap<String,ArrayList>)
        float roomX = teleporterPosition.get(room).get(0);
        // get the Y cord from Data.teleporter_Position(HashMap<String,ArrayList>)
        float roomY = teleporterPosition.get(room).get(1);
        // transform auber to the chosen room
        auber.movementSystem.b2body.setTransform(roomX, roomY, 0);
        // set the selectBox back to Teleport and disable the selectedBox
        selectedRoom.setSelected("Teleport");
        selectedRoom.setDisabled(true);
    }
}
