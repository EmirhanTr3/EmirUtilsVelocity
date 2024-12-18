package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.proxy.ProxyPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class FindCommand {

    @Command({"find", "sfind"})
    @CommandPermission("emirutilsvelocity.find")
    public void find(CommandSource sender, ProxyPlayer player) {
        Utils.sendMessage(sender,
                EmirUtilsVelocity.hasRedisBungee() ?
                        "<#00ccff>{0} <#00eeee>is found in <#00ccff>{1} <#00eeee>from proxy <#00ccff>{2}" :
                        "<#00ccff>{0} <#00eeee>is found in <#00ccff>{1}"
                ,
                player.getName(),
                player.getServer().getName(),
                player.getProxy()
        );
    }
}