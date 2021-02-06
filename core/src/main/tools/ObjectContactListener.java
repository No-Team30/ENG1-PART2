package tools;

import characters.Entities.Enemy;
import characters.Entities.Player;
import characters.Entities.abilities.AttackPlayerAbility;
import com.badlogic.gdx.physics.box2d.*;
import sprites.Door;
import sprites.Systems;

import java.util.regex.Pattern;


public class ObjectContactListener implements ContactListener {

    // regex to determine whether contact object is a teleport or not
    private final String teleportPattern = ".*teleporter.*";
    private boolean isTeleport;
    // regex to determine whether contact object is a healing pod or not
    private final String healingPattern = ".*healingPod.*";
    private boolean isHealingPod;

    private final String systemPattern = ".*system.*";
    private final String enemyPattern = ".*Enemy.*";


    /**
     * If auber has contact with the teleport, the auber's userData to ready_to_teleport,
     * update auber's position in player.update()
     * if enemy has contact with the systems, start sabotage process.
     * if auber has contact with healing pod, start healing process.
     * if auber has contact with enemy body and auber is not arresting another enemy and
     * KEY A is pressed, auber to arrest enemy.
     * if auber has contact with enemy ability area and enemy is not in cool down,
     * enemy will use ability.
     *
     * @param contact the contact that is being sensed
     */
    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // use reg to check whether the object contacted is a teleporter or not
        if (fixB.getBody().getUserData() == null) {
            System.out.println(fixB);
            System.out.println("Failed");
        }
        isTeleport = Pattern.matches(teleportPattern, fixB.getBody().getUserData().toString());
        // use reg to check whether the object contacted is a healpod
        isHealingPod = Pattern.matches(healingPattern, fixB.getBody().getUserData().toString());

        // only auber contact with teleport will be listened
        if (isTeleport && fixA.getBody().getUserData() == "auber") {
            // set the player.UserData to ready_to_teleport for teleport_process
            fixA.getBody().setUserData("ready_to_teleport");
        }

        // if auber contact with healing pod and healing pod is not sabotaged
        if (isHealingPod && fixA.getBody().getUserData().equals("auber")) {
            // set the player.UserData to ready_to_heal for healing process
            if (fixB.getBody().getUserData() == "healingPod_not_sabotaged") {
                fixA.getBody().setUserData("ready_to_heal");
            }
        }
        Player player = null;
        Enemy enemy = null;
        Systems systems = null;

        if (fixA.getUserData() instanceof Player) {
            player = (Player) fixA.getUserData();
        } else if (fixB.getUserData() instanceof Player) {
            player = (Player) fixB.getUserData();
        }

        if (fixA.getUserData() instanceof Enemy) {
            enemy = (Enemy) fixA.getUserData();
        } else if (fixB.getUserData() instanceof Enemy) {
            enemy = (Enemy) fixB.getUserData();
        }

        if (fixA.getUserData() instanceof Systems) {
            systems = (Systems) fixA.getUserData();
        } else if (fixB.getUserData() instanceof Systems) {
            systems = (Systems) fixB.getUserData();
        }

