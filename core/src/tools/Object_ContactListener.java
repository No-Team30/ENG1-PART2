package tools;


import com.badlogic.gdx.physics.box2d.*;
import java.util.regex.Pattern;

public class Object_ContactListener implements ContactListener {

    // regex to determine whether contact object is a teleport or not
    private final String pattern = ".*teleporter.*";
    private boolean isTeleport;

    private final String pattern2 = ".*system.*";
    private boolean isSystem;

    private final String pattern3 = ".*healingPod.*";
    private boolean isHealingPod;


    /**
     * If auber has contact with the teleporter, the auber's userData --> ready_to_teleport, update auber's position in player.update()
     * if enemy has contact with the systems. start sabotage process
     * if auber has contact with healing pod. start healing process
     * @param contact
     */
    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // use reg to check whether the object contacted is a teleporter or not
        isTeleport = Pattern.matches(pattern, (String) fixB.getUserData());
        // use reg to check whether the object contacted is a system or not
        isSystem = Pattern.matches(pattern2,(String) fixB.getUserData());
        // use reg to check whether the object contacted is a healpod
        isHealingPod = Pattern.matches(pattern3,(String) fixB.getUserData());


        if (isTeleport && (String) fixA.getBody().getUserData() == "auber")  {
            // set the player.UserData to ready_to_teleport for teleport_process
            fixA.getBody().setUserData("ready_to_teleport");
        }
        // change the fixA to enemy after enemy entity built, this is for test purpose
        if (isSystem && (String) fixA.getBody().getUserData() == "auber")  {

            System.out.println("start contact with " + (String) fixB.getUserData());
            System.out.println((String) fixB.getUserData() + " sabotaged : " + (String) fixB.getBody().getUserData());
            
            // test for system status menu
            //fixB.getBody().setUserData("sabotaged");
            fixB.getBody().setUserData("sabotaging");

            /** To Do
             *  if the contact begin between enemy and systems, sabotage process should begin
             *  set enemy.userdata to sabotaging.
             *  ...
             */
        }
        // if auber contact with healing pod and healing pod is not sabotaged
        if (isHealingPod && (String) fixA.getBody().getUserData() == "auber"){
            // set the player.UserData to ready_to_heal for healing process
            if(fixB.getBody().getUserData() == "not sabotaged"){
                fixA.getBody().setUserData("ready_to_heal");
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // use reg to check whether the object end contact is a teleporter or not
        isTeleport = Pattern.matches(pattern, (String) fixB.getUserData());
        // use reg to check whether the object end contact is a system or not
        isSystem = Pattern.matches(pattern2,(String) fixB.getUserData());
        // use reg to check whether the object end contact is a healpod
        isHealingPod = Pattern.matches(pattern3,(String) fixB.getUserData());


        if (isTeleport && (String) fixA.getBody().getUserData() == "ready_to_teleport") {
            // set the player.UserData to auber after the contact ended
            fixA.getBody().setUserData("auber");
        }
        // change the fixA to enemy after enemy entity built, this is for test purpose
        if (isSystem && (String) fixA.getBody().getUserData() == "auber")  {
            System.out.println("end contact with " + (String) fixB.getUserData());
            System.out.println((String) fixB.getUserData() + " sabotaged : " + (String) fixB.getBody().getUserData());

            /** To Do
             *  if the contact end between enemy and systems, sabotage process should stop
             *  set enemy.userdata to not_sabotage.(if contact ends, either auber is near or is being arrested
             *  if player is nearby, should start attacking auber or run away from auber)
             *  ...
             */

        }
        // if auber end contact with the healing pod
        if (isHealingPod && (String) fixA.getBody().getUserData() == "ready_to_heal"){
            // set the player.UserData back to auber to end healing process
            fixA.getBody().setUserData("auber");
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
