package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ListCommand extends xyz.emirdev.emirutilsvelocity.Command {
    public static String name = "list";
    public static List<String> aliases = List.of("slist");

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("list")
            .requires(source -> source.hasPermission("emirutilsvelocity.list"))
            .executes(context -> {
                CommandSource source = context.getSource();
                RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

                Map<String, List<String>> servers = new HashMap<>();

                redisbungee.getAllProxies().forEach(rproxy -> {
                    redisbungee.getPlayersOnProxy(rproxy).forEach(uuid -> {
                        String server = redisbungee.getServerFor(uuid).getName();

                        List<String> players = servers.get(server);
                        if (players == null) {
                            players = new ArrayList<>();
                        }
                        players.add(redisbungee.getNameFromUuid(uuid));

                        servers.put(server, players);
                    });
                });

                source.sendMessage(Utils.deserialize("<yellow>There are currently "+ redisbungee.getPlayerCount() +" players connected to the network."));
                servers.forEach((server, players) -> {
                    source.sendMessage(Utils.deserialize("<dark_aqua>["+ server +"] <gray>("+ players.size() +")<white>: "+ String.join(", ", players)));
                });

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(node);
    }
}