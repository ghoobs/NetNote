package server.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMessaging {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketMessaging(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    /**
     * sends a message to all connected WebSocket clients
     *
     * @param event the event object to be sent to clients
     */
    public void sendEvent(Object event, String destination) {
        messagingTemplate.convertAndSend(destination, event);
    }
}

