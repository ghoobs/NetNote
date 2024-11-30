package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Main2Ctrl {
    private Stage primaryStage;
    private NoteOverviewCtrl overviewCtrl;
    private Scene overview;

    /**
     * Sets up and displays the primary stage with the note overview scene.
     *
     * @param primaryStage the primary Stage for the application
     * @param overview     a Pair containing:
     *                     - the controller for the note overview (NoteOverviewCtrl)
     *                     - the root layout (Parent) for the note overview scene
     */
    public void initialize(Stage primaryStage, Pair<NoteOverviewCtrl, Parent> overview) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());
        showOverview();
        primaryStage.show();
    }

    /**
     * Sets the main scene and sets its title.
     */
    public void showOverview() {
        primaryStage.setTitle("Notes: Overview");
        primaryStage.setScene(overview);
    }

}
