package io.github.lianjordaan.bytebuildersplotplugin.commands;

import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import io.github.lianjordaan.bytebuildersplotplugin.WebSocketClientHandler;
import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import io.github.lianjordaan.bytebuildersplotplugin.utils.PlotUtils;
import io.github.lianjordaan.bytebuildersplotplugin.utils.WebSocketUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlotCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Handle both "plot" and "p" commands
        if (command.getName().equalsIgnoreCase("plot") || command.getName().equalsIgnoreCase("p")) {
            if (args.length == 0) {
                player.sendMessage("Please specify a subcommand.");
                return true;
            }

            String subcommand = args[0];

            switch (subcommand.toLowerCase()) {
                case "kick":
                    if (args[1].equalsIgnoreCase("*")) {
                        Bukkit.getOnlinePlayers().forEach(player1 -> {
                            if (player1 == player) {
                                return;
                            }

                            WebSocketUtils.sendPlayerToServer(player1.getName(), "lobby");
                            WebSocketUtils.sendMessageToPlayer(player1.getName(), "<red>You have been kicked by a plot admin.");

                        });
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>You have kicked all players from this plot"));

                    } else {

                        Player targetPlayer = null;

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.getName().equalsIgnoreCase(args[1])) {
                                targetPlayer = onlinePlayer;
                                break;
                            }
                        }

                        if (targetPlayer != null) {

                            WebSocketUtils.sendPlayerToServer(targetPlayer.getName(), "lobby");
                            WebSocketUtils.sendMessageToPlayer(targetPlayer.getName(), "<red>You have been kicked by a plot admin.");
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>You have kicked " + targetPlayer.getName() + " from this plot"));
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error: <gray>Could not find that player."));
                        }
                    }
                    break;
                case "spawn":
                    // Implement spawn functionality here

                    if (player == player) { // add permission checks later
                        // check if 2nd arg is set
                        if (args.length == 2) {
                            // check if a folder exists
                            File folder = new File("dim-" + args[1]);
                            if (folder.exists()) {
                                Location spawnLocation = LocationUtils.getPlotSpawnLocation("dim-" + args[1]);
                                player.teleport(spawnLocation);
                                player.sendMessage("You have been teleported to the plot spawn.");
                            } else {
                                player.sendMessage("That world does not exist.");
                            }
                        } else {
                            Location spawnLocation = LocationUtils.getPlotSpawnLocation(player.getWorld().getName());
                            player.teleport(spawnLocation);
                            player.sendMessage("You have been teleported to the plot spawn.");
                        }
                    }
                    break;
                case "setspawn":
                    // Implement setspawn functionality here
                    if (player.getWorld().getName().equals("dim-play") || player.getWorld().getName().equals("dim-code")) {
                        LocationUtils.savePlotSpawnLocation(player.getLocation());
                        player.sendMessage("Plot spawn has been set to: " + player.getLocation());
                    }
                    break;
                case "codespace":
                    if (player.getWorld().getName().equals("dim-code")) {
                        if (args[1].equalsIgnoreCase("add")) {
                            if (args.length > 1) {
                                PlotUtils.handleCodespaceAddCommand(player, args);
                            } else {
                                player.sendMessage("Usage: /plot codespace add [options]");
                            }
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            if (args.length > 1) {
                                PlotUtils.handleCodespaceRemoveCommand(player, args);
                            } else {
                                player.sendMessage("Usage: /plot codespace remove [amount]");
                            }
                        }
                    } else {
                        player.sendMessage("You must be in dev mode to use this command. (reminder, make dev mode)");
                    }
                    break;
                default:
                    player.sendMessage("Unknown subcommand. Use /plot spawn or /plot setspawn.");
                    break;
            }
            return true;
        }

        return false;
    }
}