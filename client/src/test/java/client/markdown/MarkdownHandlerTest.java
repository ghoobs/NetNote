package client.markdown;

import commons.Note;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownHandlerTest {
    @Test
    void testLaunchSafeguardsNoInit() {
        MarkdownHandler handler = new MarkdownHandler();
        assertThrows(IllegalStateException.class, handler::launchAsyncWorker);
    }
    @Test
    void testLaunchSafeguardsOnlyMarkdown() {
        MarkdownHandler handler = new MarkdownHandler();
        handler.createMdParser(new ArrayList<>()); // extensions not required
        assertThrows(IllegalStateException.class, handler::launchAsyncWorker);
    }

    @Test
    void testSetHyperlinkInterfaceBeforeWebEngine() {
        MarkdownHandler handler = new MarkdownHandler();
        assertThrows(IllegalStateException.class, ()-> handler.setEventInterface(new IMarkdownEvents() {
            public void onTagMdButtonClick(String tag) {}
            public void onNoteMdButtonClick(String note) {}
            public void onUrlMdAnchorClick(String url) {}

            @Override
            public boolean doesNoteExistWithinSameCollection(String note) {
                return false;
            }

            @Override
            public Note getSelectedNote() {
                return null;
            }

            @Override
            public String getServerUrl() {
                return "";
            }
        }));
    }

    @Test
    void testAsyncShutdownBeforeInit() {
        MarkdownHandler handler = new MarkdownHandler();
        assertThrows(IllegalStateException.class, handler::disposeAsyncWorker);
    }

    @Test
    void testRegexReplaceNoteReferenceExists() {
        String srcHtml = "<p>[[!-My Other_n0te&&--=?]]</p>";
        String expectedHtml = "<p>" + "<button notetype=\"!-My Other_n0te&&--=?\" "+
        "style=\"font-weight: bold;\">" +
                "!-My Other_n0te&&--=?" +
                "</button>" +"</p>";
        assertEquals(
                MarkdownHandler.regexReplaceAllNoteRefs(srcHtml, _->true),
                expectedHtml
        );
    }
    @Test
    void testRegexReplaceNoteReferenceDoesNotExist() {
        String srcHtml = "<p>[[!-My Other_n0te&&--=?]]</p>";
        String expectedHtml = "<p>" + "<button notetype=\"!-My Other_n0te&&--=?\" "+
                "style=\"font-weight: bold;color: red;\">" +
                "!-My Other_n0te&&--=?" +
                "</button>" +"</p>";
        assertEquals(
                MarkdownHandler.regexReplaceAllNoteRefs(srcHtml, _->false),
                expectedHtml
        );
    }
    @Test
    void testRegexReplaceNoteReferenceInvalidString() {
        String srcHtml = "<p>[[!-My Invalid\nNote>]]</p>";
        assertEquals(
                MarkdownHandler.regexReplaceAllNoteRefs(srcHtml, _->false),
                srcHtml
        );
    }
    @Test
    void testRegexReplaceTag() {
        String srcHtml = "<p>#Myyy_Tag</p>";
        String expectedHtml = "<p><button tagtype=\"Myyy_Tag\"># Myyy_Tag</button></p>";
        assertEquals(
                MarkdownHandler.regexReplaceAllTags(srcHtml),
                expectedHtml
        );
    }
    @Test
    void testRegexReplaceTagWSpace() {
        String srcHtml = "<p>#Myyy Tag</p>";
        String expectedHtml = "<p><button tagtype=\"Myyy\"># Myyy</button> Tag</p>";
        assertEquals(
                MarkdownHandler.regexReplaceAllTags(srcHtml),
                expectedHtml
        );
    }

    @Test
    void testRegexReplaceEmbeddedFile() {
        String serverUrl = "https://imaginary-server.com";
        long noteId = 1;
        String resourceName = "my complex-image_name! [1].png";
        String altName = "test/thing";
        String srcHtml = "<p>!["+altName+"]("+resourceName+")</p>";
        String expectedHtml = "<p><img alt=\""+altName+"\" src=\""+serverUrl+"/api/embeds/" +
                noteId+ "/"+resourceName+"\"></p>";
        String result = MarkdownHandler.regexReplaceAllEmbeds(srcHtml, serverUrl, noteId);
        assertEquals(
                result ,
                expectedHtml
        );
    }
}
