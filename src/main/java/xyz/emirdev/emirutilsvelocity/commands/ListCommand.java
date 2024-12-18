package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.proxy.ProxyPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.*;

public class ListCommand {

    @Command({"list", "slist"})
    @CommandPermission("emirutilsvelocity.list")
    public void list(CommandSource sender) {
        Map<String, List<String>> servers = new HashMap<>();

        if (EmirUtilsVelocity.hasRedisBungee()) {
            RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();

            for (UUID uuid : redisBungee.getPlayersOnline()) {
                ProxyPlayer player = new ProxyPlayer(uuid);

                List<String> players = Objects.requireNonNullElse(
                        servers.get(player.getServer().getName()),
                        new ArrayList<>()
                );

                players.add(player.getName());
                servers.put(player.getServer().getName(), players);
            }
        } else {
            for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
                List<String> players = Objects.requireNonNullElse(
                        servers.get(player.getCurrentServer().get().getServerInfo().getName()),
                        new ArrayList<>()
                );

                players.add(player.getUsername());
                servers.put(player.getCurrentServer().get().getServerInfo().getName(), players);
            }
        }

        Utils.sendMessage(sender,
                "<yellow>There are currently {0} players connected to the network.",
                EmirUtilsVelocity.hasRedisBungee() ?
                        RedisBungeeAPI.getRedisBungeeApi().getPlayerCount() :
                        EmirUtilsVelocity.getProxy().getPlayerCount()
        );

        for (Map.Entry<String, List<String>> entry : servers.entrySet()) {
            String server = entry.getKey();
            List<String> players = entry.getValue();

            Utils.sendMessage(sender,
                    "<dark_aqua>[{0}] <gray>({1})<white>: {2}",
                    server,
                    players.size(),
                    String.join(", ", players)
            );
        }
    }
}