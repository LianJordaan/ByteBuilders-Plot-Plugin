package io.github.lianjordaan.bytebuildersplotplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class ByteBuildersPlotPlugin extends JavaPlugin {
    private Logger logger;

    private WebSocketClient webSocketClient;

    @Override
    public void onEnable() {
        this.logger = Bukkit.getLogger();
        // Plugin startup logic
        logger.info("ByteBuilders Plot Plugin initialized!");

        try {
            Properties env = EnvLoader.loadEnv();
            String username = env.getProperty("USERNAME");
            Objects.requireNonNull(username, "USERNAME not set in .env file");
            String uri = "ws://192.168.0.125:3000?username=" + username + "&id=" + this.getServer().getPort();
            System.out.println("Connecting to URI: " + uri);
            webSocketClient = new WebSocketClientHandler(new URI(uri), logger);
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            // Handle URI syntax issues
            logger.log(Level.SEVERE, "Invalid WebSocket URI", e);
        } catch (IllegalArgumentException e) {
            // Handle illegal arguments like empty username
            logger.log(Level.SEVERE, "Configuration error", e);
        } catch (Exception e) {
            // Handle other exceptions
            logger.log(Level.SEVERE, "Failed to initialize WebSocket client", e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        webSocketClient.send("{\"type\": \"status\", \"status\": \"stopping\"}");
        webSocketClient.send("{\"type\": \"forwarded-message\", \"targetId\": \"proxy\", \"message\": \"stopping\"}");
        webSocketClient.close();

        // Perform immediate cleanup tasks
        getLogger().info("Starting shutdown tasks...");
    }
}
