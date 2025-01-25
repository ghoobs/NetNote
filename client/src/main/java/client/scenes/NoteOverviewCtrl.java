package client.scenes;

import client.markdown.IMarkdownEvents;
import client.markdown.MarkdownHandler;
import client.utils.CollectionServerUtils;
import client.utils.*;
import client.markdown.*;

import client.websocket.WebSocketClient2;
import com.google.common.io.Files;
import com.google.inject.Inject;
import commons.Collection;
import commons.EmbeddedFile;
import commons.Note;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The type Note overview ctrl.
 */
public class NoteOverviewCtrl implements Initializable, IMarkdownEvents {

    private final ServerConnection server;
    private final MainCtrl mainCtrl;
    private final MarkdownHandler mdHandler;
    private WebSocketClient2 webSocketClient;

    private CollectionServerUtils colServer = new CollectionServerUtils() ;

    private Collection currentCollection;

    @FXML
    AnchorPane root;

    /**
     * The Note writing.
     */
    @FXML
    TextArea noteWriting;
    @FXML
    private Menu languageMenu;
    private final StringProperty currentLanguage = new SimpleStringProperty("Language");
    private ObservableList<Note> data;
    @FXML
    private ListView<Note> listNotes;
    @FXML
    private TextField titleWriting;
    @FXML
    private WebView markDownView;
    @FXML
    private TextField searchBar;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button editCollectionButton;
    @FXML
    private HBox tagField; // HBox to hold the tag ComboBoxes
    @FXML
    private ComboBox<String> tagComboBox; // Initial ComboBox for tags
    @FXML
    private Button clearTagsButton; // Button to reset filters
    @FXML
    private ToggleButton themeToggleButton;

    @FXML
    private ComboBox<Collection> collectionMenu;

    private boolean isDarkMode = false;

    private Set<String> activeTagFilters = new LinkedHashSet<>(); // Stores currently selected tags
    private List<ComboBox<String>> tagFilters = new ArrayList<>();
    private ObservableList<Note> filteredNotes;

    private final StringProperty propertyDeleteButton = new SimpleStringProperty();
    private final StringProperty propertyAddButton = new SimpleStringProperty();
    private final StringProperty propertySearchButton = new SimpleStringProperty();
    private final StringProperty propertySearchBarPrompt = new SimpleStringProperty();
    private final StringProperty propertyEditCollButton = new SimpleStringProperty();
    private final StringProperty propertyCollectionLabel = new SimpleStringProperty();
    private final StringProperty propertyRefreshButton = new SimpleStringProperty();
    private final StringProperty propertyClearButton = new SimpleStringProperty();
    private Locale currentLocale;
    private ResourceBundle resourceBundle;


    /**
     * Constructs a new NoteOverviewCtrl with the specified server and main controller.
     *
     * @param server    the server utils instance for interacting with the server
     * @param mdHandler the markdown renderer instance to update the webview asynchronously
     * @param mainCtrl  the main controller of the application
     */
    @Inject
    public NoteOverviewCtrl(ServerConnection server, MarkdownHandler mdHandler, MainCtrl mainCtrl) {
        this.server = server;
        this.mdHandler = mdHandler;
        this.mainCtrl = mainCtrl;
    }


