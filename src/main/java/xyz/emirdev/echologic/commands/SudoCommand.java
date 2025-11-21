package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.ProxyPlayerArgumentType;
import xyz.emirdev.echologic.utils.Utils;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;

public class SudoCommand extends PluginCommand {

    public enum SudoMode {
        SERVER,
        PROXY
    }

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("sudo")
                .requires(hasPermission("echologic.sudo"))
                .then(BrigadierCommand.literalArgumentBuilder("server")
                        .requires(hasPermission("echologic.sudo.server"))
                        .then(requiredCustomArgumentBuilder("player", new ProxyPlayerArgumentType())
                                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                                        .executes(this::server))))
                .then(BrigadierCommand.literalArgumentBuilder("proxy")
                        .requires(hasPermission("echologic.sudo.proxy"))
                        .then(requiredCustomArgumentBuilder("player", new ProxyPlayerArgumentType())
                                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                                        .executes(this::proxy))))
                .build();
    }

    public int server(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ProxyPlayer player = getCustomArgument(ctx, "player", ProxyPlayerArgumentType.class);
        String message = StringArgumentType.getString(ctx, "message");

        Utils.sendMessage(ctx.getSource(),
                "<#00eeee>Sent <#00ccff><message></#00ccff> as <#00ccff><player></#00ccff> in <#00ccff><server></#00ccff>.",
                Placeholder.unparsed("message", message),
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("server", player.getServer().getName()));

        EchoLogic.getProxyUtils().sudoPlayer(SudoMode.SERVER, player, message);
        return 1;
    }

    public int proxy(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ProxyPlayer player = getCustomArgument(ctx, "player", ProxyPlayerArgumentType.class);
        String message = StringArgumentType.getString(ctx, "message");

        Utils.sendMessage(ctx.getSource(),
                "<#00eeee>Sent <#00ccff><message></#00ccff> as <#00ccff><player></#00ccff> in proxy.",
                Placeholder.unparsed("message", message),
                Placeholder.unparsed("player", player.getName()));

        EchoLogic.getProxyUtils().sudoPlayer(SudoMode.PROXY, player, message);
        return 1;
    }
}
