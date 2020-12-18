package screen;

import characters.Movement.AiMovement;
import characters.Movement.UserMovement;
import com.badlogic.gdx.math.Vector2;
import com.team3.game.GameMain;

public class GameDemo extends Gameplay {

    public GameDemo(GameMain game) {
        //super(game, new Vector2(2560, 1440));
        super(game, new Vector2(640, 360));
        Gameplay.player.movementSystem = new AiMovement(Gameplay.player, (UserMovement) Gameplay.player.movementSystem);
        Gameplay.player.movementSystem.b2body.setUserData("auber_demo");
        Gameplay.player.movementSystem.speed = 10000;
        Gameplay.player.arrestPressed = true;
    }
}
