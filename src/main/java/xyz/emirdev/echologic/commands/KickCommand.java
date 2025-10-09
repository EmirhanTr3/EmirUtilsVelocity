package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.ProxyPlayerArgumentType;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.LuckPermsUtils;
import xyz.emirdev.echologic.utils.Utils;

public class KickCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("pkick")
                .requires(hasPermission("echologic.kick"))
                .then(requiredCustomArgumentBuilder("player", new ProxyPlayerArgumentType())
                        .executes(this::noReason)
                        .then(BrigadierCommand.requiredArgumentBuilder("reason", StringArgumentType.greedyString())
                                .executes(this::reason)))
                .build();
    }

    public int noReason(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        return execute(ctx, "You have been kicked!");
    }

    public int reason(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        return execute(ctx, StringArgumentType.getString(ctx, "reason"));
    }

    public int execute(CommandContext<CommandSource> ctx, String reason) throws CommandSyntaxException {
        ProxyPlayer player = getCustomArgument(ctx, "player", ProxyPlayerArgumentType.class);

        LuckPermsUtils.hasPermission(player.getUniqueId(), "echologic.kick").thenAcceptAsync(hasPerm -> {
            if (hasPerm && !(ctx.getSource() instanceof Player senderPlayer
                    && player.getUniqueId().equals(senderPlayer.getUniqueId()))) {
                Utils.sendError(ctx.getSource(),
                        "You cannot kick <name>.",
                        Placeholder.unparsed("name", player.getName()));
                return;
            }

            Utils.sendMessage(ctx.getSource(),
                    "<#00eeee>You have kicked <#00ccff><name> <#00eeee>for <#00ccff><reason>",
                    Placeholder.unparsed("name", player.getName()),
                    Placeholder.component("reason", MiniMessage.miniMessage().deserialize(reason)));

            EchoLogic.getProxyUtils().kickPlayer(player.getUniqueId(), reason);
        });

        return 1;
    }
}
