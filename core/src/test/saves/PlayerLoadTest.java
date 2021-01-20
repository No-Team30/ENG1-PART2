package saves;

import com.team3.game.GameMain;
import org.junit.Test;
import screen.Gameplay;

import static org.junit.Assert.assertEquals;

public class PlayerLoadTest {
    Gameplay gameplay;

    public PlayerLoadTest() {
        GameMain game = new GameMain();
        gameplay = new Gameplay(game);
    }

    @Test
    public void test() {
        assertEquals(1 + 2, 3);
    }
}
