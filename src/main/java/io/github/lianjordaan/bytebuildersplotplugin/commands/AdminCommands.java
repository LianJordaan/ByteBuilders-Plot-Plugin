package io.github.lianjordaan.bytebuildersplotplugin.commands;

import io.github.lianjordaan.bytebuildersplotplugin.ByteBuildersPlotPlugin;
import io.github.lianjordaan.bytebuildersplotplugin.utils.LocationUtils;
import io.github.lianjordaan.bytebuildersplotplugin.utils.PlayerStateCheckUtils;
import io.github.lianjordaan.bytebuildersplotplugin.utils.PlotUtils;
import io.github.lianjordaan.bytebuildersplotplugin.utils.WebSocketUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;

public class AdminCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Handle both "plot" and "p" commands
        if (command.getName().equalsIgnoreCase("admin") || command.getName().equalsIgnoreCase("a")) {
            if (args.length == 0) {
                player.sendMessage("Please specify a subcommand.");
                return true;
            }

            String subcommand = args[0];

            switch (subcommand.toLowerCase()) {
                case "bypass":
                    if (args.length == 1) {
                        if (PlayerStateCheckUtils.isPlayerInAdminBypass(player)) {
                            player.sendMessage("You have disabled admin bypass mode.");
                            player.removeMetadata("admin-bypass", ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class));
                        } else {
                            player.sendMessage("You have enabled admin bypass mode.");
                            player.setMetadata("admin-bypass", new FixedMetadataValue(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), false));
                        }
                    } else {
                        if (args[1].equalsIgnoreCase("on")) {
                            player.sendMessage("You have enabled admin bypass mode.");
                            player.setMetadata("admin-bypass", new FixedMetadataValue(ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class), true));
                        } else if (args[1].equalsIgnoreCase("off")) {
                            player.sendMessage("You have disabled admin bypass mode.");
                            player.removeMetadata("admin-bypass", ByteBuildersPlotPlugin.getPlugin(ByteBuildersPlotPlugin.class));
                        } else {
                            player.sendMessage("Invalid argument. Use /admin bypass [on/off]");
                        }
                    }
                    break;
                default:
                    player.sendMessage("Unknown subcommand. Use /admin bypass [on/off]");
                    break;
            }
            return true;
        }

        return false;
    }
}