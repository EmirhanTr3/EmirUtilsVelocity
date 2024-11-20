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

public class OwnerChatCommand {
    public static List<UUID> toggledPlayers = new ArrayList<>();

    @Command({"ownerchat", "oc"})
    @CommandPermission("emirutilsvelocity.ownerchat")
    public void staffchat(CommandSource sender, @Optional String message) {
        if (message != null) {
            if (sender instanceof Player player) {
                sendOwnerChatMessage(player, message);
            } else {
                sendOwnerChatMessage(message);
            }
        } else {
            if (sender instanceof Player player) {
                if (!toggledPlayers.contains(player.getUniqueId())) {
                    toggledPlayers.add(player.getUniqueId());
                    Utils.sendMessage(sender, "<green>You are <bold>now</bold> chatting in owner chat.");
                } else {
                    toggledPlayers.remove(player.getUniqueId());
                    Utils.sendMessage(sender, "<green>You are <bold>no longer</bold> chatting in owner chat.");
                }
            } else {
                Utils.sendError(sender, "You cannot toggle owner chat as console.");
            }
        }
    }

    public static void sendOwnerChatMessage(Player player, String message) {
        LuckPermsUtils.getDisplayName(player).thenAcceptAsync(displayname -> {
            RedisBungeeUtils.broadcastWithPermission(
                    "emirutilsvelocity.ownerchat",
                    "<dark_red>[<red>OC<dark_red>] <dark_red>[<red>%s<dark_red>] <dark_red>[<red>%s<dark_red>] <red>%s<red>: %s",
                    EmirUtilsVelocity.getRedisBungee().getProxyId(),
                    EmirUtilsVelocity.getRedisBungee().getServerFor(player.getUniqueId()).getName(),
                    displayname,
                    Utils.sanitize(message)
            );
        });
    }

    public static void sendOwnerChatMessage(String message) {
        RedisBungeeUtils.broadcastWithPermission(
                "emirutilsvelocity.ownerchat",
                "<dark_red>[<red>OC<dark_red>] <dark_red>[<red>%s<dark_red>] <red>Console<red>: %s",
                EmirUtilsVelocity.getRedisBungee().getProxyId(),
                Utils.sanitize(message)
        );
    }
}