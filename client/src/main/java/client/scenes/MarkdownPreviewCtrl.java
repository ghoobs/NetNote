package client.scenes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import one.jpro.platform.mdfx.MarkdownView;

public class MarkdownPreviewCtrl {
    @FXML
    TextArea noteContents;
    @FXML
    MarkdownView markdownView;

    public void updateMarkdown() {
        Platform.runLater(()-> {
            markdownView.setMdString(noteContents.getText());
        });
    }
}
