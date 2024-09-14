package io.github.lianjordaan.bytebuildersplotplugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketClientHandler extends WebSocketClient {

    private final Logger logger;
    private final URI serverUri;

    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int RECONNECT_DELAY = 30; // Delay in seconds before trying to reconnect

    public WebSocketClientHandler(URI serverUri, Logger logger) {
        super(serverUri);
        this.serverUri = serverUri;
        this.logger = logger;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("WebSocket connection opened");
        send("{\"type\": \"message\", \"message\": \"Hello from Minecraft Plot plugin!\"}");
        send("{\"type\": \"status\", \"status\": \"running\"}");
        send("{\"type\": \"forwarded-message\", \"targetId\": \"proxy\", \"message\": \"running\"}");
    }

    @Override
    public void onMessage(String message) {
        ByteBuildersPlotPlugin.setLatestMessage(message);
        logger.log(Level.INFO, "Received message: {0}", message);
        // Handle incoming messages from WebSocket server
        JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
        String type = jsonMessage.get("type").getAsString();
        if ("action".equals(type)) {
            String action = jsonMessage.get("action").getAsString();

            // read unloadPlugins function
//            if ("unload-plugins".equals(action)) {
//                ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class).unloadPlugins();
//                send("{\"type\":\"action\",\"action\":\"unload-plugins\",\"status\":\"done\"}");
//            }
            if ("load-worlds".equals(action)) {
                ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class).loadWorlds();
                send("{\"type\":\"action\",\"action\":\"load-worlds\",\"status\":\"done\"}");
            }
            if ("load-world".equals(action)) {
                String worldName = jsonMessage.get("worldName").getAsString();
                ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class).loadWorld(worldName);
                send("{\"type\":\"action\",\"action\":\"load-world\",\"worldName\":\"" + worldName + "\",\"status\":\"done\"}");
            }

            // read loadPlugins function
//            if ("load-plugins".equals(action)) {
//                ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class).loadPlugins();
//            }
        }
    }

    private void scheduleReconnect() {
        // Calculate exponential backoff delay
        long delay = Math.min(RECONNECT_DELAY * (long) Math.pow(2, getReconnectAttempts()), 300);

        logger.info("Attempting to reconnect in " + delay + "seconds...");
        reconnectScheduler.schedule(() -> {
            try {
                reconnectBlocking();
                resetReconnectAttempts(); // Reset attempts if reconnection is successful
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Reconnection attempt interrupted " + e);
                Thread.currentThread().interrupt();
            }
        }, delay, TimeUnit.SECONDS);
    }

    private int reconnectAttempts = 0;

    // Increment reconnect attempts count
    private void incrementReconnectAttempts() {
        reconnectAttempts++;
    }

    // Reset reconnect attempts count
    private void resetReconnectAttempts() {
        reconnectAttempts = 0;
    }

    // Get the current reconnect attempts count
    private int getReconnectAttempts() {
        return reconnectAttempts;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed: " + reason);
        incrementReconnectAttempts(); // Increment the reconnect attempts
        scheduleReconnect();
    }

    @Override
    public void onError(Exception ex) {
        logger.log(Level.SEVERE, "WebSocket error", ex);
        incrementReconnectAttempts(); // Increment the reconnect attempts
        scheduleReconnect();
    }
}
