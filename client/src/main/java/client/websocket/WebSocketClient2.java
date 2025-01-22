package client.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.converter.StringMessageConverter;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class WebSocketClient2 {

    private final WebSocketStompClient client;
    private StompSession session;

    public WebSocketClient2() {
        this.client = new WebSocketStompClient(new StandardWebSocketClient());
        this.client.setMessageConverter(new StringMessageConverter());
    }


    public void connect(String url, Consumer<String> handlerMessage) {
        try {
            this.session = client.connect(url, new StompSessionHandlerAdapter() {

                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("WebSocket server connected successfully");
                    session.subscribe("/topic/notes", new StompSessionHandlerAdapter() {

                        @Override
                        public Type getPayloadType(StompHeaders stompHeaders) {
                            return String.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            handlerMessage.accept(payload.toString());
                        }
                    });
                }
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Failed to connect to WebSocket server: " + e.getMessage());
        }
    }
}