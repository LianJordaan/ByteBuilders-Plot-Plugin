package io.github.lianjordaan.bytebuildersplotplugin.tabcompleters;

import io.github.lianjordaan.bytebuildersplotplugin.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class PlotCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            completions.addAll(Arrays.asList("spawn", "s", "setspawn", "codespace", "kick"));
        } else if (args.length == 2) {
            if ("spawn".equalsIgnoreCase(args[0]) || "s".equalsIgnoreCase(args[0])) {
                File[] folders = new File(".").listFiles();
                for (File folder : folders) {
                    if (folder.isDirectory() && folder.getName().startsWith("dim-")) {
                        completions.add(folder.getName().replaceFirst("dim-", ""));
                    }
                }
            } else if ("codespace".equalsIgnoreCase(args[0])) {
                completions.addAll(Arrays.asList("add", "remove"));
            } else if ("kick".equalsIgnoreCase(args[0])) {
                completions.add("*");
                completions.add("@a");
                completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            }
        }

        if (args.length >= 3) {
            if ("codespace".equalsIgnoreCase(args[0])) {
                if ("add".equalsIgnoreCase(args[1])) {
                    Set<String> potentialArgs = new HashSet<>();

                    // Check if any of the arguments contain -f, -l, or -d
                    boolean hasType = Arrays.stream(args)
                            .map(String::toLowerCase) // Normalize to lower case
                            .anyMatch(arg -> Arrays.asList("-f", "-l", "-d").contains(arg));

                    // Check if -c is present
                    boolean hasCompact = Arrays.stream(args)
                            .map(String::toLowerCase) // Normalize to lower case
                            .anyMatch("-c"::equals);

                    // Convert set of colors to list
                    Set<String> colorSet = PlotUtils.COLOR_TO_BLOCK_TYPE.keySet();
                    List<String> colorList = colorSet.stream().map(String::toLowerCase).toList(); // Normalize to lower case

                    // Check if any of the arguments match an entry in the color list
                    boolean hasColor = Arrays.stream(args)
                            .map(String::toLowerCase) // Normalize to lower case
                            .anyMatch(colorList::contains);

                    // If none of -f, -l, -d are present, add -f, -l, and -d
                    if (!hasType) {
                        potentialArgs.addAll(Arrays.asList("-f", "-l", "-d"));
                    }

                    // If -c is not present, add -c
                    if (!hasCompact) {
                        potentialArgs.add("-c");
                    }

                    // If no color is present, add all the colors
                    if (!hasColor) {
                        potentialArgs.addAll(colorList);
                    }

                    // Add potential arguments to completions
                    completions.addAll(potentialArgs);
                }
            }
        }

        return completions;
    }
}
