package client.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompClientSupport;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.converter.StringMessageConverter;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class WebSocketClient2 {

    private final WebSocketStompClient client;
    private StompSession session;

    public WebSocketClient2() {
        this.client = new WebSocketStompClient(new StandardWebSocketClient());
        this.client.setMessageConverter(new StringMessageConverter());
    }

    public void connect(String url, Consumer<String> handlerMessage) throws ExecutionException, InterruptedException {
        this.session = client.connect(url, new StompSessionHandlerAdapter() {

            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("websocket server connected succesfully");
                session.subscribe("/topic/notes", new StompSessionHandlerAdapter() {

                    public void handlerFrame(StompHeaders headers, Object payload) {
                        handlerMessage.accept(payload.toString());
                    }
                });
            }
        }).get();
    }
}