package screen.actors;

import characters.Entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ArrestedHeader extends Label {

    /**
     * Label to show the number of arrested enemies.
     */
    public ArrestedHeader() {

        super("Arrested: 0, Jailed: 0/8", new Skin(Gdx.files.internal("skin/hudskin/comic-ui.json")), "title");
        setName("ArrestCount");
        getStyle().font.getData().setScale(.45f, .45f);
        setColor(Color.WHITE);

    }

    /**
     * update the number of arrested enemies.
     *
     * @param auber player
     */
    public void update_Arrested(Player auber) {
        int jailedCount = auber.enemyManager.getJailedCount();
        setText("Arrested: " + auber.enemyManager.getArrestedCount() + ", Jailed: " + jailedCount + "/8");
        if (jailedCount > 0) {
            setColor(Color.GREEN);
        }
    }


}
