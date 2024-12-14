package client.scenes;

import com.google.inject.Inject;
import commons.Note;
import javafx.scene.input.KeyEvent;

public class DeleteCtrl {
    private final MainCtrl mainCtrl;

    /**
     * Constructs an DeleteCtrl instance with the main controller
     *
     * @param mainCtrl the main controller used for scene navigation
     */

    @Inject
    public DeleteCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Cancels the deletion operation and returns to the note overview scene
     */

    public void cancel(){
        mainCtrl.showOverview();
    }

    /**
     * Confirms the deletion operation and returns to the note overview scene
     */

    public void ok(){
        mainCtrl.getOverviewCtrl().deleteNote();
        mainCtrl.showOverview();
    }

    /**
     * Handles key events during note deletion, such as confirming changes with ENTER
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
