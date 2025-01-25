package client;

import static com.google.inject.Guice.createInjector;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import client.scenes.*;
import com.google.inject.Injector;
import client.utils.ServerConnection;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


public class Main extends Application {
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * The main method, serving as the entry point for the application.
     *
     * @param args the command-line arguments passed during application startup
     * @throws URISyntaxException if an invalid URI is encountered during initialization
     * @throws IOException        if an I/O error occurs during application startup
     */

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * Starts the JavaFX application and sets up the primary stage.
     * Verifies server availability before initializing the main application scene.
     *
     * @param primaryStage the primary stage for this application, onto which the application scene can be set.
     *                     Additional stages may be created if needed, but this is the main stage.
     * @throws Exception if an error occurs during application startup
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle", loadSavedLocale());

        var serverUtils = INJECTOR.getInstance(ServerConnection.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("alert.serverError1"));
            alert.setHeaderText(resourceBundle.getString("alert.serverError2"));
            alert.showAndWait();
            return;
        }

        var overview = FXML.load(NoteOverviewCtrl.class, "client", "scenes", "startframe.fxml");
        var editCollections = FXML.load(EditCollectionCtrl.class, "client", "scenes", "editcollections.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, overview, editCollections);
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
}
