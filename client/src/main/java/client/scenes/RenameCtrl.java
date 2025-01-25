package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class RenameCtrl {
    @FXML
    private TextField fileNameInput;

    /** 
     * called when clicked on ok button
     */
    public void ok() {
        
        // ok button fx:id is renameFileOkButton
    }
    /**
     *  called when clicked on cancel button
    */
    public void cancel() {
        // cancel button fx:id is renameFileCancelButton
    }

    String getText() {
        return "";
    }


    // titleInput is fx:id of text field where you can enter the new filename

    // scene is called rename.fxml
    // to switch scenes the implementation still needs to be added though, similar way to switching to editcollection.fxml

    // listview of files should be controlled in NoteOverviewCtrl, similarly to listview of notes
    // fx:id for listview of files is listEmbeddedFiles
}
