package client.scenes;

import client.utils.ServerUtils2;
import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.IOException;

public class EditCollectionCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final ServerUtils2 server;

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
    private final StringProperty propertystatusLabel = new SimpleStringProperty();
    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    /**
     * Constructs an EditCollectionCtrl instance with the main controller
     *
     * @param server the server utils instance for interacting with the server
     * @param mainCtrl the main controller used for scene navigation
     */
    @Inject
    public EditCollectionCtrl(ServerUtils2 server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteCollButton.textProperty().bind(propertyDeleteButton);
        addCollButton.textProperty().bind(propertyAddButton);
        backButton.textProperty().bind(propertyBackButton);
        title.textProperty().bind(propertyTitleLabel);
        serverName.textProperty().bind(propertyServerNameLabel);
        collectionName.textProperty().bind(propertyCollectionNameLabel);
        status.textProperty().bind(propertystatusLabel);

        this.currentLocale = loadSavedLocale();
        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
        setLocale(currentLocale);
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
        propertystatusLabel.set(rb.getString("label.status"));
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
