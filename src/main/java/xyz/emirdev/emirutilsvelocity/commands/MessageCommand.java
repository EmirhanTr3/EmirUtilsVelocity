package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisBungeeUtils;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCommand {
    public static Map<UUID, UUID> lastMessagedPlayer = new HashMap<>();

    @Command({"message", "msg", "m", "tell", "t", "whisper", "w", "emsg", "smsg"})
    @CommandPermission("emirutilsvelocity.message")
    public void message(CommandSource sender, RedisPlayer target, String message) {
        if (sender instanceof Player player) {
            sendMessage(player, target, message);
        } else {
            sendMessage(target, message);
        }
    }

    private static void _sendMessage(CommandSource sender, RedisPlayer target, String message) {
        Utils.sendMessage(sender,
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>me <#41BBFF>→ <#2595CC>%s<#41BBFF>: <#60CCFF>%s",
                target.getName(),
                Utils.sanitize(message)
        );
    }

    public static void sendMessage(Player player, RedisPlayer target, String message) {
        if (
                EmirUtilsVelocity.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId()) ||
                EmirUtilsVelocity.getDatabase().isIgnored(target.getUniqueId(), player.getUniqueId())
        ) {
            Utils.sendError(player,
                    "You can't send a message to %s.",
                    target.getName()
            );
            return;
        }

        _sendMessage(player, target, message);

        target.sendMessage(
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>%s <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF>%s",
                player.getUsername(),
                Utils.sanitize(message)
        );
        RedisBungeeUtils.sendSocialSpyMessage(player, target, message);

        lastMessagedPlayer.put(player.getUniqueId(), target.getUniqueId());
        lastMessagedPlayer.put(target.getUniqueId(), player.getUniqueId());
    }

    public static void sendMessage(RedisPlayer target, String message) {
        _sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), target, message);

        target.sendMessage(
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>Console <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF>%s",
                Utils.sanitize(message)
        );
    }
}