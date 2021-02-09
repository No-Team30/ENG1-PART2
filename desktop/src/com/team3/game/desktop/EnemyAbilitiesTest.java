package com.team3.game.desktop;

import characters.Entities.Enemy;
import characters.Entities.EnemyManager;
import characters.Entities.abilities.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.team3.game.GameMain;
import org.junit.jupiter.api.Test;
import screen.Gameplay;
import sprites.Systems;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

class TestGame extends GameMain {
    public Gameplay gameplay;
    public boolean created = false;

    @Override
    public void create() {
        super.create();
        newGame();
        created = true;
    }

    public void newGame() {
        gameplay = new Gameplay(this, false);
        setScreen(gameplay);
    }
}

public class EnemyAbilitiesTest {

    LwjglApplication application;
    TestGame game;

    public EnemyAbilitiesTest() {
        try {
            startApplication();
            waitApplicationReady(game);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void startApplication() throws InterruptedException {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 800;
        config.height = 640;
        config.resizable = true;

        game = new TestGame();
        application = new LwjglApplication(game, config);
    }

    void waitApplicationReady(TestGame gameMain) {
        try {
            while (true) {
                if (gameMain.created) return;
                Thread.sleep(3000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("--------------------------");
        }
    }

    ArrayList<Enemy> getAllEnemies(EnemyManager enemyManager) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = enemyManager.getClass();
        Field field = clazz.getDeclaredField("activeEnemies");
        field.setAccessible(true);

        return (ArrayList<Enemy>) field.get(enemyManager);
    }

    void fastenAbilityUpdate(ArrayList<Enemy> enemies, float delta) {
        for (Enemy enemy : enemies) {
            enemy.ability.update(delta);
        }
    }

    @Test
    void testGhostModeAbility() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Enemy.randomUseAbilityRate = 0;//Disabled abilities are released randomly
        Thread.sleep(50);

        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.getInstance().player.enemyManager);


        //Replace all enemies' ability to ghostModeAbility
        for (Enemy enemy : enemies) {
            enemy.ghostMode = false;
            enemy.ability = new GhostModeAbility();
        }

        //Repeat i times to make sure the skill can be used repeatedly
        for (int i = 0; i <= 3; i++) {

            //Assert that all enemies' abilities isReady and use the ability
            for (Enemy enemy : enemies) {
                assert enemy.ability.isReady();
                enemy.ability.setTarget(Gameplay.getInstance().player);
                enemy.ability.tryUseAbility();
            }
            Thread.sleep(10);//Waiting for game thread updates via sleep

            //Asserts that all enemies' abilities are in use
            for (Enemy enemy : enemies) {
                assert enemy.ability.inUse();
                assert (enemy.ability.getUseTimeTiming() > 0);
                assert (enemy.ghostMode);
            }
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getUseTimeTiming() + 0.1f);//speed up ability getUseTimeTiming()

            Thread.sleep(10);///Waiting for game thread updates via sleep

            //When the ability duration is over, Assert that the state of all enemies' GhostMode have become disabled
            for (Enemy enemy : enemies) {
                assert (enemy.ghostMode == false);
                assert (enemy.ability.getCooldownTimeTiming() >= 0);
            }

            fastenAbilityUpdate(enemies, enemies.get(0).ability.getCooldownTimeTiming() + 0.1f);//speed up ability coolDownTimeTiming
            Thread.sleep(10);//Waiting for game thread updates via sleep
        }
    }

