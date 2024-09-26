package io.github.lianjordaan.bytebuildersplotplugin.tabcompleters;

import io.github.lianjordaan.bytebuildersplotplugin.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class AdminCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            completions.addAll(Arrays.asList("bypass"));
        } else if (args.length == 2) {
            if ("bypass".equalsIgnoreCase(args[0])) {
                completions.addAll(Arrays.asList("on", "off"));
            }
        }

        return completions;
    }
}
