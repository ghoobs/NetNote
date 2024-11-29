package client.scenes;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.youtube.embedded.YouTubeLinkExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;

import java.util.Arrays;

public class MarkdownPreviewCtrl {
    @FXML
    TextArea noteContents;
    @FXML
    WebView markdownView;

    public void updateMarkdown() {
        Platform.runLater(()-> {
            MutableDataSet options = new MutableDataSet();

            options.set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    EmojiExtension.create(),
                    YouTubeLinkExtension.create(),
                    SuperscriptExtension.create(),
                    FootnoteExtension.create()
            ));
            options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();
            Node document = parser.parse(noteContents.getText());
            String html = renderer.render(document);
            markdownView.getEngine().loadContent(html);
        });
    }
}
