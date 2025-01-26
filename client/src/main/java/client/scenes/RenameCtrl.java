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
import javafx.stage.Modality;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.IOException;


public class RenameCtrl {
    @FXML
    private TextField fileNameInput;

    @FXML
    private Label enterNewNameLabel;

    @FXML
    private Button renameFileCancelButton;

    @FXML
    private Button renameFileOkButton;

    @FXML
    private TitledPane renamePane;

    private Note currentNote;
    private EmbeddedFile currentFile;

    private MainCtrl mainCtrl;
    private ServerConnection server;
    private ResourceBundle resourceBundle;

    /**
     * Constructs a new RenameCtrl with the specified server and main controller.
     *
     * @param server    the server utils instance for interacting with the server
     * @param mainCtrl  the main controller of the application
     */
    @Inject
    public RenameCtrl(ServerConnection server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
    @FXML
    public void initialize() {
        setLocale(loadSavedLocale());
    }


    /**
     * called when clicked on ok button
     */
    public void ok() {
        String fullFileName = fileNameInput.getText() + "." + currentFile.getFiletype();
        if (!fullFileName.matches(EmbeddedFile.REGEX_URL_NAMING_FORMAT)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(MessageFormat.format(
                    resourceBundle.getString("alert.file.renameInvalid"), fullFileName));
            alert.showAndWait();
            return;
        }
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
            alert.setTitle(resourceBundle.getString("error.title"));
            alert.setHeaderText(resourceBundle.getString("error.header"));
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
            mainCtrl.getOverviewCtrl().refresh();
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
        this.currentFile = currentFile;
        String fileName = currentFile.getFilename();
        String fileNameNoExt = fileName.substring(0, fileName.lastIndexOf("."));
        fileNameInput.setText(fileNameNoExt);
    }

    /**
     * Sets the locale for the application and updates all UI properties with localized strings.
     *
     * @param locale the {@code Locale} to set for the application.
     */
    public void setLocale(Locale locale) {
        this.resourceBundle = ResourceBundle.getBundle("bundle", locale);
        refreshTexts();
    }

    /**
     * Loads the saved locale from the configuration file.
     * If no locale is saved, defaults to English.
     *
     * @return the {@code Locale} loaded from the configuration file or the default {@code Locale.ENGLISH}.
     */
    protected Locale loadSavedLocale() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            return new Locale(props.getProperty("language", "en"));
        } catch (IOException e) {
            return Locale.ENGLISH;
        }
    }

    /**
     * Refreshes the text of all translatable elements in the UI.
     */
    private void refreshTexts() {
        renamePane.setText(resourceBundle.getString("renamePane.title"));
        enterNewNameLabel.setText(resourceBundle.getString("enterNewNameLabel"));
        renameFileCancelButton.setText(resourceBundle.getString("renameFileCancelButton"));
        renameFileOkButton.setText(resourceBundle.getString("renameFileOkButton"));
    }

    /**
     * Handles key events such as going back to the note overview scene
     *
     * @param keyEvent the key event triggered by the user
     */
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            ok();
            keyEvent.consume();
        }
        if(keyEvent.getCode() == KeyCode.ESCAPE){
            cancel();
            keyEvent.consume();
        }
    }
}
