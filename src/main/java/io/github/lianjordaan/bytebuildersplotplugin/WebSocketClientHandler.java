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
            if ("set-size".equals(action)) {
                ByteBuildersPlotPlugin.plotSize = Integer.valueOf(jsonMessage.get("size").getAsString());
            }

            // read loadPlugins function
//            if ("load-plugins".equals(action)) {
//                ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class).loadPlugins();
//            }
        }
    }

    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int RECONNECT_DELAY = 30; // Delay in seconds before trying to reconnect
    private static final int RECONNECT_TIMEOUT = 2; // Timeout for each reconnect attempt (seconds)
    private static final int MAX_RECONNECT_ATTEMPTS = 10; // Optional: max reconnect attempts
    private boolean reconnectInProgress = false;
    private boolean messageExchanged = false; // Tracks if any messages were exchanged
    private int reconnectAttempts = 0;

    // Method to track when a message is exchanged (send or receive)
    public void onMessageExchanged() {
        messageExchanged = true;
    }

    private void scheduleReconnect() {
        // Reconnect only if messages were exchanged, not if idle, or max attempts reached
        if (!messageExchanged || reconnectInProgress || reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            logger.info("Reconnection skipped. Either no recent activity, reconnect in progress, or max attempts reached.");
            return;
        }

        reconnectInProgress = true;
        long delay = Math.min(RECONNECT_DELAY * (long) Math.pow(2, reconnectAttempts), 300);
        logger.info("Attempting to reconnect in " + delay + " seconds...");

        reconnectScheduler.schedule(() -> {
            try {
                reconnectBlocking();
                resetReconnectAttempts(); // Reset attempts on success
                reconnectInProgress = false;
                messageExchanged = false; // Reset message tracking after successful reconnection
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Reconnection interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Reconnection failed: " + e.getMessage());
                incrementReconnectAttempts();
                reconnectInProgress = false; // Allow retry after failure
                scheduleReconnect(); // Schedule another attempt if needed
            }
        }, delay, TimeUnit.SECONDS);

        // Timeout handling
        reconnectScheduler.schedule(() -> {
            if (reconnectInProgress) {
                logger.log(Level.WARNING, "Reconnection timed out after " + RECONNECT_TIMEOUT + " seconds.");
                reconnectInProgress = false;
                incrementReconnectAttempts();
                scheduleReconnect();
            }
        }, RECONNECT_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed: " + reason);
        scheduleReconnect(); // Only attempt reconnect if messages were exchanged
    }

    @Override
    public void onError(Exception ex) {
        logger.log(Level.SEVERE, "WebSocket error", ex);
        scheduleReconnect(); // Only attempt reconnect if messages were exchanged
    }

    // Reconnect attempts management
    private void incrementReconnectAttempts() {
        reconnectAttempts++;
    }

    private void resetReconnectAttempts() {
        reconnectAttempts = 0;
    }

    private int getReconnectAttempts() {
        return reconnectAttempts;
    }

}
