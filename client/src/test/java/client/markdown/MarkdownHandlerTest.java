package client.markdown;

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
        }));
    }

    @Test
    void testAsyncShutdownBeforeInit() {
        MarkdownHandler handler = new MarkdownHandler();
        assertThrows(IllegalStateException.class, handler::disposeAsyncWorker);
    }

    @Test
    void testRegexReplaceNoteReference() {
        String srcHtml = "<p>[[!-My Other_n0te&&--=?]]</p>";
        String expectedHtml = "<p>" + "<button notetype=\"!-My Other_n0te&&--=?\">" +
                "!-My Other_n0te&&--=?" +
                "</button>" +"</p>";
        assertEquals(
            MarkdownHandler.regexReplaceAllNoteRefs(srcHtml),
            expectedHtml
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
}
