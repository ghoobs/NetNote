package client.scenes;

import com.google.inject.Inject;
import javafx.scene.input.KeyEvent;

public class EditCtrl {
    private final MainCtrl mainCtrl;

    /**
     * Constructs an EditCtrl instance with the main controller
     *
     * @param mainCtrl the main controller used for scene navigation
     */

    @Inject
    public EditCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Cancels the editing operation and returns to the note overview scene
     */

    public void cancel(){
        mainCtrl.showOverview();
    }

    /**
     * Confirms the editing operation and returns to the note overview scene
     */

    public void ok(){
        mainCtrl.getOverviewCtrl().editingNote();
        mainCtrl.showOverview();
    }

    /**
     * Handles key events during note editing, such as confirming changes with ENTER
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

