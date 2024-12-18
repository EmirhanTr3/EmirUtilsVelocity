package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.proxy.ProxyPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

@Command("send")
@CommandPermission("emirutilsvelocity.send")
public class SendCommand {

    @Subcommand("all")
    public void send(CommandSource source, RegisteredServer server) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting all players to server <#00ccff>{0}<#00eeee>...",
                server.getServerInfo().getName()
        );

        EmirUtilsVelocity.getProxyUtils().connectAllPlayers(server.getServerInfo().getName());
    }

    @Subcommand("player")
    public void send(CommandSource source, ProxyPlayer player, RegisteredServer server) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting <#00ccff>{0}<#00eeee> to server <#00ccff>{1}<#00eeee>...",
                player.getName(),
                server.getServerInfo().getName()
        );

        EmirUtilsVelocity.getProxyUtils().connectPlayer(player.getUniqueId(), server.getServerInfo().getName());
    }

    @Subcommand("server")
    public void send(CommandSource source, RegisteredServer server, RegisteredServer targetServer) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting all players in server <#00ccff>{0} <#00eeee>to server <#00ccff>{1}<#00eeee>...",
                server.getServerInfo().getName(),
                targetServer.getServerInfo().getName()
        );

        EmirUtilsVelocity.getProxyUtils().connectAllPlayersInServer(
                server.getServerInfo().getName(),
                targetServer.getServerInfo().getName()
        );
    }
}