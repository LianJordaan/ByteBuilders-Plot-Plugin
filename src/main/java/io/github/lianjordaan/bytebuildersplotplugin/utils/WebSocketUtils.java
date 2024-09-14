package io.github.lianjordaan.bytebuildersplotplugin.utils;

import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;

public class WebSocketUtils {
    // Function to send a message to a player via WebSocket
    public static void sendMessageToPlayer(String playerName, String message) {
        // Escape special characters for JSON
        String escapedMessage = message.replace("\"", "\\\"");
        String jsonPayload = String.format(
                "{\"type\":\"forwarded-message\",\"targetId\":\"proxy\",\"message\":\"msgPlayer\",\"data\":\"{\\\"player\\\":\\\"%s\\\",\\\"message\\\":\\\"%s\\\"}\"}",
                playerName,
                escapedMessage
        );

        // Send the JSON payload via WebSocket
        ByteBuildersPlotPlugin.webSocketClient.send(jsonPayload);
    }

    // Function to send a player to another server via WebSocket
    public static void sendPlayerToServer(String playerName, String serverName) {
        // Escape special characters for JSON
        String jsonPayload = String.format(
                "{\"type\":\"forwarded-message\",\"targetId\":\"proxy\",\"message\":\"sendPlayer\",\"data\":\"{\\\"server\\\":\\\"%s\\\",\\\"player\\\":\\\"%s\\\"}\"}",
                serverName,
                playerName
        );

        // Send the JSON payload via WebSocket
        ByteBuildersPlotPlugin.webSocketClient.send(jsonPayload);
    }
}