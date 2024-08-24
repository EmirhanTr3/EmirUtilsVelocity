package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.PluginData;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.List;
import java.util.UUID;

public final class IgnoreCommand extends xyz.emirdev.emirutilsvelocity.Command {
    public static String name = "ignore";
    public static List<String> aliases = List.of("signore", "eignore");

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("ignore")
            .requires(source -> source.hasPermission("emirutilsvelocity.message"))
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
                        if (context.getSource() instanceof Player player) {
                            RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
                            String targetName = context.getArgument("player", String.class);

                            PluginData data = EmirUtilsVelocity.data;
                            UUID playerUUID = player.getUniqueId();
                            UUID targetUUID = redisbungee.getUuidFromName(targetName);

                            if (redisbungee.isPlayerOnline(targetUUID)) {
                                if (!data.getIgnoredPlayers(playerUUID).contains(targetUUID)) {
                                    data.addIgnoredPlayer(playerUUID, targetUUID);
                                    context.getSource().sendMessage(Utils.deserialize("<green>"+ targetName +" can <bold>no longer</bold> message you."));
                                } else {
                                    context.getSource().sendMessage(Utils.deserialize("<red>You already have "+ targetName +" ignored."));
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
            .then(BrigadierCommand.literalArgumentBuilder("remove")
                .executes(context -> {
                    context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a player to unignore."));

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
                        if (context.getSource() instanceof Player player) {
                            RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
                            String targetName = context.getArgument("player", String.class);

                            PluginData data = EmirUtilsVelocity.data;
                            UUID playerUUID = player.getUniqueId();
                            UUID targetUUID = redisbungee.getUuidFromName(targetName);

                            if (redisbungee.isPlayerOnline(targetUUID)) {
                                if (data.getIgnoredPlayers(playerUUID).contains(targetUUID)) {
                                    data.removeIgnoredPlayer(playerUUID, targetUUID);
                                    context.getSource().sendMessage(Utils.deserialize("<green>"+ targetName +" can <bold>now</bold> message you."));
                                } else {
                                    context.getSource().sendMessage(Utils.deserialize("<red>You do not have "+ targetName +" ignored."));
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
            .then(BrigadierCommand.literalArgumentBuilder("list")
                .executes(context -> {
                    if (context.getSource() instanceof Player player) {
                        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
                        List<UUID> ignoredPlayers = EmirUtilsVelocity.data.getIgnoredPlayers(player.getUniqueId());

                        if (!ignoredPlayers.isEmpty()) {
                            List<String> nameList = ignoredPlayers.stream().map(redisbungee::getNameFromUuid).toList();
                            context.getSource().sendMessage(Utils.deserialize("<#e64919><bold>Ignored Players</bold></#e64919> <#b5573a>("+nameList.size()+")</#b5573a><#e64919>:</#e64919>"));
                            context.getSource().sendMessage(Utils.deserialize("  <#e64919>"+String.join(", ", nameList)+"</#e64919>"));
                        } else {
                            context.getSource().sendMessage(Utils.deserialize("<red>You do not have anyone ignored."));
                        }
                    } else {
                        context.getSource().sendMessage(Utils.deserialize("<red>You cannot ignore players as console."));
                    }

                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();

        return new BrigadierCommand(node);
    }
}