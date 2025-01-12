package server.websocket;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebSocketMessaging {

    // List to keep track of all active WebSocket sessions
    private final List<WebSocketSession> sessions = new ArrayList<>();

    /**
     * Sends a message to all connected WebSocket clients
     *
     * @param event the event object to be sent to clients
     */
    public void sendEvent(Object event) {
        String eventMessage = event.toString();

        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(eventMessage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
