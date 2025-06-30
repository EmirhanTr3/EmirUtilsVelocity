package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.LuckPermsUtils;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffChatCommand {
    public static List<UUID> toggledPlayers = new ArrayList<>();

    @Command({ "staffchat", "sc" })
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
            EmirUtilsVelocity.getProxyUtils().broadcastWithPermission(
                    "emirutilsvelocity.staffchat",
                    EmirUtilsVelocity.hasRedisBungee()
                            ? "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua><proxy><dark_aqua>] <dark_aqua>[<aqua><server><dark_aqua>] <aqua><displayname><aqua>: <message>"
                            : "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua><server><dark_aqua>] <aqua><displayname><aqua>: <message>",
                    Placeholder.unparsed("proxy",
                            EmirUtilsVelocity.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId()
                                    : "null"),
                    EmirUtilsVelocity.hasRedisBungee()
                            ? Placeholder.unparsed("server",
                                    RedisBungeeAPI.getRedisBungeeApi().getServerFor(player.getUniqueId()).getName())
                            : Placeholder.unparsed("server", player.getCurrentServer().get().getServerInfo().getName()),
                    Placeholder.component("displayname", displayname),
                    Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
        });
    }

    public static void sendStaffChatMessage(String message) {
        EmirUtilsVelocity.getProxyUtils().broadcastWithPermission(
                "emirutilsvelocity.staffchat",
                EmirUtilsVelocity.hasRedisBungee()
                        ? "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua><proxy><dark_aqua>] <aqua>Console<aqua>: <message>"
                        : "<dark_aqua>[<aqua>SC<dark_aqua>] <aqua>Console<aqua>: <message>",
                Placeholder.unparsed("proxy",
                        EmirUtilsVelocity.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId() : "null"),
                Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
    }
}
