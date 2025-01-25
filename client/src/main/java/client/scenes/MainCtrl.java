package client.scenes;

import commons.Note;
import commons.EmbeddedFile;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {
    private Stage primaryStage;

    private Scene overview;
    private NoteOverviewCtrl overviewCtrl;

    private Scene editCollections;
    private EditCollectionCtrl editCollectionCtrl;

    private Scene renameEmbeddedFile;
    private RenameCtrl renameEmbeddedFileCtrl;

    /**
     * Sets up and displays the primary stage with the note overview scene.
     *
     * @param primaryStage the primary Stage for the application
     * @param overview     a Pair containing:
     *                     - the controller for the note overview (NoteOverviewCtrl)
     *                     - the root layout (Parent) for the note overview scene
     * @param editCollections a Pair containing:
     *                        - the controller for the edit collection window (EditCollectionCtrl)
     *                        - the root layout (Parent) for the edit collection scene
     * @param renameEmbeddedFile a Pair containing:
     *                        - the controller for the rename file window (RenameCtrl)
     *                        - the root layout (Parent) for the rename file scene
     */

    public void initialize(Stage primaryStage,
                           Pair<NoteOverviewCtrl, Parent> overview,
                           Pair<EditCollectionCtrl, Parent> editCollections,
                           Pair<RenameCtrl, Parent> renameEmbeddedFile) {
        this.primaryStage = primaryStage;

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());
        this.editCollections = new Scene(editCollections.getValue());
        this.editCollectionCtrl = editCollections.getKey();
        this.renameEmbeddedFile = new Scene(renameEmbeddedFile.getValue());
        this.renameEmbeddedFileCtrl = renameEmbeddedFile.getKey();
        showOverview();
        primaryStage.show();
    }

    /**
     * Sets the main scene to the overview scene and updates its title
     */

    public void showOverview() {
        primaryStage.setTitle("NetNote");
        primaryStage.setScene(overview);
        overview.setOnKeyPressed(e -> overviewCtrl.keyPressed(e));
    }

    /**
     * Sets the main scene to the editcollections scene and updates its title
     */
    public void showEditWindow(){
        primaryStage.setScene(editCollections);
        editCollections.setOnKeyPressed(e -> editCollectionCtrl.keyPressed(e));
        editCollectionCtrl.setLocale(editCollectionCtrl.loadSavedLocale());
    }
    
    /**
     * Sets the main scene to the renameEmbeddedFile scene and updates its title
     */
    public void showRenameEmbeddedFileWindow(Note currentNote, EmbeddedFile currentFile){
        primaryStage.setScene(renameEmbeddedFile);
        renameEmbeddedFile.setOnKeyPressed(e -> renameEmbeddedFileCtrl.keyPressed(e));
        renameEmbeddedFileCtrl.setLocale(renameEmbeddedFileCtrl.loadSavedLocale());
        renameEmbeddedFileCtrl.setCurrentNote(currentNote, currentFile);
    }

    /**
     * returns the overviewCtrl to manage the note overview scene
     * @return the overviewCtrl
     */

    public NoteOverviewCtrl getOverviewCtrl() {
        return overviewCtrl;
    }
}
