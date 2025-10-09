package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.RegisteredServerArgumentType;
import xyz.emirdev.echologic.utils.Utils;

public class ServerCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("server")
                .requires(hasPermission("echologic.server"))
                .then(requiredCustomArgumentBuilder("server", new RegisteredServerArgumentType())
                        .executes(this::execute))
                .build();
    }

    public int execute(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        RegisteredServer server = getCustomArgument(ctx, "server", RegisteredServerArgumentType.class);

        if (!player.hasPermission("echologic.server." + server.getServerInfo().getName())) {
            Utils.sendError(player,
                    "You are not allowed to connect to <server>!",
                    Placeholder.unparsed("server", server.getServerInfo().getName()));
            return 1;
        }

        Utils.connectPlayer(player, server);
        return 1;
    }
}
