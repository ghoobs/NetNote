package client.websocket;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import java.util.function.Consumer;

public class WebSocketClient2 implements WebSocketHandler {

    private final WebSocketClient client;
    private WebSocketSession session;
    private Consumer<String> listener;

    public WebSocketClient2() {
        client = new StandardWebSocketClient();
    }

    /**
     * this connects with the websocket server
     * @param uri the uri to connect
     * @throws Exception in case of exception found
     */
    public void connect(String uri) throws Exception {
        session = client.doHandshake(this, uri).get();
    }

    public void addWebSocketListener(Consumer<String> listener) {
        this.listener = listener;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connected to WebSocket successfully");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (listener != null) {
            listener.accept(message.getPayload().toString());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Connection closed successfully");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}