    /**
     * Initializes the Note Overview Scene.
     * Sets up the ListView to display notes, configures search functionality,
     * and initializes UI components.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        languageMenu.textProperty().bind(currentLanguage);
        deleteButton.textProperty().bind(propertyDeleteButton);
        addButton.textProperty().bind(propertyAddButton);
        searchButton.textProperty().bind(propertySearchButton);
        searchBar.promptTextProperty().bind(propertySearchBarPrompt);
        editCollectionButton.textProperty().bind(propertyEditCollButton);
        refreshButton.textProperty().bind(propertyRefreshButton);
        clearTagsButton.textProperty().bind(propertyClearButton);
        root.getStyleClass().add("light-mode");
        root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        this.currentLocale = loadSavedLocale();
        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
        setLocale(currentLocale);

        data = FXCollections.observableArrayList(colServer.getCollections().stream().flatMap(x -> x.notes.stream()).toList());
        System.out.println(colServer.getCollections().stream().flatMap(x -> x.notes.stream()).map(x -> x.collection.toString()).toList());
        filteredNotes = FXCollections.observableArrayList(data);
        listNotes.setItems(filteredNotes);
        tagComboBox.setOnAction(event -> {
            String selectedTag = tagComboBox.getValue();
            if (selectedTag != null && !selectedTag.isEmpty()) {
                try {
                    applyTagFilter(selectedTag);
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Handled IndexOutOfBoundsException in tagComboBox.setOnAction.");
                }
            }
        });
        clearTagsButton.setOnAction(event -> resetFilters());
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        populateTagComboBox();

        makeEditable(noteWriting);
        titleWriting.setEditable(true);
        mdHandler.createMdParser(MarkdownHandler.getDefaultExtensions());
        mdHandler.setWebEngine(markDownView.getEngine());
        mdHandler.setEventInterface(this);
        mdHandler.launchAsyncWorker(); // TODO: make sure to dispose when ctrl is closed or something


        refreshCollectionList();
        listNotes.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                setText(null);
                setGraphic(null);
                if (!empty && note != null) {
                    String keyword = searchBar.getText().toLowerCase();
                    TextFlow highlightedTitle = createHighlightedText(note.getTitle(), keyword);
                    setGraphic(highlightedTitle);
                }
            }
        });
        //setupWebSocketClient();
        listNotes.setOnMouseClicked(this::onNoteClicked);
        createNoteTextInputContextMenu();

        collectionMenu.valueProperty().addListener((observable, oldValue, newValue) -> {currentCollection = newValue;
            refresh();});

        //when the root node is deleted from the scene, the destructor is called
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setOnCloseRequest(event -> {

                destructor();

            });
        });

        refresh();
        updateMarkdown();
    }

    private void refreshCollectionList() {
        collectionMenu.getItems().clear();
        collectionMenu.getItems().addAll(colServer.getCollections());
//        searchButton.setOnAction(event -> searchNotes());
    }


    private void destructor(){

        mdHandler.disposeAsyncWorker();
    }

    private void setupWebSocketClient() {
        new Thread(() -> {
            try {
                webSocketClient.connect("ws://your-websocket-url", this::handleWebSocketMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleWebSocketMessage(String message) {
        Platform.runLater(() -> {
            refresh(); // triggering refresh for automated change synchronization
        });
    }
    /**
     * Calls the addingnote function
     */
    public void addNote() {
        addingNote();
    }

