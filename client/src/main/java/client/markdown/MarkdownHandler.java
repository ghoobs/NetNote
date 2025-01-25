package client.markdown;

import client.utils.StringReplacer;
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
import commons.EmbeddedFile;
import commons.Note;
import commons.Tag;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import org.w3c.dom.Document;
import org.w3c.dom.events.*;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MarkdownHandler {
    // markdown related objects
    private WebEngine webEngine;
    private Parser mdParser;
    private HtmlRenderer mdRenderer;
    private String htmlCache;

    private IMarkdownEvents markdownEvents;

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
        bindScripts();
    }

    /**
     * Allows manually determining what occurs upon certain actions and events. If set to null, the default handler will be used.
     * The WebEngine MUST be set prior in order to modify the callback.
     * @param events The interface that handles the events, provided with the link address
     */
    public void setEventInterface(IMarkdownEvents events) {
        if (webEngine == null) {
            throw new IllegalStateException("WebEngine has not been set!");
        }
        this.markdownEvents = events;
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
        //signals to thread to stop running
        workerActive=false;
        try {
            //wait for it for 100 milliseconds to stop
            asyncMarkdownWorker.join(100);
            //if it doesnt kill it
            asyncMarkdownWorker.interrupt();
        } catch (InterruptedException e) {
            System.out.println("Markdown worker thread interrupted.");
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
        if (markdownEvents != null) {
            Note note = markdownEvents.getSelectedNote();
            if (note == null) return;
            mdContents = regexReplaceAllEmbeds(mdContents,
                    markdownEvents.getServerUrl(),
                    note.id);
        }
        // parse the markdown contents
        Node document = mdParser.parse(mdContents);
        // convert the markdown to HTML
        String html = mdRenderer.render(document);
        html = regexReplaceAllNoteRefs(
                regexReplaceAllTags(html),
                markdownEvents::doesNoteExistWithinSameCollection
        );
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
     * The action when clicking on a hyperlink
     * @param event Propagated event
     */
    private void onClickHtmlAnchor(Event event) {
        event.preventDefault();
        event.stopPropagation();

        if (markdownEvents == null) return;
        var node = (org.w3c.dom.Node)event.getTarget();
        var hrefAttr = node.getAttributes().getNamedItem("href");

        if (hrefAttr == null) {
            return; // no link
        }

        markdownEvents.onUrlMdAnchorClick(hrefAttr.getNodeValue());
    }
    /**
     * The action when clicking on a button
     * @param event Propagated event
     */
    private void onClickHtmlButton(Event event) {
        event.preventDefault();
        event.stopPropagation();

        if (markdownEvents == null) return;
        var node = (org.w3c.dom.Node)event.getTarget();

        var noteRefValue = node.getAttributes().getNamedItem("notetype");
        var tagRefValue = node.getAttributes().getNamedItem("tagtype");

        if (noteRefValue != null && tagRefValue == null) {
            markdownEvents.onNoteMdButtonClick(noteRefValue.getNodeValue());
        } else if (noteRefValue == null && tagRefValue != null) {
            markdownEvents.onTagMdButtonClick(tagRefValue.getNodeValue());
        }
    }

    /**
     * The action when clicking on an image
     * @param event Propagated event
     */
    private void onClickHtmlImage(Event event) {
        event.preventDefault();
        event.stopPropagation();

        if (markdownEvents == null) return;
        var node = (org.w3c.dom.Node)event.getTarget();

        var srcImage = node.getAttributes().getNamedItem("myAttrFile");
        if (srcImage != null) {
            markdownEvents.onMdEmbeddedFileClick(srcImage.getNodeValue());
        }
    }

    /**
     * Creates a new state listener for the webview engine which will deal with special callbacks
     */
    private void bindScripts() {
        webEngine.getLoadWorker().stateProperty()
            .addListener(
                (_, _, newValue) -> {
                    if (Worker.State.SUCCEEDED.equals(newValue)) {
                        String location = webEngine.getLocation();

                        if (htmlCache == null) {
                            return;
                        }
                        // check if the new location is meaningful or not
                        if (!location.isEmpty()) {
                           webEngine.loadContent(htmlCache); // restore the html page
                        }
                        Document doc = webEngine.getDocument();

                        // Create the event listener for anchors
                        EventListener listenerA = this::onClickHtmlAnchor;
                        // Add event handler to <a> hyperlinks.
                        var aNodeList = doc.getElementsByTagName("a");
                        for (int i = 0; i < aNodeList.getLength(); i++) {
                            EventTarget hyperlink = (EventTarget)aNodeList.item(i);
                            hyperlink.addEventListener("click", listenerA, true);
                        }

                        // Create the event listener for anchors
                        EventListener listenerBtn = this::onClickHtmlButton;
                        // Add event handler to <a> hyperlinks.
                        var btnNodeList = doc.getElementsByTagName("button");
                        for (int i = 0; i < btnNodeList.getLength(); i++) {
                            EventTarget hyperlink = (EventTarget)btnNodeList.item(i);
                            hyperlink.addEventListener("click", listenerBtn, true);
                        }

                        // Create the event listener for anchors
                        EventListener listenerImg = this::onClickHtmlImage;
                        // Add event handler to <a> hyperlinks.
                        var imgNodeList = doc.getElementsByTagName("img");
                        for (int i = 0; i < btnNodeList.getLength(); i++) {
                            EventTarget hyperlink = (EventTarget)imgNodeList.item(i);
                            // Null-check to avoid constant errors thrown in console.
                            if(hyperlink != null) {
                                hyperlink.addEventListener("click", listenerImg, true);
                            }
                        }
                    }
                }
            );
    }

    /**
     * Replaces all the [[Note]] with a button
     * @param htmlData html code
     * @param noteExists function that should check if a note exists within the given collection.
     * @return Updated html code
     */
    public static String regexReplaceAllNoteRefs(String htmlData, Predicate<String> noteExists) {
        return StringReplacer.replace(htmlData,
                Pattern.compile(Note.REGEX_MD_NOTE_REFERENCE),
                (matcher) -> {
                    String note = matcher.group(1);
                    String style = "font-weight: bold;";
                    if (!noteExists.test(note)) {
                        style+="color: red;";
                    }
                    return "<button notetype=\""+note+"\" style=\"" + style + "\">" +
                            //"<span><img src=\"\"></span>" +
                            //"<span>$1</span>" +
                            note +
                            "</button>";
                });
    }

    /**
     * Replaces all the #Tag with a button
     * @param htmlData html code
     * @return Updated html code
     */
    public static String regexReplaceAllTags(String htmlData) {
        return htmlData.replaceAll(
                Tag.REGEX_MD_TAG_REFERENCE,
                "<button tagtype=\"$1\"># $1</button>");
    }

    /**
     * Replaces all the ![alt](url) with an embed
     * @param htmlData html code
     * @param serverUrl full server URL including the port
     * @param noteId id of the current note
     * @return Updated html code
     */
    public static String regexReplaceAllEmbeds(String htmlData, String serverUrl, long noteId) {
        return htmlData.replaceAll(
                 EmbeddedFile.REGEX_MD_EMBED_REFERENCE,
                "<img myAttrFile=\"$2\" alt=\"$1\" src=\""+serverUrl+"/api/embeds/" +
                        noteId+ "/$2\">");
    }
}
