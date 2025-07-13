package xyz.emirdev.echologic.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.utils.Utils;

public class ServerCommand {

    @Command("server")
    @CommandPermission("echologic.server")
    public void server(Player player, RegisteredServer server) {
        if (!player.hasPermission("echologic.server." + server.getServerInfo().getName())) {
            Utils.sendError(player,
                    "You are not allowed to connect to <server>!",
                    Placeholder.unparsed("server", server.getServerInfo().getName()));
            return;
        }

        Utils.connectPlayer(player, server);
    }
}
