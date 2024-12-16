package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import com.google.inject.Injector;
import client.utils.ServerUtils;
import javafx.application.Application;
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

        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        var overview = FXML.load(NoteOverviewCtrl.class, "client", "scenes", "startframe.fxml");

        var edit = FXML.load(EditCtrl.class, "client", "scenes", "editview.fxml");

        var save = FXML.load(SaveCtrl.class, "client", "scenes", "saveview.fxml");

        var add = FXML.load(AddCtrl.class, "client", "scenes", "addview.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, overview, edit, save, add);
    }
}
