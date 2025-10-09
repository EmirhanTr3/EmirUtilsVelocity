package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.utils.Utils;

import java.util.Objects;
import java.util.Optional;

public class HubCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("hub")
                .requires(sender -> !Objects.equals(EchoLogic.getConfig().getRoot().node("hub").getString(), "none"))
                .executes(this::execute)
                .build();
    }

    public int execute(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);

        Optional<RegisteredServer> server = EchoLogic.getProxy().getServer(
                EchoLogic.getConfig().getRoot().node("hub").getString()
        );
        if (server.isEmpty()) {
            Utils.sendError(player, "Invalid hub server defined in config.");
            return 1;
        }

        Utils.connectPlayer(player, server.get());
        return 1;
    }
}
