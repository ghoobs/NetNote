//package server.websocket;
//
//import org.slf4j.LoggerFactory;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.stereotype.Component;
//import org.slf4j.Logger;
//
///**
// * A lot of loggin options here, so that all updates from the websocket can be seen in the logs
// * That makes it a lot easier to check if it is working properly
// */
//@Component
//public class WebSocketHandling implements WebSocketHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandling.class);
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        logger.info("WebSocket connection established with session id: " + session.getId());
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
//        logger.info("Received message: " + message.getPayload());
//        // So this is where a message is sent to refresh, for the automated change syncing
//        session.sendMessage(new TextMessage("refresh"));
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        logger.error("WebSocket transport error: " + exception.getMessage());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        logger.info("WebSocket connection closed with session id: " + session.getId());
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }
//}