    /**
     * Adds a new note to the list and updates the server with the new note.
     * Ensures that the title of the new note is unique.
     */
    public void addingNote() {
        String baseTitle = "New Note";
        String uniqueTitle = baseTitle;
        int counter = 1;
        while (allTitles().contains(uniqueTitle)) {
            uniqueTitle = baseTitle + " (" + counter + ")";
            counter++;
        }
        Note newNote = new Note(uniqueTitle, "");
        try {
            newNote = server.addNote(newNote);
        } catch (WebApplicationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        data.add(newNote);
        filteredNotes.add(newNote);
        listNotes.getSelectionModel().select(newNote);
        makeEditable(noteWriting);
        makeEditable(titleWriting);
        noteWriting.setText(newNote.getText());
        titleWriting.setText(newNote.getTitle());
        updateMarkdown();
        noteWriting.requestFocus();
        showNotification(resourceBundle.getString("notif.adding"));
    }



    /**
     * Calls the savingnote function
     */
    public void editedTheNote() {
        savingNote();
    }

    /**
     * Ensures the note cannot be empty or used.
     * Note gets saved automatically and is always editable.
     */
    public void savingNote() {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
        if (noteSelected != null) {
            if (!titleWriting.getText().isEmpty()) {
                if (!allTitles().contains(titleWriting.getText()) || (noteSelected.getTitle().equals(titleWriting.getText()))) {
                    noteSelected.setText(noteWriting.getText());
                    noteSelected.setTitle(titleWriting.getText());
                    updateMarkdown();
                    try {
                        server.updateNote(noteSelected);
                    } catch (Exception e) {
                        System.out.println(e);
                        //e.printStackTrace();
                    }
                    listNotes.refresh();
                    makeEditable(noteWriting);
                    makeEditable(titleWriting);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(resourceBundle.getString("alert.saving1"));
                    alert.setHeaderText(resourceBundle.getString("alert.saving2"));
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(resourceBundle.getString("alert.saving3"));
                alert.setHeaderText(resourceBundle.getString("alert.saving4"));
                alert.showAndWait();
            }
        }
    }

    /**
     * Returns a list of titles, only used internally in the savingNote function
     *
     * @return a List of titles
     */
    private List<String> allTitles() {
        return listNotes.getItems().stream()
                .map(n -> n.getTitle())
                .toList();
    }

    public void deleteNote() {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();

        if (noteSelected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(resourceBundle.getString("alert.deleting3"));
            alert.setHeaderText(resourceBundle.getString("alert.deleting4"));
            alert.setContentText(noteSelected.getTitle());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        server.deleteNote(noteSelected);
                        data.remove(noteSelected);
                        filteredNotes.remove(noteSelected);
                        listNotes.refresh();
                        listNotes.getSelectionModel().clearSelection();
                        showNotification(resourceBundle.getString("notif.deleting"));
                    } catch (Exception e) {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle(resourceBundle.getString("alert.deleting1"));
                        alert2.setHeaderText("alert.deleting2");
                        alert2.setContentText(e.getMessage());
                        alert2.showAndWait();
                    }
                }
            });
        }
    }
    /**
     * Updates markdown when input is typed into note contents
     */
    public void updateMarkdown() {
        mdHandler.asyncMarkdownUpdate(noteWriting.getText());
    }

    /**
     * Handles note selection in the list and displays the selected note's details.
     * Sets the UI to editable
     *
     * @param mouseEvent the mouse event that triggered this action
     */
    public void onNoteClicked(MouseEvent mouseEvent) {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
        if (noteSelected != null) {
            changeSelectedNote(noteSelected);
        }
    }

    /**
     * Changes the selection of the current note
     * @param note Note to select
     */
    public void changeSelectedNote(Note note) {
        makeEditable(noteWriting);
        makeEditable(titleWriting);
        noteWriting.setText(note.getText());
        titleWriting.setText(note.getTitle());
        updateMarkdown();
        listNotes.getSelectionModel().select(note);
    }

    /**
     * Refreshes the note list by fetching the latest data from the server.
     */
    public void refresh() {
        playFadeAnimation();

        var notes = server.getNotes();
        data = FXCollections.observableList(notes);
        refreshCollectionList();
        listNotes.setItems(data);
        listNotes.getSelectionModel().select(0);
        onNoteClicked(null);
        showNotification(resourceBundle.getString("notif.refreshing"));
        resetFilters();
    }

    public void refreshNoPopup() {
        var notes = server.getNotes();
        data = FXCollections.observableList(notes);
        refreshCollectionList();
        listNotes.setItems(data);
        listNotes.getSelectionModel().select(0);
        onNoteClicked(null);
    }


    private void playFadeAnimation() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), listNotes);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), listNotes);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(event -> fadeIn.play());
        fadeOut.play();
    }

    private void showNotification(String message) {
        Label notification = new Label(message);
        notification.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        notification.setOpacity(0);
        if (searchBar.getScene() == null) {
            System.err.println("Scene not found for the notification!");
            return;
        }
        var root = (Pane) searchBar.getScene().getRoot();
        notification.setLayoutX(420);
        notification.setLayoutY(320);
        root.getChildren().add(notification);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> root.getChildren().remove(notification));
        SequentialTransition sequentialTransition = new SequentialTransition(fadeIn, delay, fadeOut);
        sequentialTransition.play();
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
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            searchBar.requestFocus();
        }
        if (listNotes.isFocused() && keyEvent.getCode() == KeyCode.SHIFT) {
            keyEvent.consume();
            Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
            if (noteSelected != null) {
                makeEditable(noteWriting);
                makeEditable(titleWriting);
                noteWriting.setText(noteSelected.getText());
                titleWriting.setText(noteSelected.getTitle());
                noteWriting.requestFocus();
            }
        }
        if (searchBar.isFocused() && keyEvent.getCode() == KeyCode.ENTER){
            keyEvent.consume();
            listNotes.requestFocus();
            listNotes.getSelectionModel().select(0);
        }
        if (keyEvent.isShortcutDown()) {
            switch (keyEvent.getCode()) {
                case N:
                    addNote();
                    break;
                case S:
                    savingNote();
                    break;
                case R:
                    refresh();
                    break;
                case E:
                    editedTheNote();
                    break;
                case D:
                    deleteNote();
                    break;
            }
            keyEvent.consume();
        }
    }

    /**
     * Filters the notes based on both search and active tag filters.
     */
    private void applyFilters(String searchWord) {
        if ((searchWord == null || searchWord.isEmpty()) && activeTagFilters.isEmpty()) {
            filteredNotes.setAll(data);
            refreshNoPopup();
        } else {
            filteredNotes.setAll(data.stream()
                    .filter(note -> {
                        List<String> noteTags = extractTags(note.getText());
                        boolean matchesSearch = (searchWord == null || searchWord.isEmpty()) ||
                                note.getTitle().toLowerCase().contains(searchWord.toLowerCase()) ||
                                note.getText().toLowerCase().contains(searchWord.toLowerCase());
                        boolean matchesTags = activeTagFilters.isEmpty() ||
                                activeTagFilters.stream().allMatch(noteTags::contains);

                        return matchesSearch && matchesTags; // Must satisfy both
                    })
                    .toList());
        }
        listNotes.setItems(filteredNotes); // Update ListView
    }
    /**
     * Highlights the title and content of the currently selected note based on the search keyword.
     *
     * @param keyword the keyword to highlight
     */
    private void highlightSelectedNote(String keyword) {
        Note noteSelected = listNotes.getSelectionModel().getSelectedItem();
        if (noteSelected != null) {
            titleWriting.clear();
            TextFlow highlightedTitle = createHighlightedText(noteSelected.getTitle(), keyword);
            titleWriting.setText(noteSelected.getTitle());
            noteWriting.clear();
            TextFlow highlightedContent = createHighlightedText(noteSelected.getText(), keyword);
            noteWriting.setText(noteSelected.getText());
        }
    }

    /**
     * Creates a TextFlow with the keyword highlighted in the given text.
     *
     * @param text    the full text to highlight
     * @param keyword the keyword to highlight
     * @return a TextFlow containing the highlighted text
     */
    private TextFlow createHighlightedText(String text, String keyword) {
        TextFlow textFlow = new TextFlow();
        if (keyword == null || keyword.isEmpty()) {
            textFlow.getChildren().add(new Text(text));
            return textFlow;
        }

        int lastIndex = 0;
        int keywordIndex = text.toLowerCase().indexOf(keyword);
        while (keywordIndex >= 0) {
            if (lastIndex < keywordIndex) {
                textFlow.getChildren().add(new Text(text.substring(lastIndex, keywordIndex)));
            }
            Text highlightedText = new Text(text.substring(keywordIndex, keywordIndex + keyword.length()));
            highlightedText.setStyle("-fx-fill: red; -fx-font-weight: bold;"); // Highlighting style
            textFlow.getChildren().add(highlightedText);

            lastIndex = keywordIndex + keyword.length();
            keywordIndex = text.toLowerCase().indexOf(keyword, lastIndex);
        }

        if (lastIndex < text.length()) {
            textFlow.getChildren().add(new Text(text.substring(lastIndex)));
        }
        return textFlow;
    }

    /**
     * Applies a basic highlight to the given keyword within the provided text.
     * <p>
     * This method wraps all occurrences of the keyword in the text with double asterisks (`**`)
     * to indicate highlighting. The search is case-insensitive.
     * </p>
     *
     * @param text    the full text in which to search for the keyword
     * @param keyword the keyword to highlight
     * @return the modified text with the keyword highlighted using double asterisks,
     * or the original text if the keyword is null or empty
     */

    private String applyHighlight(String text, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return text;
        }
        return text.replaceAll("(?i)(" + keyword + ")", "**$1**");
    }


    // For UX purposes to understand when a field is editable or not
    // This could be improved by making in inherit from node instead of TextArea
    // But right i want it to be more easily readable


    /**
     * Makes txtArea not editable and displays it to the user by:
     * 1. Making the text gray
     * 2. Making the background gray
     *
     * @param txtArea the area to make not editable
     */
    private void makeNotEditable(TextArea txtArea) {

        txtArea.setEditable(false);


        // Only way i found to change text color is
        // through css style but theoretically
        // setBackground() could be used for the background stuff
        // https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/layout/Region.html#setBackground(javafx.scene.layout.Background)

        String textColor = "gray";

        String style = "-fx-text-fill:" + textColor + ";";

        txtArea.setStyle(style);

        String backgroundColor = "lightgrey";

        style = "-fx-control-inner-background:" + backgroundColor + ";";
        txtArea.setStyle(style);
    }


    /**
     * Restores the textArea to its original state
     * Makes the text black again
     * Sets the style back to its original
     * Makes it editable
     *
     * @param textArea the text area to make editable again
     */
    private void makeEditable(TextArea textArea) {

        textArea.setEditable(true);


        String textColor = "black";

        // CSS change style :
        String style = "-fx-text-fill:" + textColor + ";";

        textArea.setStyle(style);

    }


    /**
     * Makes textField not editable,
     * Virtually does the same thing as the overloaded counterpart
     * changes text and background color
     *
     * @param textField the area to make not editable
     */
    private void makeNotEditable(TextField textField) {

        textField.setEditable(false);

        // through css style

        // text
        String textColor = "gray";

        String style = "-fx-text-fill:" + textColor + ";";

        textField.setStyle(style);


        // background

        String backgroundColor = "lightgrey";

        style = "-fx-control-inner-background:" + backgroundColor + ";";


        textField.setStyle(style);
    }


    /**
     * Makes textField editable:
     * Same as makeEditable for TextArea
     *
     * @param textField the area to make not editable
     */
    private void makeEditable(TextField textField) {

        textField.setEditable(true);


        String textColor = "black";

        // CSS change style :
        String style = "-fx-text-fill:" + textColor + ";";

        textField.setStyle(style);


    }
    /**
     * Sets the locale for the application and updates all UI properties with localized strings.
     *
     * @param locale the {@code Locale} to set for the application.
     */
    public void setLocale(Locale locale) {
        ResourceBundle rb = ResourceBundle.getBundle("bundle", locale);
        propertyDeleteButton.set(rb.getString("button.delete"));
        propertyAddButton.set(rb.getString("button.add"));
        propertySearchButton.set(rb.getString("button.search"));
        propertySearchBarPrompt.set(rb.getString("searchBar.prompt"));
        propertyEditCollButton.set(rb.getString("button.editCollection"));
        propertyCollectionLabel.set(rb.getString("label.collections"));
        propertyRefreshButton.set(rb.getString("button.refresh"));
        propertyClearButton.set(rb.getString("button.clearFilters"));
        switch (locale.getLanguage()) {
            case "en":
                currentLanguage.set("🇬🇧");
                break;
            case "nl":
                currentLanguage.set("🇳🇱");
                break;
            case "es":
                currentLanguage.set("🇪🇸");
                break;
            case "pl":
                currentLanguage.set("\uD83C\uDDF5\uD83C\uDDF1");
                break;
            default:
                currentLanguage.set("🇬🇧");
                break;
        }
    }


    /**
     * Switches the application's language to English.
     */
    public void switchToEnglish() {
        switchLanguage(Locale.ENGLISH);
        currentLanguage.set("🇬🇧");
    }

    /**
     * Switches the application's language to Dutch.
     */
    public void switchToDutch() {
        switchLanguage(new Locale("nl"));
        currentLanguage.set("🇳🇱");
    }

    /**
     * Switches the application's language to Spanish.
     */
    public void switchToSpanish() {
        switchLanguage(new Locale("es"));
        currentLanguage.set("🇪🇸");
    }

    /**
     * Switches the application's language to Polish.
     */
    public void switchToPolish() {
        switchLanguage(new Locale("pl"));
        currentLanguage.set("\uD83C\uDDF5\uD83C\uDDF1");
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
     * Switches the application's language to the specified locale.
     * Updates the UI, saves the selected locale to the configuration file, and sets it globally.
     *
     * @param locale the {@code Locale} to switch to.
     */
    private void switchLanguage(Locale locale) {
        this.currentLocale = locale;
        this.resourceBundle = ResourceBundle.getBundle("bundle", locale);
        saveLocale(locale);
        setLocale(locale);
        createNoteTextInputContextMenu();
    }

    /**
     * Saves the specified locale to the configuration file.
     *
     * @param locale the {@code Locale} to save.
     */
    protected void saveLocale(Locale locale) {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Unable to load properties file: " + e.getMessage());
        }

        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            props.setProperty("language", locale.getLanguage());
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a new window for editing collections
     */
    public void editCollections(){
        mainCtrl.showEditWindow();
    }

    /**
     * Filters the notes based on both search and active tag filters.
     */
    private void applyFilters() {
        String searchKeyword = searchBar.getText().toLowerCase();
        filteredNotes.setAll(data.stream()
                .filter(note -> note.getTitle().toLowerCase().contains(searchKeyword)
                        || note.getText().toLowerCase().contains(searchKeyword))
                .filter(note -> activeTagFilters.stream()
                        .allMatch(filter -> extractTags(note.getText()).contains(filter)))
                .toList());

        try {
            if (filteredNotes.isEmpty()) {
                listNotes.getSelectionModel().clearSelection();
            } else {
                listNotes.getSelectionModel().select(0);
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Handled IndexOutOfBoundsException during applyFilters.");
        }
    }

    /**
     * Updates the selection in the ListView based on the filtered notes.
     * If the filtered list is empty, it clears the selection and resets the note editor.
     * Otherwise, it selects the first note in the list and updates the note editor fields.
     * Handles IndexOutOfBoundsException to prevent application crashes.
     */
    private void updateListViewSelection() {
        try {
            if (filteredNotes.isEmpty()) {
                listNotes.getSelectionModel().clearSelection();
                noteWriting.clear();
                titleWriting.clear();
            } else {
                listNotes.getSelectionModel().select(0); // Safely select the first item
                Note selectedNote = listNotes.getSelectionModel().getSelectedItem();
                if (selectedNote != null) {
                    noteWriting.setText(selectedNote.getText());
                    titleWriting.setText(selectedNote.getTitle());
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Ignored IndexOutOfBoundsException: " + e.getMessage());
        }
    }

    /**
     * Applies a tag filter to the notes based on the selected tag.
     * Updates the filtered notes list and the available tags in the dropdown.
     * Ensures proper handling of IndexOutOfBoundsException to prevent crashes.
     *
     * @param tag the tag to filter notes by.
     */
    private void applyTagFilter(String tag) {
        if (tag == null || tag.isEmpty()) return;

        Platform.runLater(() -> {
            try {
                activeTagFilters.add(tag);
                filteredNotes.setAll(data.stream()
                        .filter(note -> activeTagFilters.stream()
                                .allMatch(filter -> extractTags(note.getText()).contains(filter)))
                        .toList());
                if (!filteredNotes.isEmpty()) {
                    listNotes.getSelectionModel().select(0);
                } else {
                    listNotes.getSelectionModel().clearSelection();
                }
                tagComboBox.setValue(null);
                tagComboBox.setPromptText("Select Tag");
                updateAvailableTags();
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Handled IndexOutOfBoundsException in applyTagFilter.");
            }
        });
    }

    /**
     * Resets all filters, including search and tag filters.
     * Clears the active tag filters and updates the filtered notes to include all notes.
     * Repopulates the tag dropdown menu and resets the ListView selection.
     * Ensures the dropdown displays "Select Tag".
     */

    private void resetFilters() {
        activeTagFilters.clear(); // Clear active tag filters
        searchBar.clear(); // Clear the search bar
        filteredNotes.setAll(data); // Reset filtered notes to include all notes
        listNotes.setItems(filteredNotes); // Update ListView with all notes
        updateListViewSelection(); // Update the ListView selection

        // Repopulate the tag dropdown menu and set placeholder
        populateTagComboBox();
        tagComboBox.setValue(null); // Clear the current value
        tagComboBox.setPromptText("Select Tag"); // Set the placeholder text to "Select Tag"
    }


    /**
     * Populates the tag dropdown menu with all available tags from the notes.
     * Excludes any tags that are already in use as active filters.
     * Sets the dropdown value to null if no tags are available.
     */
    private void populateTagComboBox() {
        List<String> allTags = data.stream()
                .flatMap(note -> extractTags(note.getText()).stream())
                .distinct()
                .collect(Collectors.toList());
        allTags.removeAll(activeTagFilters);
        tagComboBox.getItems().setAll(allTags);
        tagComboBox.setPromptText("Select Tag");
        tagComboBox.setValue(null);
    }


    /**
     * Updates the available tags in the dropdown based on the filtered notes.
     * Retains the currently selected tag if it is still valid after updating.
     * Handles IndexOutOfBoundsException to ensure smooth execution.
     */
    private void updateAvailableTags() {
        Platform.runLater(() -> {
            try {
                String currentSelection = tagComboBox.getValue();
                List<String> remainingTags = filteredNotes.stream()
                        .flatMap(note -> extractTags(note.getText()).stream())
                        .distinct()
                        .collect(
                                Collectors.toList());
                remainingTags.removeAll(activeTagFilters);
                tagComboBox.getItems().setAll(remainingTags);
                if (currentSelection != null && remainingTags.contains(currentSelection)) {
                    tagComboBox.setValue(currentSelection);
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Handled IndexOutOfBoundsException in updateAvailableTags.");
            }
        });
    }

    /**
     * Extracts all tags from the given content using a regular expression.
     * Tags are identified as words prefixed with a '#' character.
     *
     * @param content the content from which to extract tags.
     * @return a list of tags without the '#' prefix.
     */
    private List<String> extractTags(String content) {
        if (content == null || content.isEmpty()) return Collections.emptyList();
        List<String> tags = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?<=\\s|^)#\\w+").matcher(content);
        while (matcher.find()) {
            tags.add(matcher.group().substring(1)); // Remove the '#' symbol
        }
        return tags;
    }

    /**
     * Handles clicking on a note title in Markdown.
     * Selects the note with the given title in the ListView and updates the note editor.
     *
     * @param noteTitle the title of the clicked note.
     */
    @Override
    public void onNoteMdButtonClick(String noteTitle) {
        Optional<Note> matchingNote = data.stream()
                .filter(note -> note.getTitle().equals(noteTitle))
                .findFirst();

        matchingNote.ifPresent(note -> {
            listNotes.getSelectionModel().select(note);
            noteWriting.setText(note.getText());
        });
    }
    @Override
    public void onTagMdButtonClick(String tag) {
        applyTagFilter(tag);
    }


    /**
     * Creates a right-click context popup for the note text input.
     */
    private void createNoteTextInputContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        Menu menuItemEmbedFile = new Menu(resourceBundle.getString("menu.embed"));
        MenuItem menuItemAddNoteReference = new MenuItem(resourceBundle.getString("menu.reference"));
        MenuItem menuItemAddTag = new MenuItem(resourceBundle.getString("menu.addTag"));

        MenuItem menuSubUploadFile = new MenuItem(resourceBundle.getString("menu.embed.upload"));
        Menu menuSubSelectExistingFile = new Menu(resourceBundle.getString("menu.embed.existing"));

        menuItemEmbedFile.getItems().add(menuSubUploadFile);
        menuItemEmbedFile.getItems().add(menuSubSelectExistingFile);

        contextMenu.getItems().add(menuItemEmbedFile);
        contextMenu.getItems().add(menuItemAddNoteReference);
        contextMenu.getItems().add(menuItemAddTag);


        menuSubUploadFile.setOnAction(this::onNoteTextInputCtxMenuUploadFile);
        menuItemAddNoteReference.setOnAction(this::onNoteTextInputCtxMenuAddNoteRef);
        menuItemAddTag.setOnAction(this::onNoteTextInputCtxMenuAddNoteTag);

        noteWriting.setContextMenu(contextMenu);
        contextMenu.setOnShown((_) -> {
            menuSubSelectExistingFile.getItems().clear();
            getSelectedNote()
                    .getEmbeddedFiles()
                    .forEach((file) -> {
                        MenuItem item = new MenuItem(file.getFilename());
                        item.setOnAction((_) -> {
                            embedFileAtCaret(file.getFilename());
                        });
                        menuSubSelectExistingFile.getItems().add(item);
                    }
            );
            menuSubSelectExistingFile.setDisable(
                menuSubSelectExistingFile.getItems().isEmpty()
            );
        });
    }

    /**
     * Shows a file browser dialogue, and requests the user to select a file.
     * @return File that the user selected. May be null if the user cancelled the operation!
     */
    private File askUserForEmbeddedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resourceBundle.getString("fileChooser.select"));

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(resourceBundle.getString("fileChooser.imageFiles"),
                        "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        return fileChooser.showOpenDialog(null);
    }

    /**
     * Shows a file browser dialogue, and requests the user to save a file.
     * @param file Embed to save
     */
    private void askUserToSaveEmbeddedFile(EmbeddedFile file) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(MessageFormat.format(
                resourceBundle.getString("alert.file.askDownload"), file.getFilename()));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resourceBundle.getString("fileChooser.save"));
        fileChooser.setInitialFileName(file.getFilename());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(resourceBundle.getString("fileChooser.imageFiles"),
                        "*." + file.getFiletype()));
        File output = fileChooser.showSaveDialog(null);
        if (output != null) {
            FileOutputStream fileWriter = null;
            try {
                fileWriter = new FileOutputStream(output);
                fileWriter.write(file.getData());
            } catch (Exception ex) {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                alertError.initModality(Modality.APPLICATION_MODAL);
                alertError.setContentText(MessageFormat.format(
                        resourceBundle.getString("alert.file.writeFail"), file.getFilename()));
                alert.showAndWait();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (Exception ex) { } //please shut up java compiler
                }
            }
        }
    }

    /**
     * Called when ContextMenu->Upload file is clicked.
     * This will open a file browser dialogue, and will embed it into the current note.
     * @param event event
     */
    private void onNoteTextInputCtxMenuUploadFile(ActionEvent event) {
        File file = askUserForEmbeddedFile();
        if (file == null || !file.exists()) {
            return; // user cancelled the operation
        }
        String fileName = file.getName();
        if (getSelectedNote()
                .getEmbeddedFiles()
                .stream()
                .map(EmbeddedFile::getFilename)
                .anyMatch(name -> name.equals(fileName))
        ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(MessageFormat.format(
                    resourceBundle.getString("alert.file.exists"), fileName));
            alert.showAndWait();
            return;
        }
        byte[] contents;
        try {
            contents = Files.toByteArray(file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(MessageFormat.format(
                    resourceBundle.getString("alert.file.readFail"), fileName));
            alert.showAndWait();
            return;
        }

        Note currentNote = listNotes.getSelectionModel().getSelectedItem();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        EmbeddedFile embed = new EmbeddedFile(
            fileName,
            extension,
            contents
        );
        currentNote.addEmbeddedFile(
            server.addFile(currentNote.id, embed)
        );
        embedFileAtCaret(fileName);
    }

    /**
     * Adds an ![alt](url) embed at the current caret position
     * @param fileName Embed file name
     */
    private void embedFileAtCaret(String fileName) {
        Platform.runLater(()->{
            int caretPosition = noteWriting.getCaretPosition();
            String fileNameNoExt = fileName.substring(0, fileName.lastIndexOf("."));
            noteWriting.insertText(caretPosition, "!["+fileNameNoExt+"]("+fileName+")");
            updateMarkdown();
        });
    }

    /**
     * Called when ContextMenu->Reference Note is clicked. Surrounds the caret with [[...]]
     * @param event event
     */
    private void onNoteTextInputCtxMenuAddNoteRef(ActionEvent event) {
        Platform.runLater(()->{
            int caretPosition = noteWriting.getCaretPosition();
            noteWriting.insertText(caretPosition, "]]");
            noteWriting.insertText(caretPosition, "[[");
        });
    }

    /**
     * Called when ContextMenu->Add tag is clicked. Prepends the caret with #...
     * @param event event
     */
    private void onNoteTextInputCtxMenuAddNoteTag(ActionEvent event) {
        int caretPosition = noteWriting.getCaretPosition();
        noteWriting.insertText(caretPosition, "#");
    }
    @Override
    public void onUrlMdAnchorClick(String url) {
        if (Desktop.isDesktopSupported()) {
            // Windows
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            // Ubuntu
            try {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(new String[]{"xdg-open", url});
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void onMdEmbeddedFileClick(String fileName) {
        EmbeddedFile file = getSelectedNote()
                .getEmbeddedFiles()
                .stream()
                .filter(f -> f.getFilename().equals(fileName))
                .findFirst()
                .orElse(null);
        if (file == null) {
            return;
        }
        askUserToSaveEmbeddedFile(file);
    }

    @Override
    public boolean doesNoteExistWithinSameCollection(String note) {
        return listNotes.getItems()
                .stream()
                .anyMatch(tNote -> {
                    return tNote.getTitle().equals(note);
                }
            );
    }

    @Override
    public Note getSelectedNote() {
        return listNotes.getSelectionModel().getSelectedItem();
    }

    @Override
    public String getServerUrl() {
        return ServerConnection.SERVER;
    }

    @FXML
    private void toggleTheme() {
        if (root.getStyleClass().contains("light-mode")) {
            root.getStyleClass().remove("light-mode");
            root.getStyleClass().remove("light-mode");
            root.getStyleClass().add("blue-mode");

            applyBlueMode();
            System.out.println("Switched to Blue Mode");
        } else {
            root.getStyleClass().remove("blue-mode");
            root.getStyleClass().add("light-mode");

            applyLightMode();
            System.out.println("Switched to Light Mode");
        }
    }

    private void applyBlueMode() {
        listNotes.getStyleClass().remove("light-mode");
        listNotes.getStyleClass().add("blue-mode");

        noteWriting.getStyleClass().remove("light-mode");
        titleWriting.getStyleClass().remove("light-mode");
        noteWriting.getStyleClass().add("blue-mode");
        titleWriting.getStyleClass().add("blue-mode");
        markDownView.getStyleClass().remove("light-mode");
        markDownView.getStyleClass().add("blue-mode");

        tagComboBox.getStyleClass().add("blue-mode");
    }

    private void applyLightMode() {
        listNotes.getStyleClass().remove("blue-mode");
        listNotes.getStyleClass().add("light-mode");

        noteWriting.getStyleClass().remove("blue-mode");
        titleWriting.getStyleClass().remove("blue-mode");
        noteWriting.getStyleClass().add("light-mode");
        titleWriting.getStyleClass().add("light-mode");
        markDownView.getStyleClass().remove("blue-mode");
        markDownView.getStyleClass().add("light-mode");

        tagComboBox.getStyleClass().add("light-mode");
    }
}
