package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisBungeeUtils;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.utils.LuckPermsUtils;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class KickCommand {

    @Command({"pkick"})
    @CommandPermission("emirutilsvelocity.kick")
    public void kick(CommandSource sender, RedisPlayer player, @Default("You have been kicked!") String reason) {
        LuckPermsUtils.hasPermission(player.getUniqueId(), "emirutilsvelocity.kick").thenAcceptAsync(hasPerm -> {
            if (hasPerm) {
                Utils.sendError(sender,
                        "You cannot kick %s.",
                        player.getName()
                );
                return;
            }

            Utils.sendMessage(sender,
                    "<#00eeee>You have kicked <#00ccff>%s <#00eeee>for <#00ccff>%s",
                    player.getName(),
                    Utils.sanitize(reason)
            );

            RedisBungeeUtils.kickPlayer(player.getUniqueId(), Utils.sanitize(reason));
        });
    }
}