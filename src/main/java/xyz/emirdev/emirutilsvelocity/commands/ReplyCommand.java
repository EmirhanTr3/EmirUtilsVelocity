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
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.List;
import java.util.UUID;

public final class ReplyCommand {
    public static String name = "reply";
    public static List<String> aliases = List.of("r", "ereply", "er", "sreply", "sr");

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("reply")
            .requires(source -> source.hasPermission("emirutilsvelocity.message"))
            .executes(context -> {
                context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a message."));

                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                .executes(context -> {
                    CommandSource source = context.getSource();
                    RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

                    if (source instanceof Player player) {
                        if (EmirUtilsVelocity.playerLastMessagedPlayer.containsKey(player.getUniqueId())) {
                            UUID lastMessagedPlayer = EmirUtilsVelocity.playerLastMessagedPlayer.get(player.getUniqueId());
                            String target = redisbungee.getNameFromUuid(lastMessagedPlayer);

                            if (redisbungee.isPlayerOnline(lastMessagedPlayer)) {
                                MessageCommand.sendProxyMessage(
                                        source,
                                        target,
                                        context.getArgument("message", String.class)
                                );
                            } else {
                                source.sendMessage(Utils.deserialize("<red>" + target + " is not online."));
                            }
                        } else {
                            source.sendMessage(Utils.deserialize("<red>You did not message anyone."));
                        }
                    } else {
                        source.sendMessage(Utils.deserialize("<red>Cannot reply to messages as console."));
                    }

                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();

        return new BrigadierCommand(node);
    }
}