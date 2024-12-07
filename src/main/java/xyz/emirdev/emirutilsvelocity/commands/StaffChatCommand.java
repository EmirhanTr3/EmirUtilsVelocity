package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.LuckPermsUtils;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisBungeeUtils;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffChatCommand {
    public static List<UUID> toggledPlayers = new ArrayList<>();

    @Command({"staffchat", "sc"})
    @CommandPermission("emirutilsvelocity.staffchat")
    public void staffchat(CommandSource sender, @Optional String message) {
        if (message != null) {
            if (sender instanceof Player player) {
                sendStaffChatMessage(player, message);
            } else {
                sendStaffChatMessage(message);
            }
        } else {
            if (sender instanceof Player player) {
                if (!toggledPlayers.contains(player.getUniqueId())) {
                    toggledPlayers.add(player.getUniqueId());
                    Utils.sendMessage(sender, "<green>You are <bold>now</bold> chatting in staff chat.");
                } else {
                    toggledPlayers.remove(player.getUniqueId());
                    Utils.sendMessage(sender, "<green>You are <bold>no longer</bold> chatting in staff chat.");
                }
            } else {
                Utils.sendError(sender, "You cannot toggle staff chat as console.");
            }
        }
    }

    public static void sendStaffChatMessage(Player player, String message) {
        LuckPermsUtils.getDisplayName(player).thenAcceptAsync(displayname -> {
            RedisBungeeUtils.broadcastWithPermission(
                    "emirutilsvelocity.staffchat",
                    "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>{0}<dark_aqua>] <dark_aqua>[<aqua>{1}<dark_aqua>] <aqua>{2}<aqua>: {3}",
                    EmirUtilsVelocity.getRedisBungee().getProxyId(),
                    EmirUtilsVelocity.getRedisBungee().getServerFor(player.getUniqueId()).getName(),
                    displayname,
                    message
            );
        });
    }

    public static void sendStaffChatMessage(String message) {
        RedisBungeeUtils.broadcastWithPermission(
                "emirutilsvelocity.staffchat",
                "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>{0}<dark_aqua>] <aqua>Console<aqua>: {1}",
                EmirUtilsVelocity.getRedisBungee().getProxyId(),
                message
        );
    }
}