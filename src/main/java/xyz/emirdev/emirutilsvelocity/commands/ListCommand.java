package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.command.CommandSource;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.*;

public class ListCommand {

    @Command({"list", "slist"})
    @CommandPermission("emirutilsvelocity.list")
    public void list(CommandSource sender) {
        RedisBungeeAPI redisBungee = EmirUtilsVelocity.getRedisBungee();
        Map<String, List<String>> servers = new HashMap<>();

        for (UUID uuid : redisBungee.getPlayersOnline()) {
            RedisPlayer player = new RedisPlayer(uuid);

            List<String> players = Objects.requireNonNullElse(
                    servers.get(player.getServer().getName()),
                    new ArrayList<>()
            );

            players.add(player.getName());
            servers.put(player.getServer().getName(), players);
        }

        Utils.sendMessage(sender,
                "<yellow>There are currently %s players connected to the network.",
                redisBungee.getPlayerCount()
        );

        for (Map.Entry<String, List<String>> entry : servers.entrySet()) {
            String server = entry.getKey();
            List<String> players = entry.getValue();

            Utils.sendMessage(sender,
                    "<dark_aqua>[%s] <gray>(%s)<white>: %s",
                    server,
                    players.size(),
                    String.join(", ", players)
            );
        }
    }
}