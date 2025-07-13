package xyz.emirdev.echologic.commands;

import java.net.InetSocketAddress;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.Utils;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;

public class TransferCommand {

    @Command("transfer")
    @CommandPermission("echologic.transfer")
    public void transfer(CommandSource source, ProxyPlayer player, InetSocketAddress address) {
        Utils.sendMessage(source,
                "<#00eeee>Transferring <#00ccff><player><#00eeee> to <#00ccff><ip><#00eeee>...",
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("ip", address.getHostName() + ":" + address.getPort()));

        EchoLogic.getProxyUtils().transferPlayer(player.getUniqueId(), address);
    }
}
