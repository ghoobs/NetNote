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
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class MarkdownHandler {
    // markdown related objects
    private WebEngine webEngine;
    private Parser mdParser;
    private HtmlRenderer mdRenderer;
    private String htmlCache;

    private Consumer<String> hyperlinkCallback;

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

    /**
     * Loads the provided markdown into the webview asynchronously
     * @param mdText Markdown text to load
     */
    public void asyncMarkdownUpdate(String mdText) {
        if (webEngine == null) {
            throw new IllegalStateException("WebEngine has not been set!");
        }
        mdQueueLock.lock();
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
     * Sets the WebEngine
     * @param webEngine WebEngine corresponding to the WebView where the markdown should be loaded.
     */
    public void setWebEngine(WebEngine webEngine) {
        if (this.webEngine == webEngine) {
            return;
        }
        this.webEngine = webEngine;
        createWebEngineStateHandler();
    }

    /**
     * Allows manually determining what occurs upon clicking a hyperlink. If set to null, the default handler will be used.
     * The WebEngine MUST be set prior in order to modify the callback.
     * @param callback The lambda that will be invoked when a hyperlink is clicked, provided with the link address
     */
    public void setHyperlinkCallback(Consumer<String> callback) {
        if (webEngine == null) {
            throw new IllegalStateException("WebEngine has not been set!");
        }
        hyperlinkCallback = callback;
    }

    /**
     * Starts asynchronous thread worker that renders markdown asynchronously,
     * avoiding poor perceived performance on the client.
     */
    public void launchAsyncWorker() {
        if (asyncMarkdownWorker != null) {
            throw new IllegalStateException("Markdown worker is already running");
        }
        if (mdParser == null || mdRenderer == null) {
            throw new IllegalStateException("Markdown parser has not been created!");
        }
        if (webEngine == null) {
            throw new IllegalStateException("WebEngine has not been set!");
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
        Platform.runLater(this::loadDispatchedHtml);
    }

    /**
     * Loads the latest dispatched HTML data into the webview engine
     */
    private void loadDispatchedHtml(){
        String html;
        synchronized (mdReadyToDisplay) {
            if (mdReadyToDisplay.isEmpty()) {
                return; // task discarded due to long waiting
            }
            html = mdReadyToDisplay.pollLast();
            mdReadyToDisplay.clear();
            htmlCache=html;
        }
        webEngine.loadContent(html);
    }

    /**
     * Creates a new state listener for the webview engine which will deal with the hyperlink callback
     */
    private void createWebEngineStateHandler() {
        webEngine.getLoadWorker().stateProperty()
            .addListener(
                (_, _, newValue) -> {
                    if (hyperlinkCallback == null) {
                        return;
                    }
                    if (Worker.State.SUCCEEDED.equals(newValue)) {
                        String location = webEngine.getLocation();
                        hyperlinkCallback.accept(location);
                        if (htmlCache == null) {
                            return;
                        }
                        // check if the new location is meaningful or not
                        if (!location.isEmpty()) {
                           webEngine.loadContent(htmlCache); // restore the html page
                        }
                    }
                }
            );
    }
}
