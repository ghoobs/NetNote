package client.scenes;

import client.utils.ServerUtils2;
import com.google.inject.Inject;
import commons.Note;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;

import java.net.URL;
//import java.util.Arrays;
//import java.util.List;
import java.util.List;
import java.util.ResourceBundle;
//import java.util.stream.Collectors;

public class NoteOverviewCtrl implements Initializable {

    private final ServerUtils2 server;
    private final MainCtrl mainCtrl;
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
    @FXML
    private TextField searchBar;
//    @FXML
//    private Button searchButton;
    @FXML
    private Button deleteButton;

    /**
     * Constructs a new NoteOverviewCtrl with the specified server and main controller.
     *
     * @param server   the server utils instance for interacting with the server
     * @param mainCtrl the main controller of the application
     */
    @Inject
    public NoteOverviewCtrl(ServerUtils2 server, MainCtrl mainCtrl) {
        this.server = server;
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
     * The title of the note can't be empty and has to be unique.
     */
    public void savingNote() {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
        if (noteSelected != null) {
            if(!titleWriting.getText().isEmpty()) {
                if(!allTitles().contains(titleWriting.getText())) {
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
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Title Already Exists");
                    alert.setHeaderText("The title of your note has to be unique!");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            System.out.println("test");
                        }
                    });
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Title");
                alert.setHeaderText("The title of your note can't be empty!");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        System.out.println("test");
                    }
                });
            }
        }
    }

    /**
     * Returns a list of titles, only used internally in the savingNote function
     *
     * @return a List of titles
     */
    private List<String> allTitles(){
        return listNotes.getItems().stream()
                .map(n -> n.getTitle())
                .toList();
    }

    /**
     * Deletes the currently selected note from the server, and the list
     */
    public void deleteNote() {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();

        if (noteSelected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Note");
            alert.setHeaderText("Are you sure you want to delete this note?");
            alert.setContentText(noteSelected.getTitle());

            alert.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK) {
                    try {
                        server.deleteNote(noteSelected);
                        listNotes.getItems().remove(noteSelected);
                        listNotes.refresh();
                    } catch (Exception e) {
                        //error if delete fails
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Deletion Failed");
                        alert2.setHeaderText("Error occurred during deletion");
                        alert2.setContentText(e.getMessage());
                        alert2.showAndWait();
                    }
                }
            });
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
     * Handles note selection in the list and displays the selected note's details.
     * Sets the UI to non-editable mode for the selected note.
     *
     * @param mouseEvent the mouse event that triggered this action
     */
    public void onNoteClicked(MouseEvent mouseEvent) {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
        if(noteSelected != null) {
            noteWriting.setEditable(false);
            titleWriting.setEditable(false);
            noteWriting.setText(noteSelected.getText());
            titleWriting.setText(noteSelected.getTitle());
        }
    }

    /**
     * Refreshes the note list by fetching the latest data from the server.
     */
    public void refresh() {
        var notes = server.getNotes();
        data = FXCollections.observableList(notes);
        listNotes.setItems(data);
    }

    /**
     * Handles keyboard shortcuts for refreshing and
     * saving, editing, adding and deleting (soon) notes.
     * Pressing ESC sets the input focus to the search bar.
     * Allows user to go through the list of notes using arrows and open them using O
     *
     * @param keyEvent the key event triggered by the user
     */
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ESCAPE){
            searchBar.requestFocus();
        }
        if(listNotes.isFocused() && keyEvent.getCode() == KeyCode.O){
            Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
            if(noteSelected != null) {
                noteWriting.setEditable(false);
                titleWriting.setEditable(false);
                noteWriting.setText(noteSelected.getText());
                titleWriting.setText(noteSelected.getTitle());
            }
        }
        if(keyEvent.isShortcutDown()) {
            switch (keyEvent.getCode()) {
                case S:
                    saveNote();
                    break;
                case R:
                    refresh();
                    break;
                case N:
                    addNote();
                    break;
                case E:
                    editNote();
                    break;
                case D:
                    //delete note
                    break;
            }
        }
    }
}
