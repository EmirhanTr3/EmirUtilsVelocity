package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class ServerCommand {

    @Command("server")
    @CommandPermission("emirutilsvelocity.server")
    public void server(Player player, RegisteredServer server) {
        if (!player.hasPermission("emirutilsvelocity.server." + server.getServerInfo().getName())) {
            Utils.sendError(player, "You are not allowed to connect to %s!", server.getServerInfo().getName());
            return;
        }

        Utils.connectTo(player, server);
    }
}