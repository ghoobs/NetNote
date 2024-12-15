package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {
    private Scene edit;
    private EditCtrl editCtrl;
    private Scene save;
    private SaveCtrl saveCtrl;
    private Scene add;
    private AddCtrl addCtrl;
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
     * @param edit a pair containing the EditCtrl and the Parent root layout for the edit scene
     * @param save a pair containing the SaveCtrl and the Parent root layout for the save scene
     * @param add a pair containing the AddCtrl and the Parent root layout for the add scene
     */

    public void initialize(Stage primaryStage,
                           Pair<NoteOverviewCtrl, Parent> overview,
                           Pair<EditCtrl, Parent> edit,
                           Pair<SaveCtrl, Parent> save,
                           Pair<AddCtrl, Parent> add) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());
        this.editCtrl = edit.getKey();
        this.edit = new Scene(edit.getValue());
        this.saveCtrl = save.getKey();
        this.save = new Scene(save.getValue());
        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());
        showOverview();
        primaryStage.show();
    }

    /**
     * Sets the main scene to the overview scene and updates its title
     */

    public void showOverview() {
        primaryStage.setTitle("Notes: Overview");
        primaryStage.setScene(overview);
        overview.setOnKeyPressed(e -> overviewCtrl.keyPressed(e));
    }


    /**
     * Sets the main scene to the edit scene and updates its title
     */

    public void showEdit() {
        primaryStage.setTitle("Notes: Edit");
        primaryStage.setScene(edit);
        edit.setOnKeyPressed(e -> editCtrl.keyPressed(e));
    }


    /**
     * Sets the main scene to the save scene and updates its title
     */

    public void showSave() {
        primaryStage.setTitle("Notes: Save");
        primaryStage.setScene(save);
        save.setOnKeyPressed(e -> saveCtrl.keyPressed(e));
    }


    /**
     * Sets the main scene to the add scene and updates its title
     */

    public void showAdd() {
        primaryStage.setTitle("Notes: Add");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }


    /**
     * returns the overviewCtrl to manage the note overview scene
     * @return the overviewCtrl
     */

    public NoteOverviewCtrl getOverviewCtrl() {
        return overviewCtrl;
    }
}
