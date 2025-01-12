package client.websocket;

import javafx.application.Platform;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketClientService {
    private static final String WEBSOCKET_URL = "ws://localhost:8080/ws/notes";

    public void startConnection() {
        WebSocketClient client = new StandardWebSocketClient();
        client.doHandshake((org.springframework.web.socket.WebSocketHandler) new WebSocketHandler(), WEBSOCKET_URL);
    }

    private static class WebSocketHandler extends TextWebSocketHandler {

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            // Handle incoming message (For now, just print to console)
            Platform.runLater(() -> {
                // Update UI or take appropriate action here
                System.out.println("Received message: " + message.getPayload());
            });
        }
    }
}
