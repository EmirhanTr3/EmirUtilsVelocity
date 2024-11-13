package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ServerCommand {

    @Command("server")
    @CommandPermission("emirutilsvelocity.server")
    public void server(Player player, RegisteredServer server) {
        if (!player.hasPermission("emirutilsvelocity.server." + server.getServerInfo().getName())) {
            Utils.sendError(player, "You are not allowed to connect to %s!", server.getServerInfo().getName());
            return;
        }

        Utils.sendMessage(player,
                "<#00ffff>Connecting to server <#00cccc>%s<#00ffff>...",
                server.getServerInfo().getName()
        );

        CompletableFuture<ConnectionRequestBuilder.Result> request = player.createConnectionRequest(server).connect();

        request.thenAcceptAsync(action -> {
            if (!action.isSuccessful()) {
                if (action.getStatus() == ConnectionRequestBuilder.Status.ALREADY_CONNECTED) {
                    Utils.sendError(player, "You are already connected to this server.");
                    return;
                }

                Component message = Utils.format(
                        "<#ee4444>Unable to connect to %s. ",
                        server.getServerInfo().getName()
                );
                Optional<Component> reason = action.getReasonComponent();

                if (reason.isPresent()) {
                    message = message.append(reason.get());
                }

                player.sendMessage(message);
            }
        });
    }
}