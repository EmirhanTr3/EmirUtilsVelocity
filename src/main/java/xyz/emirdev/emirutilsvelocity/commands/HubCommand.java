package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import revxrsal.commands.annotation.Command;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class HubCommand {

    @Command("hub")
    public void hub(Player player) {
        RegisteredServer server = EmirUtilsVelocity.getConfig().getHubServer();
        if (server == null) {
            Utils.sendError(player, "Invalid hub server defined in config.");
            return;
        }

        Utils.connectTo(player, server);
    }
}