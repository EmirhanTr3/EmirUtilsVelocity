package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisBungeeUtils;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

@Command("send")
@CommandPermission("emirutilsvelocity.send")
public class SendCommand {

    @Subcommand("all")
    public void send(CommandSource source, RegisteredServer server) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting all players to server <#00ccff>%s<#00eeee>...",
                server.getServerInfo().getName()
        );

        RedisBungeeUtils.connectAllPlayers(server.getServerInfo().getName());
    }

    @Subcommand("player")
    public void send(CommandSource source, RedisPlayer player, RegisteredServer server) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting <#00ccff>%s<#00eeee> to server <#00ccff>%s<#00eeee>...",
                player.getName(),
                server.getServerInfo().getName()
        );

        RedisBungeeUtils.connectPlayer(player.getUniqueId(), server.getServerInfo().getName());
    }

    @Subcommand("server")
    public void send(CommandSource source, RegisteredServer server, RegisteredServer targetServer) {
        Utils.sendMessage(source,
                "<#00eeee>Connecting all players in server <#00ccff>%s <#00eeee>to server <#00ccff>%s<#00eeee>...",
                server.getServerInfo().getName(),
                targetServer.getServerInfo().getName()
        );

        RedisBungeeUtils.connectAllPlayersInServer(
                server.getServerInfo().getName(),
                targetServer.getServerInfo().getName()
        );
    }
}