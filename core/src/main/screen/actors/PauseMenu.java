package screen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import screen.Gameplay;

public class PauseMenu extends Menu {
    private boolean exiting;
    TextButton resumeButton = new TextButton("Resume", skin);
    TextButton saveButton = new TextButton("Save", skin);
    TextButton settingsButton = new TextButton("Settings", skin);
    TextButton exitButton = new TextButton("Exit", skin);
    Label titleLabel = new Label("Paused", skin);
    SettingsMenu settingsMenu;

    /**
     * The pause menu for the game.
     *
     * @param settingsMenu the settings menu to include
     */
    public PauseMenu(SettingsMenu settingsMenu) {
        super("Pause");

        this.settingsMenu = settingsMenu;

        // Adds actors to the menu
        resumeButton.setName("Resume");
        saveButton.setName("Save");
        settingsButton.setName("Settings");
        exitButton.setName("Exit");
        titleLabel.setName("Title");

        window.add(resumeButton);
        window.add(saveButton);
        window.add(settingsButton);
        window.add(exitButton);
        window.add(titleLabel);

        window.findActor("Resume").setPosition(this.window.getWidth() / 2
                        - resumeButton.getWidth() / 2,
                this.window.getHeight() * 7 / 10 - resumeButton.getHeight() / 2);

        window.findActor("Save").setPosition(this.window.getWidth() / 2
                        - saveButton.getWidth() / 2,
                this.window.getHeight() * 5 / 10 - saveButton.getHeight() / 2);

        window.findActor("Settings").setPosition(this.window.getWidth() / 2
                        - settingsButton.getWidth() / 2,
                this.window.getHeight() * 3 / 10 - saveButton.getHeight() / 2);

        window.findActor("Exit").setPosition(this.window.getWidth() / 2
                        - exitButton.getWidth() / 2,
                this.window.getHeight() * 1 / 10 - exitButton.getHeight() / 2);

        window.findActor("Title").setPosition(this.window.getWidth() / 2
                        - titleLabel.getWidth() / 2,
                this.window.getHeight() * 9 / 10 - titleLabel.getHeight() / 2);
    }

    /**
     * shows the menu.
     */
    @Override
    public void show() {
        super.show();

        // Event listeners for buttons
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false;
            }
        });
        // TODO Provide a prompt to the user
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gameplay.getInstance().requestSave();
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exiting = true;
            }
        });
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsMenu.show();
                PauseMenu.super.hide();
            }
        });
    }

    @Override
    public void hide() {
        super.hide();
        this.settingsMenu.hide();
    }

    public Window pauseWindow() {
        return this.window;
    }

    /**
     * if the game should resume.
     *
     * @return whether or not gameplay should resume
     */
    public boolean resume() {
        if (settingsMenu.resume() && this.paused) {
            this.show();
        }
        return (!this.paused) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);

    }

    public boolean exit() {
        return exiting;
    }

}
