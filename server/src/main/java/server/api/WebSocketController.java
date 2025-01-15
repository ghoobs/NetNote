package server.api;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.websocket.WebSocketMessaging;

@RestController
@RequestMapping("/api/websocket")
public class WebSocketController {
    private final WebSocketMessaging webSocketMessaging;

    public WebSocketController(WebSocketMessaging webSocketMessaging) {
        this.webSocketMessaging = webSocketMessaging;
    }

    @PostMapping("/sendUpdate")
    public void sendUpdate() {
        webSocketMessaging.sendEvent("refresh"); // Sends refresh message to all clients
    }
}
