package screen.actors;

import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AbilityFooter extends Label {

    private final String ability = "";
    private final String abilityName = "";

    /**
     * Label to show the number of arrested enemies.
     */
    public AbilityFooter() {

        super("A:Arrest\n" +
                "S: Slowdown \t CD: 0/8\n" +
                "D: Reinforced Systems CD:0/8\n" +
                "F: Mark Infiltrators CD:0/8", new Skin(Gdx.files.internal("skin/hudskin/comic-ui.json")), "title");
        setName("ability" + abilityName);
        getStyle().font.getData().setScale(.45f, .45f);
        setColor(Color.WHITE);

    }

    /**
     * update the number of arrested enemies.
     *
     * @param auber player
     */
    public void update_abilities(Player auber) {
        int jailedCount = auber.enemyManager.getJailedCount();
        String speedUpText = "S: Slowdown ";
        if (auber.globalSlowDownAbility.inUse()) {
            speedUpText += "Using\n";
        } else if (auber.globalSlowDownAbility.isReady) {
            speedUpText += "Ready\n";
        } else {
            speedUpText += String.format(" CD:%.2f/%.2f\n", auber.globalSlowDownAbility.getCooldownTimeTiming(), auber.globalSlowDownAbility.getCooldownTime());
        }

        String reinforcedText = "D: Reinforced Systems ";
        reinforcedText += auber.reinforcedSystemsAbility.isReady ? "Ready\n" : "Used\n";

        String markInfiltratorText = "F: Mark Infiltrators ";
        if (auber.markInfiltratorAbility.inUse()) {
            markInfiltratorText += "Using\n";
        } else if (auber.markInfiltratorAbility.isReady) {
            markInfiltratorText += "Ready\n";
        } else {
            markInfiltratorText += String.format(" CD:%.2f/%.2f\n", auber.markInfiltratorAbility.getCooldownTimeTiming(), auber.markInfiltratorAbility.getCooldownTime());
        }

        setText(speedUpText + reinforcedText + markInfiltratorText);
        if (jailedCount > 0) {
            setColor(Color.GREEN);
        }
    }


}
