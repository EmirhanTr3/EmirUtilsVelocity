package xyz.emirdev.echologic.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.Utils;

import java.util.*;

public class ListCommand {

    @Command({ "list", "slist" })
    @CommandPermission("echologic.list")
    public void list(CommandSource sender) {
        Map<String, List<String>> servers = new HashMap<>();

        if (EchoLogic.hasRedisBungee()) {
            RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();

            for (UUID uuid : redisBungee.getPlayersOnline()) {
                ProxyPlayer player = new ProxyPlayer(uuid);

                List<String> players = Objects.requireNonNullElse(
                        servers.get(player.getServer().getName()),
                        new ArrayList<>());

                players.add(player.getName());
                servers.put(player.getServer().getName(), players);
            }
        } else {
            for (Player player : EchoLogic.getProxy().getAllPlayers()) {
                List<String> players = Objects.requireNonNullElse(
                        servers.get(player.getCurrentServer().get().getServerInfo().getName()),
                        new ArrayList<>());

                players.add(player.getUsername());
                servers.put(player.getCurrentServer().get().getServerInfo().getName(), players);
            }
        }

        Utils.sendMessage(sender,
                "<yellow>There are currently <count> players connected to the network.",
                EchoLogic.hasRedisBungee()
                        ? Placeholder.unparsed("count",
                                String.valueOf(RedisBungeeAPI.getRedisBungeeApi().getPlayerCount()))
                        : Placeholder.unparsed("count", String.valueOf(EchoLogic.getProxy().getPlayerCount())));

        for (Map.Entry<String, List<String>> entry : servers.entrySet()) {
            String server = entry.getKey();
            List<String> players = entry.getValue();

            Utils.sendMessage(sender,
                    "<dark_aqua>[<server>] <gray>(<size>)<white>: <players>",
                    Placeholder.unparsed("server", server),
                    Placeholder.unparsed("size", String.valueOf(players.size())),
                    Placeholder.unparsed("players", String.join(", ", players)));
        }
    }
}
