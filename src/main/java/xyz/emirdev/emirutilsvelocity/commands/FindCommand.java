package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.proxy.ProxyPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class FindCommand {

        @Command({ "find", "sfind" })
        @CommandPermission("emirutilsvelocity.find")
        public void find(CommandSource sender, ProxyPlayer player) {
                Utils.sendMessage(sender,
                                EmirUtilsVelocity.hasRedisBungee()
                                                ? "<#00ccff><player> <#00eeee>is found in <#00ccff><server> <#00eeee>from proxy <#00ccff><proxy>"
                                                : "<#00ccff><player> <#00eeee>is found in <#00ccff><server>",
                                Placeholder.unparsed("player", player.getName()),
                                Placeholder.unparsed("server", player.getServer().getName()),
                                Placeholder.unparsed("proxy", player.getProxy()));
        }
}
