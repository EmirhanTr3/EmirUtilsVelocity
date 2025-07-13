package xyz.emirdev.echologic.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.LuckPermsUtils;
import xyz.emirdev.echologic.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OwnerChatCommand {
    public static List<UUID> toggledPlayers = new ArrayList<>();

    @Command({ "ownerchat", "oc" })
    @CommandPermission("echologic.ownerchat")
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
            EchoLogic.getProxyUtils().broadcastWithPermission(
                    "echologic.ownerchat",
                    EchoLogic.hasRedisBungee()
                            ? "<dark_red>[<red>OC<dark_red>] <dark_red>[<red><proxy><dark_red>] <dark_red>[<red><server><dark_red>] <red><displayname><red>: <message>"
                            : "<dark_red>[<red>OC<dark_red>] <dark_red>[<red><server><dark_red>] <red><displayname><red>: <message>",
                    Placeholder.unparsed("proxy",
                            EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId()
                                    : "null"),
                    EchoLogic.hasRedisBungee()
                            ? Placeholder.unparsed("server",
                                    RedisBungeeAPI.getRedisBungeeApi().getServerFor(player.getUniqueId()).getName())
                            : Placeholder.unparsed("server", player.getCurrentServer().get().getServerInfo().getName()),
                    Placeholder.component("displayname", displayname),
                    Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
        });
    }

    public static void sendOwnerChatMessage(String message) {
        EchoLogic.getProxyUtils().broadcastWithPermission(
                "echologic.ownerchat",
                EchoLogic.hasRedisBungee()
                        ? "<dark_red>[<red>OC<dark_red>] <dark_red>[<red><proxy><dark_red>] <red>Console<red>: <message>"
                        : "<dark_red>[<red>OC<dark_red>] <red>Console<red>: <message>",
                Placeholder.unparsed("proxy",
                        EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId() : "null"),
                Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
    }
}
