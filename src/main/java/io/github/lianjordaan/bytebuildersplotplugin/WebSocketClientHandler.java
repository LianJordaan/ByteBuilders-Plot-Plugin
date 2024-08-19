package io.github.lianjordaan.bytebuildersplotplugin;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketClientHandler extends WebSocketClient {

    private final Logger logger;

    public WebSocketClientHandler(URI serverUri, Logger logger) {
        super(serverUri);
        this.logger = logger;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("WebSocket connection opened");
        send("{\"type\": \"message\", \"message\": \"Hello from Minecraft Velocity plugin!\"}");
    }

    @Override
    public void onMessage(String message) {
        logger.log(Level.INFO, "Received message: {}", message);
        // Handle incoming messages from WebSocket server
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.log(Level.INFO, "WebSocket connection closed: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        logger.log(Level.SEVERE, "WebSocket error", ex);
    }
}