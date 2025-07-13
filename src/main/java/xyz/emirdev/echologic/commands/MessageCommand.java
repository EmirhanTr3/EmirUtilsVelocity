package xyz.emirdev.echologic.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCommand {
    public static Map<UUID, UUID> lastMessagedPlayer = new HashMap<>();

    @Command({ "message", "msg", "m", "tell", "t", "whisper", "w", "emsg", "smsg" })
    @CommandPermission("echologic.message")
    public void message(CommandSource sender, ProxyPlayer target, String message) {
        if (sender instanceof Player player) {
            sendMessage(player, target, message);
        } else {
            sendMessage(target, message);
        }
    }

    private static void _sendMessage(CommandSource sender, ProxyPlayer target, String message) {
        Utils.sendMessage(sender,
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>me <#41BBFF>→ <#2595CC><target><#41BBFF>: <#60CCFF><message>",
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("message", message));
    }

    public static void sendMessage(Player player, ProxyPlayer target, String message) {
        if (EchoLogic.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId()) ||
                EchoLogic.getDatabase().isIgnored(target.getUniqueId(), player.getUniqueId())) {
            Utils.sendError(player,
                    "You can't send a message to <target>.",
                    Placeholder.unparsed("target", target.getName()));
            return;
        }

        _sendMessage(player, target, message);

        target.sendMessage(
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC><player> <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF><message>",
                Placeholder.unparsed("player", player.getUsername()),
                Placeholder.unparsed("message", message));
        EchoLogic.getProxyUtils().sendSocialSpyMessage(player, target, message);

        lastMessagedPlayer.put(player.getUniqueId(), target.getUniqueId());
        lastMessagedPlayer.put(target.getUniqueId(), player.getUniqueId());
    }

    public static void sendMessage(ProxyPlayer target, String message) {
        _sendMessage(EchoLogic.getProxy().getConsoleCommandSource(), target, message);

        target.sendMessage(
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>Console <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF><message>",
                Placeholder.unparsed("message", message));
    }
}
