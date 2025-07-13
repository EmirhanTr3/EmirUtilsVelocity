package xyz.emirdev.echologic.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import revxrsal.commands.annotation.Command;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.Utils;

public class HubCommand {

    @Command("hub")
    public void hub(Player player) {
        RegisteredServer server = EchoLogic.getConfig().getHubServer();
        if (server == null) {
            Utils.sendError(player, "Invalid hub server defined in config.");
            return;
        }

        Utils.connectPlayer(player, server);
    }
}
