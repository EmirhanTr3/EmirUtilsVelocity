package xyz.emirdev.emirutilsvelocity.commands;

import java.net.InetSocketAddress;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;
import xyz.emirdev.emirutilsvelocity.utils.proxy.ProxyPlayer;

public class TransferCommand {

    @Command("transfer")
    @CommandPermission("emirutilsvelocity.transfer")
    public void transfer(CommandSource source, ProxyPlayer player, InetSocketAddress address) {
        Utils.sendMessage(source,
                "<#00eeee>Transferring <#00ccff><player><#00eeee> to <#00ccff><ip><#00eeee>...",
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("ip", address.getHostName() + ":" + address.getPort()));

        EmirUtilsVelocity.getProxyUtils().transferPlayer(player.getUniqueId(), address);
    }
}