        if (enemy != null) {
            if (player != null) {
                enemy.ability.setTarget(player);
                enemy.ability.provokeAbility(enemy, player);
                if (player.isArrestPressed()) {
                    player.enemyManager.arrestEnemy(enemy);
                }
            } else if (systems != null) {
                Systems targetSystem = enemy.get_target_system();
                if (targetSystem == systems) {
                    enemy.ability.setDisable(true);
                    enemy.currentContactSystem = systems;
                    enemy.set_attackSystemMode();
                    targetSystem.set_sabotaging();
                }
            }
        }
    }


    /**
     * if auber end contact with enemy and KEY A is not pressed, arrest process will fail
     * if enemy end contact with system system's hp should be checked.
     *
     * @param contact The contact that is ending
     */
    @Override
    public void endContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // use reg to check whether the object end contact is a teleporter or not
        isTeleport = Pattern.matches(teleportPattern, fixB.getBody().getUserData().toString());
        // use reg to check whether the object end contact is a healpod
        isHealingPod = Pattern.matches(healingPattern, fixB.getBody().getUserData().toString());

        // only auber contact with teleport will be listened
        if (isTeleport && (fixA.getBody().getUserData()).toString().equals("ready_to_teleport")) {
            // set the player.UserData to auber after the contact ended
            fixA.getBody().setUserData("auber");
        }

        // if auber end contact with healing pod, set auber's body data back to auber
        if (isHealingPod
                && fixA.getBody().getUserData().equals("ready_to_heal")) {
            // set the player.UserData to ready_to_heal for healing process
            fixA.getBody().setUserData("auber");
        }

        Player player = null;
        Enemy enemy = null;
        Systems systems = null;

        if (is_Auber(fixA)) {
            player = (Player) fixA.getUserData();
        } else if (is_Auber(fixB)) {
            player = (Player) fixB.getUserData();
        }

        if (is_Enemy(fixA)) {
            enemy = (Enemy) fixA.getUserData();
        } else if (is_Enemy(fixB)) {
            enemy = (Enemy) fixB.getUserData();
        }

        if (is_System(fixA)) {
            systems = (Systems) fixA.getUserData();
        } else if (is_System(fixB)) {
            systems = (Systems) fixB.getUserData();
        }

        if (enemy != null && !enemy.isArrested()) {
            if (systems != null) {
                Systems currentContactsystem = enemy.currentContactSystem;
                // contact will be listened if enemy finished sabotaging a system
                // and have generated next target system or enemy stop sabotaging the system
                // the end contact between enemy and system will be listened
                if (currentContactsystem == systems) {
                    enemy.ability.setDisable(false);
                    float sysHp = currentContactsystem.hp;
                    if (sysHp > 1) {
                        // if system's hp > 1, set it to not sabotaged status
                        currentContactsystem.set_not_sabotaged();
                    }
                }

            } else if (player != null) {
                if (enemy.ability instanceof AttackPlayerAbility) {
                    AttackPlayerAbility ability = (AttackPlayerAbility) enemy.ability;
                    ability.contact = false;
                }
                if (player.isArrestPressed()) {
                    player.enemyManager.arrestEnemy(enemy);
                    enemy.ability.setDisable(true);
                }
            }
        }


    }

    /**
     * if the given fixture is an enemy.
     *
     * @param fixture contact fixture
     * @return true if fixture is an Enemy object
     */
    public boolean is_Enemy(Fixture fixture) {
        return Pattern.matches(enemyPattern, fixture.getBody().getUserData().toString());
    }

    /**
     * If the given fixture is a system.
     *
     * @param fixture contact fixture
     * @return true if fixture is a System object
     */
    public boolean is_System(Fixture fixture) {
        return Pattern.matches(systemPattern, fixture.getBody().getUserData().toString())
                || Pattern.matches(healingPattern, fixture.getBody().getUserData().toString());
    }

    /**
     * If the given fixture is a Door.
     *
     * @param fixture contact fixture
     * @return true if fixture is a door
     */
    public boolean is_Doors(Fixture fixture) {
        return Pattern.matches("door_.*", fixture.getUserData().toString());
    }


    /**
     * If the given fixture is a Auber.
     *
     * @param fixture contact fixture
     * @return true if fixture is a Player object
     */
    public boolean is_Auber(Fixture fixture) {
        return fixture.getBody().getUserData().equals("auber");
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // Try and obtain the door object
        Door door = null;
        if (is_Doors(fixB) && fixB.getBody().getUserData() instanceof Door) {
            door = (Door) fixB.getBody().getUserData();
        } else if (is_Doors(fixB) && fixB.getBody().getUserData() instanceof Door) {
            door = (Door) fixA.getBody().getUserData();
        }
        // Unlock the door, if an auber or enemy are passing through
        if ((is_Auber(fixA) || is_Enemy(fixA) || is_Auber(fixB) || is_Enemy(fixB)) && door != null) {
            contact.setEnabled(door.isLocked());
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}