package io.github.lianjordaan.bytebuildersplotplugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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
    private int maxAttempts;
    private int attempt;
    private int delay;

    public WebSocketClientHandler(URI serverUri, Logger logger) {
        super(serverUri);
        this.serverUri = serverUri;
        this.logger = logger;
        this.maxAttempts = 0;
        this.attempt = 0;
        this.delay = 0;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        attempt = 0;
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
        if ("shutdown".equals(type)) {
            logger.info("Websocket server is shutting down");
            maxAttempts = jsonMessage.get("attempts").getAsInt();
            delay = jsonMessage.get("delay").getAsInt();
            scheduleReconnect(delay, 1);
        }
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

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed: " + reason);
        if (reason.contains("refused")) {
            attempt++;
            if (attempt > maxAttempts) {
                logger.log(Level.SEVERE, "Max reconnect attempts reached, shutting down");

                scheduler.schedule(() -> {
                    Bukkit.getServer().shutdown();
                }, delay, TimeUnit.SECONDS);
            } else {
                logger.log(Level.SEVERE, "Failed to connect to backend database, attempting again in " + delay + " seconds");
                scheduleReconnect(delay, attempt);
            }
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.log(Level.SEVERE, "WebSocket error", ex);
    }

    public void scheduleReconnect(int seconds, int attempt) {
        scheduler.schedule(() -> {
            ByteBuildersPlotPlugin.webSocketClient.reconnect();
        }, seconds, TimeUnit.SECONDS);
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


}
