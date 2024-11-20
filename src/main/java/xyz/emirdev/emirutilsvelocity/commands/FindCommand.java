package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class FindCommand {

    @Command({"find", "sfind"})
    @CommandPermission("emirutilsvelocity.find")
    public void find(CommandSource sender, RedisPlayer player) {
        Utils.sendMessage(sender,
                "<#00ccff>%s <#00eeee>is found in <#00ccff>%s <#00eeee>from proxy <#00ccff>%s",
                player.getName(),
                player.getServer().getName(),
                player.getProxy()
        );
    }
}