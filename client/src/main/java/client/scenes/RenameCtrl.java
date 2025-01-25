package client.scenes;

import client.utils.ServerConnection;
import com.google.inject.Inject;
import commons.Note;
import commons.EmbeddedFile;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import java.text.MessageFormat;

public class RenameCtrl {
    @FXML
    private TextField fileNameInput;

    private Note currentNote;
    private EmbeddedFile currentFile;

    private MainCtrl mainCtrl;
    private ServerConnection server;

    /**
     * Constructs a new NoteOverviewCtrl with the specified server and main controller.
     *
     * @param server    the server utils instance for interacting with the server
     * @param mainCtrl  the main controller of the application
     */
    @Inject
    public RenameCtrl(ServerConnection server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    /** 
     * called when clicked on ok button
     */
    public void ok() {
        String fullFileName = fileNameInput.getText() + currentFile.getFiletype();
        if (currentNote
                .getEmbeddedFiles()
                .stream()
                .map(EmbeddedFile::getFilename)
                .anyMatch(name -> name.toLowerCase().equals(fullFileName.toLowerCase()))
        ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(MessageFormat.format(
                    resourceBundle.getString("alert.file.renameError"), fullFileName));
            alert.showAndWait();
            return;
        }
        String oldFilename = currentFile.getFilename();
        try {
            server.renameFile(currentNote.id, currentFile.getFilename(), fullFileName);
        } catch (WebApplicationException e) {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.initModality(Modality.APPLICATION_MODAL);
            alert2.setContentText(e.getMessage());
            alert2.showAndWait();
        } finally {
            mainCtrl.showOverview();
        }
    }
    /**
     *  called when clicked on cancel button
    */
    public void cancel() {
        mainCtrl.showOverview();
    }

    public void setCurrentNote(Note currentNote, EmbeddedFile currentFile) {
        this.currentNote = currentNote;
        String fileName = currentFile.getFilename();
        String fileNameNoExt = fileName.substring(0, fileName.lastIndexOf("."));
        fileNameInput.setText(fileNameNoExt);
    }
    // titleInput is fx:id of text field where you can enter the new filename

    // scene is called rename.fxml
    // to switch scenes the implementation still needs to be added though, similar way to switching to editcollection.fxml

    // listview of files should be controlled in NoteOverviewCtrl, similarly to listview of notes
    // fx:id for listview of files is listEmbeddedFiles
}
