package client.scenes;

import client.markdown.MarkdownHandler;
import client.utils.ServerUtils2;
import com.google.inject.Inject;
import commons.Note;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;

import java.net.URL;
//import java.util.Arrays;
//import java.util.List;
import java.util.ResourceBundle;
//import java.util.stream.Collectors;

public class NoteOverviewCtrl implements Initializable {

    private final ServerUtils2 server;
    private final MainCtrl mainCtrl;
    private final MarkdownHandler mdHandler;
    private ObservableList<Note> data;

    @FXML
    private ListView<Note> listNotes;
    @FXML
    TextArea noteWriting;
    @FXML
    private TextField titleWriting;
    @FXML
    private WebView markDownView;
    @FXML
    private Button saveButton;
    @FXML
    private Button editButton;
    @FXML
    private Button addButton;
//    @FXML
//    private TextField searchBar;
//    @FXML
//    private Button searchButton;
//    @FXML
//    private Button deleteButton;

    /**
     * Constructs a new NoteOverviewCtrl with the specified server and main controller.
     *
     * @param server   the server utils instance for interacting with the server
     * @param mdHandler the markdown renderer instance to update the webview asynchronously
     * @param mainCtrl the main controller of the application
     */

    @Inject
    public NoteOverviewCtrl(ServerUtils2 server, MarkdownHandler mdHandler, MainCtrl mainCtrl) {
        this.server = server;
        this.mdHandler = mdHandler;
        this.mainCtrl = mainCtrl;
    }



    /**
     * Initializes the note overview interface.
     * Sets up the observable data list, configures UI components, and populates initial data.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data = FXCollections.observableArrayList();
        listNotes.setItems(data);
        noteWriting.setEditable(false);
        titleWriting.setEditable(false);
        listNotes.setOnMouseClicked(this::onNoteClicked);

        mdHandler.createMdParser(MarkdownHandler.getDefaultExtensions());
        mdHandler.setWebEngine(markDownView.getEngine());
        mdHandler.setHyperlinkCallback((String link)->{
            System.out.println("Webpage: " + link);
        });
        mdHandler.launchAsyncWorker(); // TODO: make sure to dispose when ctrl is closed or something
//        searchButton.setOnAction(event -> searchNotes());
        refresh();
    }

    /**
     * Displays the note adding scene to create a new note
     */

    public void addNote() {
        mainCtrl.showAdd();
    }

    /**
     * Adds a new note to the list and updates the server with the new note.
     * If an error occurs during the server update, an error alert is displayed.
     */

    public void addingNote() {
        Note newNote = new Note("New Note", "");
        try {
            server.addNote(newNote);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        data.add(newNote);
        saveButton.setDisable(false);
        editButton.setDisable(false);
        listNotes.getSelectionModel().select(newNote);
        noteWriting.setEditable(true);
        titleWriting.setEditable(true);
        noteWriting.setText(newNote.getText());
        titleWriting.setText(newNote.getTitle());
    }

    /**
     * Displays the note saving scene to save the current note
     */

    public void saveNote() {
        mainCtrl.showSave();
    }

    /**
     * Saves changes made to the selected note and updates the server when the "Save" button is clicked.
     * Ensures the note is set to non-editable mode after saving.
     */

    public void savingNote() {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
        if (noteSelected != null) {
            noteSelected.setText(noteWriting.getText());
            noteSelected.setTitle(titleWriting.getText());
            try {
                server.updateNote(noteSelected);
            } catch (Exception e) {

            }
            listNotes.refresh();
            noteWriting.setEditable(false);
            titleWriting.setEditable(false);
        }
    }

    /**
     * Displays the note editing scene to start editing the note
     */

    public void editNote() {
        mainCtrl.showEdit();
    }


    /**
     * Enables editing mode for the currently selected note when the "Edit" button is clicked.
     */

    public void editingNote() {
        noteWriting.setEditable(true);
        titleWriting.setEditable(true);
    }

    /**
     * Updates markdown when input is typed into note contents
     */
    public void updateMarkdown() {
        mdHandler.asyncMarkdownUpdate(noteWriting.getText());
    }

    /**
     * Handles note selection in the list and displays the selected note's details.
     * Sets the UI to non-editable mode for the selected note.
     *
     * @param mouseEvent the mouse event that triggered this action
     */

    public void onNoteClicked(MouseEvent mouseEvent) {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
        noteWriting.setEditable(false);
        titleWriting.setEditable(false);
        noteWriting.setText(noteSelected.getText());
        titleWriting.setText(noteSelected.getTitle());

    }

    /**
     * Refreshes the note list by fetching the latest data from the server.
     */

    public void refresh() {
        var notes = server.getNotes();
        data = FXCollections.observableList(notes);
        listNotes.setItems(data);
    }
}
