package screen;

import characters.Movement.AiMovement;
import characters.Movement.UserMovement;
import com.team3.game.GameMain;

public class GameDemo extends Gameplay {

    public GameDemo(GameMain game) {
        //super(game, new Vector2(2560, 1440));
        super(game, true);// new Vector2(640, 360), true);
        this.player.movementSystem = new AiMovement(this.player, (UserMovement) this.player.movementSystem);
        this.player.movementSystem.b2body.setUserData("auber_demo");
        this.player.movementSystem.speed = 10000000;
        this.player.setArrestPressed(true);
    }
}
