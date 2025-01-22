package client.scenes;

import client.markdown.*;
import client.utils.ServerUtils2;
import client.websocket.WebSocketClient2;
import com.google.inject.Inject;
import commons.Collection;
import commons.Note;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

/**
 * The type Note overview ctrl.
 */
public class NoteOverviewCtrl implements Initializable, IMarkdownEvents {

    private final ServerUtils2 server;
    private final MainCtrl mainCtrl;
    private final MarkdownHandler mdHandler;
    private WebSocketClient2 webSocketClient;

    private Collection currentCollection;
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
    private Button editCollectionButton;
    @FXML
    private Label collectionLabel;
    @FXML
    private HBox tagField; // HBox to hold the tag ComboBoxes
    @FXML
    private ComboBox<String> tagComboBox; // Initial ComboBox for tags
    @FXML
    private Button clearTagsButton; // Button to reset filters
    private Set<String> activeTagFilters = new LinkedHashSet<>(); // Stores currently selected tags
    private List<ComboBox<String>> tagFilters = new ArrayList<>();
    private ObservableList<Note> filteredNotes;
    private final StringProperty propertyDeleteButton = new SimpleStringProperty();
    private final StringProperty propertyAddButton = new SimpleStringProperty();
    private final StringProperty propertySearchButton = new SimpleStringProperty();
    private final StringProperty propertySearchBarPrompt = new SimpleStringProperty();
    private final StringProperty propertyEditCollButton = new SimpleStringProperty();
    private final StringProperty propertyCollectionLabel = new SimpleStringProperty();
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
    public NoteOverviewCtrl(ServerUtils2 server, MarkdownHandler mdHandler, MainCtrl mainCtrl) {
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
        collectionLabel.textProperty().bind(propertyCollectionLabel);
        this.currentLocale = loadSavedLocale();
        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
        setLocale(currentLocale);
        data = FXCollections.observableArrayList(server.getNotes());
        filteredNotes = FXCollections.observableArrayList(data);
        listNotes.setItems(filteredNotes);
        tagFilters.add(tagComboBox);
        displayTags(tagComboBox);
        clearTagsButton.setOnAction(event -> clearTags());
        makeEditable(noteWriting);
        titleWriting.setEditable(true);
        mdHandler.createMdParser(MarkdownHandler.getDefaultExtensions());
        mdHandler.setWebEngine(markDownView.getEngine());
        mdHandler.setEventInterface(this);
        mdHandler.launchAsyncWorker(); // TODO: make sure to dispose when ctrl is closed or something
//        searchButton.setOnAction(event -> searchNotes());
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
        setupWebSocketClient();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(newValue);
            highlightSelectedNote(newValue);
        });
        listNotes.setOnMouseClicked(this::onNoteClicked);
        refresh();
        updateMarkdown();
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
        showNotification("Note added successfully!");
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
                    alert.setTitle("Title Already Exists");
                    alert.setHeaderText("The title of your note has to be unique!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Title");
                alert.setHeaderText("The title of your note can't be empty!");
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
            alert.setTitle("Delete Note");
            alert.setHeaderText("Are you sure you want to delete this note?");
            alert.setContentText(noteSelected.getTitle());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        server.deleteNote(noteSelected);
                        data.remove(noteSelected);
                        filteredNotes.remove(noteSelected);
                        listNotes.refresh();
                        listNotes.getSelectionModel().clearSelection();
                        showNotification("Note deleted successfully!");
                    } catch (Exception e) {
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
        listNotes.setItems(data);
        listNotes.getSelectionModel().select(0);
        onNoteClicked(null);
        showNotification("Notes refreshed successfully!");
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
     * Sets the {@code Button} used for the delete functionality.
     *
     * @param deleteButton the delete {@code Button}.
     */
    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }

    /**
     * Sets the {@code Button} used for the add functionality.
     *
     * @param addButton the add {@code Button}.
     */
    public void setAddButton(Button addButton) {
        this.addButton = addButton;
    }

    /**
     * Sets the {@code Button} used for the search functionality.
     *
     * @param searchButton the search {@code Button}.
     */
    public void setSearchButton(Button searchButton) {
        this.searchButton = searchButton;
    }

    /**
     * Gets the current locale used by the application.
     *
     * @return the current {@code Locale}.
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Sets the {@code TextField} used for the search bar functionality.
     *
     * @param searchBar the search {@code TextField}.
     */
    public void setSearchBar(TextField searchBar) {
        this.searchBar = searchBar;
    }

    /**
     * Gets the {@code Button} used for the search functionality.
     *
     * @return the search {@code Button}.
     */
    public Button getSearchButton() {
        return searchButton;
    }

    /**
     * Gets the {@code Button} used for the add functionality.
     *
     * @return the add {@code Button}.
     */
    public Button getAddButton() {
        return addButton;
    }

    /**
     * Gets the {@code Button} used for the delete functionality.
     *
     * @return the delete {@code Button}.
     */
    public Button getDeleteButton() {
        return deleteButton;
    }

    /**
     * Gets the {@code TextField} used for the search bar functionality.
     *
     * @return the search {@code TextField}.
     */
    public TextField getSearchBar() {
        return searchBar;
    }
    /**
     * Filters notes by a selected tag.
     */
    /**
     * Filters notes by a selected tag and applies the combined filters.
     */
    private void filterByTag(String newTag) {
        if (newTag == null || newTag.isEmpty()) {
            return;
        }
        activeTagFilters.add(newTag);
        applyFilters(searchBar.getText());
        ComboBox<String> newComboBox = new ComboBox<>();
        newComboBox.setPromptText("Select a tag");
        tagFilters.add(newComboBox);
        tagField.getChildren().add(newComboBox);
        displayTags(newComboBox);
    }

    /**
     * Displays available tags in a given ComboBox.
     */
    private void displayTags(ComboBox<String> tagBox) {
        List<String> allTags = filteredNotes.stream()
                .map(Note::getText)
                .flatMap(content -> extractTags(content).stream()) // Flatten the tags
                .distinct()
                .toList();
        List<String> availableTags = new ArrayList<>(allTags);
        availableTags.removeAll(activeTagFilters);
        tagBox.getItems().setAll(availableTags);
        tagBox.valueProperty().addListener((observable, oldValue, newValue) -> filterByTag(newValue));
    }
    public void clearTags() {
        activeTagFilters.clear();
        tagFilters.clear();
        tagField.getChildren().clear();
        ComboBox<String> initialComboBox = new ComboBox<>();
        initialComboBox.setPromptText("Select a tag");
        tagFilters.add(initialComboBox);
        tagField.getChildren().add(initialComboBox);
        displayTags(initialComboBox);
        filteredNotes.setAll(data);
        listNotes.setItems(filteredNotes);
    }
    /**
     * Filters notes based on the provided predicate.
     */
    private void filterNotes(Predicate<Note> predicate) {
        filteredNotes.setAll(data.stream().filter(predicate).toList());
        listNotes.setItems(filteredNotes);
    }
    /**
     * Extracts tags directly from the content of a note using regex.
     */
    private List<String> extractTags(String content) {
        List<String> tags = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?<=\\s|^)#\\w+\\b").matcher(content);
        while (matcher.find()) {
            tags.add(matcher.group());
        }
        return tags;
    }

    @Override
    public void onTagMdButtonClick(String tag) {
        filterByTag(tag);
    }

    @Override
    public void onNoteMdButtonClick(String note) {
        Optional<Note> maybeNote = listNotes.getItems().stream()
                .filter(tnote -> tnote.getTitle().equals(note))
                .findFirst();
        // Note exists, navigate
        maybeNote.ifPresent(this::changeSelectedNote);
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
    public boolean doesNoteExistWithinSameCollection(String note) {
        return listNotes.getItems()
                .stream()
                .anyMatch(tNote -> {
                    return tNote.getTitle().equals(note);
                }
            );
    }
}
