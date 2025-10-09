package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.ProxyPlayerArgumentType;
import xyz.emirdev.echologic.arguments.RegisteredServerArgumentType;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.Utils;

public class SendCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("send")
                .requires(hasPermission("echologic.send"))
                .then(BrigadierCommand.literalArgumentBuilder("all")
                        .then(requiredCustomArgumentBuilder("server", new RegisteredServerArgumentType())
                                .executes(this::all)))
                .then(BrigadierCommand.literalArgumentBuilder("player")
                        .then(requiredCustomArgumentBuilder("player", new ProxyPlayerArgumentType())
                                .then(requiredCustomArgumentBuilder("server", new RegisteredServerArgumentType())
                                        .executes(this::player))))
                .then(BrigadierCommand.literalArgumentBuilder("server")
                        .then(requiredCustomArgumentBuilder("server", new RegisteredServerArgumentType())
                                .then(requiredCustomArgumentBuilder("targetServer", new RegisteredServerArgumentType())
                                        .executes(this::server))))
                .build();
    }

    public int all(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        RegisteredServer server = getCustomArgument(ctx, "server", RegisteredServerArgumentType.class);

        Utils.sendMessage(ctx.getSource(),
                "<#00eeee>Connecting all players to server <#00ccff><server><#00eeee>...",
                Placeholder.unparsed("server", server.getServerInfo().getName()));

        EchoLogic.getProxyUtils().connectAllPlayers(server.getServerInfo().getName());
        return 1;
    }

    public int player(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ProxyPlayer player = getCustomArgument(ctx, "player", ProxyPlayerArgumentType.class);
        RegisteredServer server = getCustomArgument(ctx, "server", RegisteredServerArgumentType.class);

        Utils.sendMessage(ctx.getSource(),
                "<#00eeee>Connecting <#00ccff><player><#00eeee> to server <#00ccff><server><#00eeee>...",
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("server", server.getServerInfo().getName()));

        EchoLogic.getProxyUtils().connectPlayer(player.getUniqueId(), server.getServerInfo().getName());
        return 1;
    }

    public int server(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        RegisteredServer server = getCustomArgument(ctx, "server", RegisteredServerArgumentType.class);
        RegisteredServer targetServer = getCustomArgument(ctx, "targetServer", RegisteredServerArgumentType.class);

        Utils.sendMessage(ctx.getSource(),
                "<#00eeee>Connecting all players in server <#00ccff><server> <#00eeee>to server <#00ccff><targetserver><#00eeee>...",
                Placeholder.unparsed("server", server.getServerInfo().getName()),
                Placeholder.unparsed("targetserver", targetServer.getServerInfo().getName()));

        EchoLogic.getProxyUtils().connectAllPlayersInServer(
                server.getServerInfo().getName(),
                targetServer.getServerInfo().getName());

        return 1;
    }
}
