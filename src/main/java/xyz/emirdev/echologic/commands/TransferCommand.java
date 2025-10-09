package xyz.emirdev.echologic.commands;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.InetAddressArgumentType;
import xyz.emirdev.echologic.arguments.ProxyPlayerArgumentType;
import xyz.emirdev.echologic.utils.Utils;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;

public class TransferCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("transfer")
                .requires(hasPermission("echologic.transfer"))
                .then(requiredCustomArgumentBuilder("player", new ProxyPlayerArgumentType())
                        .then(requiredCustomArgumentBuilder("address", new InetAddressArgumentType())
                                .executes(this::noPort)
                                .then(BrigadierCommand.requiredArgumentBuilder("port", IntegerArgumentType.integer(0, 65535))
                                        .executes(this::port))))
                .build();
    }

    public int noPort(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        return execute(ctx, 25565);
    }

    public int port(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        int port = IntegerArgumentType.getInteger(ctx, "port");
        return execute(ctx, port);
    }

    public int execute(CommandContext<CommandSource> ctx, int port) throws CommandSyntaxException {
        ProxyPlayer player = getCustomArgument(ctx, "player", ProxyPlayerArgumentType.class);
        InetAddress address = getCustomArgument(ctx, "address", InetAddressArgumentType.class);
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);

        Utils.sendMessage(ctx.getSource(),
                "<#00eeee>Transferring <#00ccff><player><#00eeee> to <#00ccff><ip><#00eeee>...",
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("ip", address.getHostName() + ":" + port));

        EchoLogic.getProxyUtils().transferPlayer(player.getUniqueId(), socketAddress);
        return 1;
    }
}
