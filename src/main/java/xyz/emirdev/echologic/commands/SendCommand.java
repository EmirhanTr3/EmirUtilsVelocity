package xyz.emirdev.echologic.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.Utils;

@Command("send")
@CommandPermission("echologic.send")
public class SendCommand {

    @Subcommand("all")
    public void send(CommandSource source, RegisteredServer server) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting all players to server <#00ccff><server><#00eeee>...",
                Placeholder.unparsed("server", server.getServerInfo().getName()));

        EchoLogic.getProxyUtils().connectAllPlayers(server.getServerInfo().getName());
    }

    @Subcommand("player")
    public void send(CommandSource source, ProxyPlayer player, RegisteredServer server) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting <#00ccff><player><#00eeee> to server <#00ccff><server><#00eeee>...",
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("server", server.getServerInfo().getName()));

        EchoLogic.getProxyUtils().connectPlayer(player.getUniqueId(), server.getServerInfo().getName());
    }

    @Subcommand("server")
    public void send(CommandSource source, RegisteredServer server, RegisteredServer targetServer) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting all players in server <#00ccff><server> <#00eeee>to server <#00ccff><targetserver><#00eeee>...",
                Placeholder.unparsed("server", server.getServerInfo().getName()),
                Placeholder.unparsed("targetserver", targetServer.getServerInfo().getName()));

        EchoLogic.getProxyUtils().connectAllPlayersInServer(
                server.getServerInfo().getName(),
                targetServer.getServerInfo().getName());
    }
}
