package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.proxy.ProxyPlayer;
import xyz.emirdev.emirutilsvelocity.utils.LuckPermsUtils;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class KickCommand {

    @Command({ "pkick" })
    @CommandPermission("emirutilsvelocity.kick")
    public void kick(CommandSource sender, ProxyPlayer player, @Default("You have been kicked!") String reason) {
        LuckPermsUtils.hasPermission(player.getUniqueId(), "emirutilsvelocity.kick").thenAcceptAsync(hasPerm -> {
            if (hasPerm) {
                Utils.sendError(sender,
                        "You cannot kick <name>.",
                        Placeholder.unparsed("name", player.getName()));
                return;
            }

            Utils.sendMessage(sender,
                    "<#00eeee>You have kicked <#00ccff><name> <#00eeee>for <#00ccff><reason>",
                    Placeholder.unparsed("name", player.getName()),
                    Placeholder.component("reason", MiniMessage.miniMessage().deserialize(reason)));

            EmirUtilsVelocity.getProxyUtils().kickPlayer(player.getUniqueId(), reason);
        });
    }
}
