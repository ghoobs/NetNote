package client.scenes;

import client.utils.ServerUtils2;
import com.google.inject.Inject;
import commons.Collection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

public class EditCollectionCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils2 server;

    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;

    /**
     * Constructs an EditCollectionCtrl instance with the main controller
     *
     * @param mainCtrl the main controller used for scene navigation
     */

    @Inject
    public EditCollectionCtrl(ServerUtils2 server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Goes back to the note overview scene
     */
    public void back(){
        mainCtrl.showOverview();
    }

    /**
     * Handles key events such as going back to the note overview scene
     *
     * @param keyEvent the key event triggered by the user
     */
    public void keyPressed(KeyEvent keyEvent) {
        switch(keyEvent.getCode()) {
            case ESCAPE:
                back();
                break;
        }
    }

}
