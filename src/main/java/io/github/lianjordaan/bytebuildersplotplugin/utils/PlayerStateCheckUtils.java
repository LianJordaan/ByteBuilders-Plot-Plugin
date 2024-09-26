package io.github.lianjordaan.bytebuildersplotplugin.utils;

import org.bukkit.entity.Player;

public class PlayerStateCheckUtils {
    public static boolean isPlayerInAdminBypass(Player player) {
        return player.hasMetadata("admin-bypass");
    }
}
