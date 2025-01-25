package server.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessaging {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketMessaging(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEvent(Object event, String destination) {
        messagingTemplate.convertAndSend(destination, event);
    }
}
