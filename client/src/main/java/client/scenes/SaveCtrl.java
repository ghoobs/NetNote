package client.scenes;

import client.scenes.MainCtrl;
import com.google.inject.Inject;
import javafx.scene.input.KeyEvent;

public class SaveCtrl {
    private final MainCtrl mainCtrl;

    /**
     * Constructs an SaveCtrl instance with the main controller
     *
     * @param mainCtrl the main controller used for scene navigation
     */

    @Inject
    public SaveCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Cancels the saving operation and returns to the note overview scene
     */

    public void cancel(){
        mainCtrl.showOverview();
    }

    /**
     * Confirms the saving operation and returns to the note overview scene
     */

    public void ok(){
        mainCtrl.getOverviewCtrl().savingNote();
        mainCtrl.showOverview();
    }

    /**
     * Handles key events during note saving, such as confirming changes with ENTER
     * or cancelling with ESCAPE.
     *
     * @param keyEvent the key event triggered by the user
     */

    public void keyPressed(KeyEvent keyEvent) {
        switch(keyEvent.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                cancel();
                break;
        }
    }

}
