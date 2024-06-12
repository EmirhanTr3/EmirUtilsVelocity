package xyz.emirdev.emirutilsvelocity.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class MessageCommand {

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("message")
            .executes(context -> {
                context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a player to message."));

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
                    context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a message"));

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                    .executes(context -> {
                        CommandSource source = context.getSource();

                        sendProxyMessage(
                            source,
                            context.getArgument("player", String.class),
                            context.getArgument("message", String.class)
                        );

                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
            .build();

        return new BrigadierCommand(node);
    }

    public static void sendProxyMessage(CommandSource source, String target, String message) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

        UUID uuid = redisbungee.getUuidFromName(target);

        if (redisbungee.isPlayerOnline(uuid)) {
            Gson gson = new Gson();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("proxyId", redisbungee.getProxyId());
            map.put("isPlayer", (source instanceof Player));
            map.put("name", ((source instanceof Player player) ? player.getUsername() : "Console"));
            map.put("uuid", ((source instanceof Player player) ? player.getUniqueId() : null));
            map.put("targetuuid", uuid);
            map.put("message", message);

            String json = gson.toJson(map);

            redisbungee.sendChannelMessage("emirutilsvelocity:message", json);
        } else {
            source.sendMessage(Utils.deserialize("<red>"+ target +" is currently offline."));
        }
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (event.getChannel().equals("emirutilsvelocity:message")) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            if ((Boolean) map.get("isPlayer")) {
                sendMessage(
                    UUID.fromString((String) map.get("uuid")),
                    UUID.fromString((String) map.get("targetuuid")),
                    (String) map.get("message")
                );
            } else {
                sendConsoleMessage(
                    UUID.fromString((String) map.get("targetuuid")),
                    (String) map.get("message")
                );
            }
        }
    }

    public static void sendMessage(UUID playerUUID, UUID targetUUID, String message) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
        Optional<Player> optionalPlayer = EmirUtilsVelocity.proxy.getPlayer(playerUUID);
        Optional<Player> optionalTarget = EmirUtilsVelocity.proxy.getPlayer(targetUUID);

        EmirUtilsVelocity.playerLastMessagedPlayer.put(playerUUID, targetUUID);
        EmirUtilsVelocity.playerLastMessagedPlayer.put(targetUUID, playerUUID);

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            String targetName = redisbungee.getNameFromUuid(targetUUID);
            Component playerComp = Utils.deserialize("<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>me <#41BBFF>→ <#2595CC>" + targetName + "<#41BBFF>: <#60CCFF>" + message);
            player.sendMessage(playerComp);
        }
        if (optionalTarget.isPresent()) {
            Player target = optionalTarget.get();
            String playerName = redisbungee.getNameFromUuid(playerUUID);
            Component targetComp = Utils.deserialize("<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>" + playerName + " <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF>" + message);
            target.sendMessage(targetComp);
        }
    }

    public static void sendConsoleMessage(UUID targetUUID, String message) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
        Optional<Player> optionalTarget = EmirUtilsVelocity.proxy.getPlayer(targetUUID);

        String targetName = redisbungee.getNameFromUuid(targetUUID);
        Component consoleComp = Utils.deserialize("<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>me <#41BBFF>→ <#2595CC>"+ targetName +"<#41BBFF>: <#60CCFF>" + message);
        EmirUtilsVelocity.proxy.getConsoleCommandSource().sendMessage(consoleComp);

        if (optionalTarget.isPresent()) {
            Player target = optionalTarget.get();
            Component targetComp = Utils.deserialize("<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>Console <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF>" + message);
            target.sendMessage(targetComp);
        }
    }
}