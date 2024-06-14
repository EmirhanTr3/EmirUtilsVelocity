package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.List;
import java.util.UUID;

public final class FindCommand {
    public static String name = "find";
    public static List<String> aliases = List.of("sfind");

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("find")
            .requires(source -> source.hasPermission("emirutilsvelocity.find"))
            .executes(context -> {
                context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a player."));
                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.greedyString())
                .suggests((ctx, builder) -> {
                    RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

                    redisbungee.getAllProxies().forEach(rproxy ->
                        redisbungee.getPlayersOnProxy(rproxy).forEach(uuid ->
                            builder.suggest(redisbungee.getNameFromUuid(uuid))
                        )
                    );

                    return builder.buildFuture();
                })
                .executes(context -> {
                    RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
                    String name = context.getArgument("player", String.class);

                    UUID uuid = redisbungee.getUuidFromName(name);

                    if (redisbungee.isPlayerOnline(uuid)) {
                        String server = redisbungee.getServerFor(uuid).getName();
                        String rproxy = redisbungee.getProxy(uuid);

                        context.getSource().sendMessage(Utils.deserialize("<aqua>"+ name +" is found in "+ server +" from proxy "+ rproxy));
                    } else {
                        context.getSource().sendMessage(Utils.deserialize("<red>"+ name +" is currently offline."));
                    }

                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();

        return new BrigadierCommand(node);
    }
}