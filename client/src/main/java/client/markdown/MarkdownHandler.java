package client.markdown;

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

import com.vladsch.flexmark.util.misc.Extension;
import javafx.application.Platform;
import javafx.scene.web.WebView;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.locks.ReentrantLock;

public class MarkdownHandler {
    // markdown related objects
    private WebView webView;
    private Parser mdParser;
    private HtmlRenderer mdRenderer;

    // asynchronous thread related objects
    private Thread asyncMarkdownWorker;
    private boolean workerActive;
    private final ArrayDeque<String> mdReadyToDisplay;
    private final ArrayDeque<String> mdUnrenderedTextQueue;
    private final ReentrantLock mdQueueLock;
    private final ReentrantLock asyncRendererLock;

    /**
     * Default list of extensions
     * @return Tables, strikethrough, emojis, superscript, footnotes, and YouTube embedding
     */
    public static List<Extension> getDefaultExtensions() {
        return Arrays.asList(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            EmojiExtension.create(),
            YouTubeLinkExtension.create(),
            SuperscriptExtension.create(),
            FootnoteExtension.create()
        );
    }

    public MarkdownHandler() {
        mdQueueLock = new ReentrantLock();
        asyncRendererLock =new ReentrantLock();
        mdUnrenderedTextQueue = new ArrayDeque<>();
        mdReadyToDisplay = new ArrayDeque<>();
    }

    public void asyncMarkdownUpdate(WebView webview, String mdText) {
        mdQueueLock.lock();
        this.webView = webview;
        if (!mdUnrenderedTextQueue.isEmpty()) {
            mdUnrenderedTextQueue.pop();
        }
        mdUnrenderedTextQueue.push(mdText);
        mdQueueLock.unlock();
        synchronized(asyncRendererLock) {
            asyncRendererLock.notify();
        }
    }

    /**
     * Creates the parser and html renderer necessary to allow creation of HTML data from markdown
     * @param extensions List of markdown extensions to load
     */
    public void createMdParser(List<Extension> extensions){
        MutableDataSet options = new MutableDataSet();

        // enable the extensions to make the markdown a bit more useful
        options.set(Parser.EXTENSIONS, extensions);
        // to avoid the stupid default behaviour where a new line doesn't always add a new line
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        // parse options
        mdParser = Parser.builder(options).build();

        // create the html renderer
        mdRenderer = HtmlRenderer.builder(options).build();
    }

    /**
     * Starts asynchronous thread worker that renders markdown asynchronously,
     * avoiding poor perceived performance on the client.
     */
    public void launchAsyncWorker() {
        if (mdParser == null || mdRenderer == null) {
            throw new IllegalStateException("Markdown parser has not been created!");
        }
        if (asyncMarkdownWorker != null) {
            throw new IllegalStateException("Markdown worker is already running");
        }
        workerActive=true;
        asyncMarkdownWorker = new Thread(this::performMarkdownUpdateCycle);
        asyncMarkdownWorker.start();
    }

    /**
     * Destroys the async worker and makes class safe to dispose
     */
    public void disposeAsyncWorker() {
        if (asyncMarkdownWorker == null) {
            throw new IllegalStateException("Markdown worker not running or was already disposed!");
        }
        synchronized(asyncRendererLock) {
            asyncRendererLock.notify();
        }
        try {
            asyncMarkdownWorker.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs thread safe cycle of the markdown update. Must be signalled! Or will be in infinite wait loop!
     */
    private void performMarkdownUpdateCycle() {
        while (true) {
            synchronized(asyncRendererLock) {
                try {
                    asyncRendererLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!workerActive) {
                break; // disposed, stop loop
            }
            mdQueueLock.lock();
            if (!mdUnrenderedTextQueue.isEmpty()) {
                String mdContents = mdUnrenderedTextQueue.pop();
                mdQueueLock.unlock();
                dispatchWebViewUpdate(mdContents);
            } else {
                mdQueueLock.unlock();
            }
        }
    }

    /**
     * Displays the rendered HTML on the JavaFX thread once it's ready
     * @param mdContents Markdown data to render and display on the webview
     */
    private void dispatchWebViewUpdate(String mdContents) {
        // parse the markdown contents
        Node document = mdParser.parse(mdContents);
        // convert the markdown to HTML
        String html = mdRenderer.render(document);
        synchronized (mdReadyToDisplay) {
            mdReadyToDisplay.add(html);
        }
        Platform.runLater(() -> {
            synchronized (mdReadyToDisplay) {
                if (mdReadyToDisplay.isEmpty()) {
                    return; // task discarded due to long waiting
                }
                mdReadyToDisplay.pollLast();
                mdReadyToDisplay.clear();
            }
            webView.getEngine().loadContent(html);
        });
    }
}
