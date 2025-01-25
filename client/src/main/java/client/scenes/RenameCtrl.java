package client.scenes;

public class RenameCtrl {

    public void ok(){
        // called when clicked on ok button
        // ok button fx:id is renameFileOkButton
    }

    public void cancel(){
        // called when clicked on cancel button
        // cancel button fx:id is renameFileCancelButton
    }

    // titleInput is fx:id of text field where you can enter the new filename

    // scene is called rename.fxml
    // to switch scenes the implementation still needs to be added though, similar way to switching to editcollection.fxml

    // listview of files should be controlled in NoteOverviewCtrl, similarly to listview of notes
    // fx:id for listview of files is listEmbeddedFiles
}
