package client.scenes;

import client.utils.CollectionServerUtils;
import client.utils.ServerUtils2;
import com.google.inject.Inject;
import commons.Collection;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.IOException;

public class EditCollectionCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final ServerUtils2 server;
    private final CollectionServerUtils collectionUtils;

    @FXML
    private Button deleteCollButton;
    @FXML
    private Button addCollButton;
    @FXML
    private Button backButton;
    @FXML
    private Label title;
    @FXML
    private Label serverName;
    @FXML
    private Label collectionName;
    @FXML
    private Label status;
    private final StringProperty propertyDeleteButton = new SimpleStringProperty();
    private final StringProperty propertyAddButton = new SimpleStringProperty();
    private final StringProperty propertyBackButton = new SimpleStringProperty();
    private final StringProperty propertyTitleLabel = new SimpleStringProperty();
    private final StringProperty propertyServerNameLabel = new SimpleStringProperty();
    private final StringProperty propertyCollectionNameLabel = new SimpleStringProperty();
    private final StringProperty propertyStatusLabel = new SimpleStringProperty();
    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    @FXML
    private TextField titleInput;
    @FXML
    private TextField serverInput;
    @FXML
    private TextField collectionInput;

    @FXML
    private ListView<Collection> collectionsList;
    private ObservableList<Collection> collections;

    /**
     * Constructs an EditCollectionCtrl instance with the main controller
     *
     * @param server the server utils instance for interacting with the server
     * @param mainCtrl the main controller used for scene navigation
     * @param collectionUtils more utils for interacting with the Collection part of the server
     */
    @Inject
    public EditCollectionCtrl(ServerUtils2 server, MainCtrl mainCtrl, CollectionServerUtils collectionUtils) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.collectionUtils = collectionUtils;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteCollButton.textProperty().bind(propertyDeleteButton);
        addCollButton.textProperty().bind(propertyAddButton);
        backButton.textProperty().bind(propertyBackButton);
        title.textProperty().bind(propertyTitleLabel);
        serverName.textProperty().bind(propertyServerNameLabel);
        collectionName.textProperty().bind(propertyCollectionNameLabel);
        status.textProperty().bind(propertyStatusLabel);

        this.currentLocale = loadSavedLocale();
        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
        setLocale(currentLocale);

        collections = FXCollections.observableArrayList(collectionUtils.getCollections());
        collectionsList.setItems(collections);
        collectionsList.setOnMouseClicked(this::onCollectionClicked);
    }

    /**
     * Goes back to the note overview scene
     */
    public void back(){
        mainCtrl.showOverview();
    }

    /**
     * Updates the selected collection with new data
     */
    public void updateCollection(){
        String title = titleInput.getText();
        String server = serverInput.getText();
        String collection = collectionInput.getText();

        if(collection.contains(" ")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("The Collection field cannot contain spaces.");
            alert.showAndWait();
            return;
        }

        if(title == null || server == null || collection == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Fields");
            alert.setHeaderText("You have to fill out all of the fields!");
            alert.showAndWait();
        }
        else {
            try {
                //collectionUtils.addCollection(new Collection(title)); // TODO: change to updateCollection!!
            } catch (WebApplicationException e) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                return;
            }
            collectionsList.refresh();
        }
    }

    public void addCollection(){
        Collection newCollection = new Collection("New Collection");
        try {
            newCollection = collectionUtils.addCollection(newCollection);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        collections.add(newCollection);
        collectionsList.getSelectionModel().select(newCollection);
        titleInput.setText(newCollection.name);
    }

    /**
     * Handles collection selection in the list and displays the selected collection's details.
     *
     * @param mouseEvent the mouse event that triggered this action
     */
    public void onCollectionClicked(MouseEvent mouseEvent) {
        Collection selected = collectionsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            titleInput.setText(selected.name);
        }
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
        propertyBackButton.set(rb.getString("button.back"));
        propertyTitleLabel.set(rb.getString("label.title"));
        propertyServerNameLabel.set(rb.getString("label.serverName"));
        propertyCollectionNameLabel.set(rb.getString("label.collectionName"));
        propertyStatusLabel.set(rb.getString("label.status"));
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
