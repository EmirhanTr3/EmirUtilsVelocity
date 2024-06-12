package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.UUID;

public final class IgnoreCommand {

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("message")
            .executes(context -> {
                context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a subcommand."));

                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.literalArgumentBuilder("add")
                .executes(context -> {
                    context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a player to ignore."));

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.string())
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
                        if (context instanceof Player player) {
                            RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
                            String targetName = context.getArgument("player", String.class);

                            UUID targetUUID = redisbungee.getUuidFromName(name);

                            if (redisbungee.isPlayerOnline(uuid)) {
                                if (db has uuid) {
                                    // remove targetUUID from ignore db of player
                                    context.getSource().sendMessage(Utils.deserialize("<green>"+ targetName +"can <bold>now</bold> message you."));
                                } else {
                                    // add targetUUID to ignore db of player
                                    context.getSource().sendMessage(Utils.deserialize("<green>"+ targetName +" can <bold>no longer</bold> message you."));
                                }
                            } else {
                                context.getSource().sendMessage(Utils.deserialize("<red>"+ targetName +" is currently offline."));
                            }
                        } else {
                            context.getSource().sendMessage(Utils.deserialize("<red>You cannot ignore players as console."));
                        }

                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
            .build();

        return new BrigadierCommand(node);
    }
}