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
    void testSetHyperlinkCallbackBeforeWebEngine() {
        MarkdownHandler handler = new MarkdownHandler();
        assertThrows(IllegalStateException.class, ()-> handler.setHyperlinkCallback((_)->{}));
    }

    @Test
    void testAsyncShutdownBeforeInit() {
        MarkdownHandler handler = new MarkdownHandler();
        assertThrows(IllegalStateException.class, handler::disposeAsyncWorker);
    }
}
