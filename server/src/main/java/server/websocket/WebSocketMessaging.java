package server.websocket;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the service that handles websocket messaging, these messages are sent to the client
 */
@Service
public class WebSocketMessaging {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketMessaging(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEvent(Object event) {
        messagingTemplate.convertAndSend("/topic/updates", event);
    }
}
