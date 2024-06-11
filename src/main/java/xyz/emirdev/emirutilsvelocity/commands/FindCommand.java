package xyz.emirdev.emirutilsvelocity.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class FindCommand {

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
                    CommandSource source = context.getSource();
                    RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
                    String name = context.getArgument("player", String.class);

                    UUID uuid = redisbungee.getUuidFromName(name);

                    if (redisbungee.isPlayerOnline(uuid)) {
                        String server = redisbungee.getServerFor(redisbungee.getUuidFromName(name)).getName();

                        context.getSource().sendMessage(Utils.deserialize("<aqua>"+ name +" is found in "+ server));
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