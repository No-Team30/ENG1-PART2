package tools;

import characters.Entities.Player;
import characters.Entities.abilities.*;
import characters.Entities.Enemy;
import com.badlogic.gdx.physics.box2d.*;

import java.util.regex.Pattern;

import sprites.Door;
import sprites.Systems;


public class ObjectContactListener implements ContactListener {

    // regex to determine whether contact object is a teleport or not
    private final String teleportPattern = ".*teleporter.*";
    private boolean isTeleport;
    // regex to determine whether contact object is a healing pod or not
    private final String healingPattern = ".*healingPod.*";
    private boolean isHealingPod;

    private final String systemPattern = ".*system.*";
    private final String infiltratorsPattern = ".*Infiltrators.*";


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
        isTeleport = Pattern.matches(teleportPattern, fixB.getBody().getUserData().toString());
        // use reg to check whether the object contacted is a healpod
        isHealingPod = Pattern.matches(healingPattern, fixB.getBody().getUserData().toString());

        // only auber contact with teleport will be listened
        if (isTeleport && fixA.getBody().getUserData() == "auber") {
            // set the player.UserData to ready_to_teleport for teleport_process
            fixA.getBody().setUserData("ready_to_teleport");
        }

        // if auber contact with healing pod and healing pod is not sabotaged
        if (isHealingPod && ((String) fixA.getBody().getUserData()).equals("auber")) {
            // set the player.UserData to ready_to_heal for healing process
            if (fixB.getBody().getUserData() == "healingPod_not_sabotaged") {
                fixA.getBody().setUserData("ready_to_heal");
            }
        }
        Player player = null;
        Enemy infiltrator = null;
        Systems systems = null;

        if (fixA.getUserData() instanceof Player) {
            player = (Player) fixA.getUserData();
        } else if (fixB.getUserData() instanceof Player) {
            player = (Player) fixB.getUserData();
        }

        if (fixA.getUserData() instanceof Enemy) {
            infiltrator = (Enemy) fixA.getUserData();
        } else if (fixB.getUserData() instanceof Enemy) {
            infiltrator = (Enemy) fixB.getUserData();
        }

        if (fixA.getUserData() instanceof Systems) {
            systems = (Systems) fixA.getUserData();
        } else if (fixB.getUserData() instanceof Systems) {
            systems = (Systems) fixB.getUserData();
        }

        if (infiltrator != null) {
            if (player != null) {
                infiltrator.ability.setTarget(player);
                infiltrator.ability.provokeAbility(infiltrator, player);
            } else if (systems != null) {
                Systems targetSystem = infiltrator.get_target_system();
                if (targetSystem == systems) {
                    infiltrator.ability.setDisable(true);
                    infiltrator.currentContactSystem = systems;
                    infiltrator.set_attackSystemMode();
                    targetSystem.set_sabotaging();
                }
            }
        }

        if (player != null) {
            if (!player.is_arresting() && player.not_arrested(infiltrator)) {
                player.setNearby_enemy(infiltrator);

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
                && ((String) fixA.getBody().getUserData()).toString().equals("ready_to_heal")) {
            // set the player.UserData to ready_to_heal for healing process
            fixA.getBody().setUserData("auber");
        }

        Player player = null;
        Enemy infiltrator = null;
        Systems systems = null;

        if (is_Auber(fixA)) {
            player = (Player) fixA.getUserData();
        } else if (is_Auber(fixB)) {
            player = (Player) fixB.getUserData();
        }

        if (is_Infiltrators(fixA)) {
            infiltrator = (Enemy) fixA.getUserData();
        } else if (is_Infiltrators(fixB)) {
            infiltrator = (Enemy) fixB.getUserData();
        }

        if (is_System(fixA)) {
            systems = (Systems) fixA.getUserData();
        } else if (is_System(fixB)) {
            systems = (Systems) fixB.getUserData();
        }

        if (infiltrator != null && !infiltrator.isArrested()) {
            if (systems != null) {
                Systems currentContactsystem = infiltrator.currentContactSystem;
                // contact will be listened if enemy finished sabotaging a system
                // and have generated next target system or enemy stop sabotaging the system
                // the end contact between enemy and system will be listened
                if (currentContactsystem == systems) {
                    infiltrator.ability.setDisable(false);
                    float sysHp = currentContactsystem.hp;
                    if (sysHp > 1) {
                        // if system's hp > 1, set it to not sabotaged status
                        currentContactsystem.set_not_sabotaged();
                    }
                }

            } else if (player != null) {
                if (infiltrator.ability instanceof AttackPlayerAbility) {
                    AttackPlayerAbility ability = (AttackPlayerAbility) infiltrator.ability;
                    ability.contact = false;
                }
            }
        }
        if (player != null) {
            if (infiltrator != null && !player.arrestPressed) {
                player.setNearby_enemy(null);
            }
        }


    }

    /**
     * if the given fixture is an infiltrator.
     *
     * @param fixture contact fixture
     * @return true if fixture is an Enemy object
     */
    public boolean is_Infiltrators(Fixture fixture) {
        return Pattern.matches(infiltratorsPattern, fixture.getBody().getUserData().toString());
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

        // if the character is about to come into contact with a door
        if (is_Doors(fixB)
                && ((fixA.getBody().getUserData() == "auber") || is_Infiltrators(fixA))) {
            // gets the door
            Object data = fixB.getBody().getUserData();
            if (data instanceof Door) {
                // if the door is locked, it is collidable,
                // else it is not
                contact.setEnabled(((Door) data).isLocked());
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}