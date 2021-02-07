package screen.actors;

import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.security.Key;

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
    public void updateAbilities(Player auber) {

        StringBuilder sb = new StringBuilder();
        for (int key:auber.abilityMap.keySet()) {
            sb.append(Input.Keys.toString(key) +": "+ auber.abilityMap.get(key).toString()+"\n");
        }

        setText(sb.toString());

    }
}