    @Test
    void testSpeedingUpAbility() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Enemy.randomUseAbilityRate = 0;//Disabled abilities are released randomly

        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.getInstance().player.enemyManager);

        //Replace all enemies' ability to SpeedingUpAbility
        for (Enemy enemy : enemies) {

            enemy.ability = new SpeedingUpAbility();
        }

        //Repeat i times to make sure the skill can be used repeatedly
        for (int i = 0; i <= 3; i++) {
            float speedBackup = enemies.get(0).movementSystem.speed;
            //Assert that all enemies' abilities isReady and use the ability
            for (Enemy enemy : enemies) {
                assert enemy.ability.isReady();
                enemy.ability.setTarget(Gameplay.getInstance().player);
                enemy.ability.tryUseAbility();
            }
            Thread.sleep(10);//Waiting for game thread updates via sleep

            //Asserts that all enemies' abilities are in use
            for (Enemy enemy : enemies) {
                assert enemy.ability.inUse();
                assert (enemy.ability.getUseTimeTiming() > 0);
                assert (enemy.movementSystem.speed == speedBackup * 3.0);
            }
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getUseTimeTiming() + 0.1f);//speed up ability getUseTimeTiming()

            Thread.sleep(10);//Waiting for game thread updates via sleep

            //When the ability duration is over, Assert that the state of all enemies' SpeedingUpAbility have become disabled
            for (Enemy enemy : enemies) {
                assert (Math.abs(enemy.movementSystem.speed - speedBackup) <= 0.1);
                assert (enemy.ability.getCooldownTimeTiming() >= 0);
            }

            fastenAbilityUpdate(enemies, enemies.get(0).ability.getCooldownTimeTiming() + 0.1f);//speed up ability coolDownTimeTiming
            Thread.sleep(10);//Waiting for game thread updates via sleep
        }
    }

    @Test
    void testHigherSystemDamagerAbility() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Enemy.randomUseAbilityRate = 0;//Disabled abilities are released randomly

        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.getInstance().player.enemyManager);


        //Replace all enemies' ability to HigherSystemDamagerAbility
        for (Enemy enemy : enemies) {

            enemy.ability = new HigherSystemDamageAbility();
        }

        //Repeat i times to make sure the skill can be used repeatedly
        for (int i = 0; i <= 3; i++) {
            float damageBackup = enemies.get(0).systemDamage;
            //Assert that all enemies' abilities isReady and use the ability
            for (Enemy enemy : enemies) {
                assert enemy.ability.isReady();
                enemy.ability.setTarget(Gameplay.getInstance().player);
                enemy.ability.tryUseAbility();
            }
            Thread.sleep(10);//Waiting for game thread updates via sleep

            //Asserts that all enemies' abilities are in use
            for (Enemy enemy : enemies) {
                assert enemy.ability.inUse();
                assert (enemy.ability.getUseTimeTiming() > 0);
                assert (enemy.systemDamage == damageBackup * 2);
            }
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getUseTimeTiming() + 0.1f);//speed up ability getUseTimeTiming()

            Thread.sleep(10);//Waiting for game thread updates via sleep

            //When the ability duration is over, Assert that the state of all enemies' HigherSystemDamagerAbility have become disabled
            for (Enemy enemy : enemies) {
                assert (Math.abs(enemy.systemDamage - damageBackup) <= 0.1);
                assert (enemy.ability.getCooldownTimeTiming() >= 0);
            }

            fastenAbilityUpdate(enemies, enemies.get(0).ability.getCooldownTimeTiming() + 0.1f);//speed up ability coolDownTimeTiming
            Thread.sleep(10);//Waiting for game thread updates via sleep
        }
    }

    @Test
    void testSlowDownPlayerAbility() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Enemy.randomUseAbilityRate = 0;//Disabled abilities are released randomly

        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.getInstance().player.enemyManager);


        //Replace all enemies' ability to SlowDownPlayerAbility
        for (Enemy enemy : enemies) {

            enemy.ability = new SlowDownTargetAbility();
        }

        //Repeat i times to make sure the skill can be used repeatedly
        for (int i = 0; i <= 3; i++) {
            float speedBackup = Gameplay.getInstance().player.movementSystem.speed;
            //Assert that all enemies' abilities isReady and use the ability
            for (Enemy enemy : enemies) {
                assert enemy.ability.isReady();
                enemy.ability.setTarget(Gameplay.getInstance().player);
                enemy.ability.tryUseAbility();
            }
            Thread.sleep(10);//Waiting for game thread updates via sleep

            //Asserts that all enemies' abilities are in use
            for (Enemy enemy : enemies) {
                assert enemy.ability.inUse();
                assert (enemy.ability.getUseTimeTiming() > 0);
            }
            assert (Gameplay.getInstance().player.movementSystem.speed == speedBackup * Math.pow(0.5f, enemies.size()));
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getUseTimeTiming() + 0.1f);//speed up ability getUseTimeTiming()

            Thread.sleep(10);//Waiting for game thread updates via sleep

            //When the ability duration is over, Assert that the state of all enemies' SlowDownPlayerAbility have become disabled
            for (Enemy enemy : enemies) {
                assert (enemy.ability.getCooldownTimeTiming() >= 0);
            }
            assert (Gameplay.getInstance().player.movementSystem.speed == speedBackup);
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getCooldownTimeTiming() + 0.1f);//speed up ability coolDownTimeTiming
            Thread.sleep(10);//Waiting for game thread updates via sleep
        }
    }


    @Test
    void testStopPlayerAbility() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Enemy.randomUseAbilityRate = 0;//Disabled abilities are released randomly

        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.getInstance().player.enemyManager);


        //Replace all enemies' ability to StopPlayerAbility
        for (Enemy enemy : enemies) {

            enemy.ability = new StopTargetAbility();
        }

        //Repeat i times to make sure the skill can be used repeatedly
        for (int i = 0; i <= 3; i++) {
            float speedBackup = Gameplay.getInstance().player.movementSystem.speed;
            //Assert that all enemies' abilities isReady and use the ability
            for (Enemy enemy : enemies) {
                assert enemy.ability.isReady();
                enemy.ability.setTarget(Gameplay.getInstance().player);
                enemy.ability.tryUseAbility();
            }
            Thread.sleep(10);//Waiting for game thread updates via sleep

            //Asserts that all enemies' abilities are in use
            for (Enemy enemy : enemies) {
                assert enemy.ability.inUse();
                assert (enemy.ability.getUseTimeTiming() > 0);
            }
            assert (Gameplay.getInstance().player.cantMove);
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getUseTimeTiming() + 0.1f);//speed up ability getUseTimeTiming()

            Thread.sleep(10);//Waiting for game thread updates via sleep

            //When the ability duration is over, Assert that the state of all enemies' StopPlayerAbility have become disabled
            for (Enemy enemy : enemies) {
                assert (enemy.ability.getCooldownTimeTiming() >= 0);
            }
            assert (Gameplay.getInstance().player.cantMove == false);
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getCooldownTimeTiming() + 0.1f);//speed up ability coolDownTimeTiming
            Thread.sleep(10);//Waiting for game thread updates via sleep
        }
    }


    @Test
    void testAttackPlayerAbility() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Enemy.randomUseAbilityRate = 0;//Disabled abilities are released randomly

        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.getInstance().player.enemyManager);


        //Replace all enemies' ability to AttackPlayerAbility
        for (Enemy enemy : enemies) {

            enemy.ability = new AttackPlayerAbility();
        }

        //Repeat i times to make sure the skill can be used repeatedly
        for (int i = 0; i <= 3; i++) {
            float healthBackup = Gameplay.getInstance().player.health;
            float DAMAGE = ((AttackPlayerAbility) enemies.get(0).ability).DAMAGE;
            //Assert that all enemies' abilities isReady and use the ability
            for (Enemy enemy : enemies) {
                assert enemy.ability.isReady();
                enemy.ability.setTarget(Gameplay.getInstance().player);
                enemy.ability.tryUseAbility();
                AttackPlayerAbility ability = (AttackPlayerAbility) enemy.ability;
                ability.contact = true;
            }

            Thread.sleep(10);//Waiting for game thread updates via sleep

            //Asserts that all enemies' abilities are in use
            for (Enemy enemy : enemies) {
                assert enemy.ability.inUse();
                assert (enemy.ability.getUseTimeTiming() > 0);
            }

            fastenAbilityUpdate(enemies, 0.1f);//Make sure the damage has been done to the player
            assert (Gameplay.getInstance().player.health <= (healthBackup - enemies.size() * DAMAGE * 0.1f));

            //Mark out of contact  to avoid excessive sustained damage
            for (Enemy enemy : enemies) {
                AttackPlayerAbility ability = (AttackPlayerAbility) enemy.ability;
                ability.contact = false;
            }

            healthBackup = Gameplay.getInstance().player.health;
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getUseTimeTiming() + 0.1f);//speed up ability getUseTimeTiming()

            Thread.sleep(10);//Waiting for game thread updates via sleep

            //When the ability duration is over, Assert that the state of all enemies' AttackPlayerAbility have become disabled
            for (Enemy enemy : enemies) {
                assert (enemy.ability.getCooldownTimeTiming() >= 0);
            }

            assert (Gameplay.getInstance().player.health <= healthBackup);
            fastenAbilityUpdate(enemies, enemies.get(0).ability.getCooldownTimeTiming() + 0.1f);//speed up ability coolDownTimeTiming
            Thread.sleep(10);//Waiting for game thread updates via sleep
        }
    }
    void fastenAbilityUpdate(IAbility ability, float delta) {
        ability.update(delta);
    }
    void enemiesSabotageSystems(boolean reinforced) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.getInstance().player.enemyManager);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            Systems systems = Gameplay.getInstance().systems.get(i);
            float hp = systems.hp;
            enemy.set_target_system(systems);
            enemy.sabotage(systems);
            assert  hp == systems.hp == reinforced;
        }
    }
    @Test
    void testSystemReinforce() throws NoSuchFieldException, IllegalAccessException, InterruptedException {

//        ArrayList<Enemy> enemies = getAllEnemies(Gameplay.player.enemyManager);
        ReinforcedSystemsAbility ability = null;
        for (Map.Entry<Integer, IAbility> playerAbility:Gameplay.getInstance().player.abilityMap.entrySet()){
            if (playerAbility.getValue() instanceof ReinforcedSystemsAbility){
                ability = (ReinforcedSystemsAbility) playerAbility.getValue();
                break;
            }
        }
        assert ability != null;

        for (Systems systems: Gameplay.getInstance().systems){
            assert systems.isReinforced() == false;
        }
        assert ability.isReady();

        enemiesSabotageSystems(false);


        ability.tryUseAbility();
        assert ability.isReady() == false;

        enemiesSabotageSystems(true);


        for (Systems systems: Gameplay.getInstance().systems){
            assert systems.isReinforced() == true;
        }
        fastenAbilityUpdate(ability,ability.useTimeTiming);
        Thread.sleep(10);

        for (Systems systems: Gameplay.getInstance().systems){
            assert systems.isReinforced() == false;
        }
        enemiesSabotageSystems(false);
    }

    @Test
    void testGlobalSlowDown() throws InterruptedException {
        GlobalSlowDownAbility ability = null;
        for (Map.Entry<Integer, IAbility> playerAbility:Gameplay.getInstance().player.abilityMap.entrySet()){
            if (playerAbility.getValue() instanceof GlobalSlowDownAbility){
                ability = (GlobalSlowDownAbility) playerAbility.getValue();
                break;
            }
        }
        assert ability != null;

        for (Enemy enemy:Gameplay.getInstance().player.enemyManager.getActiveEnemies()){
            assert  enemy.movementSystem.speed == 1000.0f;
        }

        assert ability.isReady();
        ability.tryUseAbility();
        assert ability.isReady() == false;

        for (Enemy enemy:Gameplay.getInstance().player.enemyManager.getActiveEnemies()){
            assert  enemy.movementSystem.speed == 1000.0f / GlobalSlowDownAbility.SLOWDOWN;
        }
        fastenAbilityUpdate(ability,ability.useTime);
        Thread.sleep(10);

        for (Enemy enemy:Gameplay.getInstance().player.enemyManager.getActiveEnemies()){
            assert Math.abs( enemy.movementSystem.speed- 1000.0f) <= 0.001;
        }

    }

    @Test
    void testMarkInfiltratorAbility() throws InterruptedException {
        MarkInfiltratorAbility ability = null;
        for (Map.Entry<Integer, IAbility> playerAbility:Gameplay.getInstance().player.abilityMap.entrySet()){
            if (playerAbility.getValue() instanceof MarkInfiltratorAbility){
                ability = (MarkInfiltratorAbility) playerAbility.getValue();
                break;
            }
        }
        assert ability != null;

        for (Enemy enemy:Gameplay.getInstance().player.enemyManager.getActiveEnemies()){
            assert enemy.beMarked == false;
        }

        assert ability.isReady();
        ability.tryUseAbility();
        assert ability.isReady() == false;

        Enemy enemy = Gameplay.getInstance().player.enemyManager.getClosestActiveEnemy(Gameplay.getInstance().player.getPosition());
        if (enemy == null){
            for (Enemy enemy1:Gameplay.getInstance().player.enemyManager.getActiveEnemies()){
                assert enemy1.beMarked == false;
            }
        }else{
            assert  enemy.beMarked;
        }


        fastenAbilityUpdate(ability,ability.useTime);
        Thread.sleep(10);
        for (Enemy enemy1:Gameplay.getInstance().player.enemyManager.getActiveEnemies()){
            assert enemy1.beMarked == false;
        }
    }
}