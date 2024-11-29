package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import one.jpro.platform.mdfx.MarkdownView;

public class MarkdownPreviewCtrl {
    @FXML
    TextArea noteContents;
    @FXML
    MarkdownView markdownView;

    private boolean dirtyContents = false;
    private double lastMarkdownTimeUpdateElapsed = 0.0;

    /**
     * Update rendered markdown periodically after a change has been made
     * @throws InterruptedException
     */
    public void asyncMarkdownUpdateAwait() throws InterruptedException {
        while (dirtyContents) {
            Thread.sleep(100);
            lastMarkdownTimeUpdateElapsed += 1.0;
            if (lastMarkdownTimeUpdateElapsed >= 5.0) {
                lastMarkdownTimeUpdateElapsed = 0.0;
                dirtyContents = false;
                // TODO: fix thread exception, this must run on the JavaFX application thread
                markdownView.setMdString(noteContents.getText());
            }
        }
    }

    public void updateMarkdown() {
        markdownView.setMdString(noteContents.getText());
        // TODO: make this update markdown asynchronously, so input does not lag as the user types
//        if (dirtyContents) return;
//        Thread.startVirtualThread(()->{
//            try {
//                this.asyncMarkdownUpdateAwait();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        dirtyContents = true;
    }
}
