package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class ReplyCommand {

    @Command({"reply", "r", "ereply", "er", "sr"})
    @CommandPermission("emirutilsvelocity.message")
    public void reply(Player player, String message) {
        if (!MessageCommand.lastMessagedPlayer.containsKey(player.getUniqueId())) {
            Utils.sendError(player, "You did not message anyone.");
            return;
        }

        RedisPlayer target = new RedisPlayer(MessageCommand.lastMessagedPlayer.get(player.getUniqueId()));
        if (!target.isOnline()) {
            Utils.sendError(player,
                    "{0} is not online.",
                    target.getName()
            );
            return;
        }

        MessageCommand.sendMessage(player, target, message);
    }
}