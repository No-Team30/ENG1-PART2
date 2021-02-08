package saves;

import characters.Entities.Npc;
import characters.Entities.Player;
import characters.Movement.AiMovement;
import characters.Movement.UserMovement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static saves.HelperTestFunctions.buildPlayer;
import static saves.HelperTestFunctions.buildWorld;

public class EntitiesEqualTest {

    @Test
    void TestAiMovementSystemsEqual() {
        Npc npcA = new Npc(buildWorld(), 0, 0);
        AiMovement a = (AiMovement) npcA.movementSystem;
        // TODO Maybe need to clone this?
        AiMovement b = a;
        assertNotEquals(a, null, "Null check fails");
        // Check two identical systems are equal
        assertEquals(a, b, "Identical ai movement equality check fails");
        // Check movement speed fails
        b.speed += 10;
        assertNotEquals(a, b, "Ai movement speed equality check fails");
        b.speed = a.speed;
        // Check user data fails
        b.b2body.setUserData("Test");
        assertNotEquals(a, b, "Ai movement userdata equality check fails");
        b.b2body.setUserData(a.b2body.getUserData());

        // Check size fails
        b.destX += 10;
        assertNotEquals(a, b, "Ai movement destX equality check fails");
        b.destX -= 10;
        b.destY += 10;
        assertNotEquals(a, b, "Ai movement destY equality check fails");
        b.destY -= 10;

    }

    @Test
    void TestUserMovementSystemsEqual() {
        Player playerA = buildPlayer();
        UserMovement a = (UserMovement) playerA.movementSystem;
        // TODO Maybe need to clone this?
        UserMovement b = a;
        assertNotEquals(a, null, "Null check fails");
        // Check two identical systems are equal
        assertEquals(a, b, "Identical User movement equality check fails");
        // Check movement speed fails
        b.speed += 10;
        assertNotEquals(a, b, "User movement speed equality check fails");
        b.speed = a.speed;
        // Check user data fails
        b.b2body.setUserData("Test");
        assertNotEquals(a, b, "User movement user data equality check fails");
        b.b2body.setUserData(a.b2body.getUserData());
    }

    @Test
    void TestEntitiesEqual() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Test
    void TestEnemiesEqual() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Test
    void TestPlayersEqual() {
        Player a = buildPlayer();
        // TODO Maybe need to clone this?
        Player b = a;
        assertEquals(a, b, "Identical Player equality check fails");
        b.movementSystem = null;
        assertNotEquals(a, b, "Player movement equality check fails");
        b.movementSystem = a.movementSystem;
        b.health += 1;
        assertNotEquals(a, b, "Player health equality check fails");
        b.health -= 1;

        b.isHealing = !b.isHealing;
        assertNotEquals(a, b, "Player isHealing equality check fails");
        b.isHealing = !b.isHealing;

    }
}
