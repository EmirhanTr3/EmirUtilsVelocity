package xyz.emirdev.emirutilsvelocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.Utils;

public final class ReloadCommand extends xyz.emirdev.emirutilsvelocity.Command {
    public static String name = "euvreload";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("euvreload")
            .requires(source -> source.hasPermission("emirutilsvelocity.reload"))
            .executes(context -> {
                EmirUtilsVelocity.reloadConfig();
                context.getSource().sendMessage(Utils.deserialize("<green>The configuration has been reloaded.</green>"));

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(node);
    }
}