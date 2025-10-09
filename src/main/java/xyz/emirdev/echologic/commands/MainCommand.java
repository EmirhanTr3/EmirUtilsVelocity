package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.utils.Utils;

import java.io.IOException;

public class MainCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("echologic")
                .requires(hasPermission("echologic.command"))
                .then(BrigadierCommand.literalArgumentBuilder("reload")
                        .requires(hasPermission("echologic.command.reload"))
                        .executes(this::reload))
                .build();
    }

    public int reload(CommandContext<CommandSource> ctx) {
        long time = System.currentTimeMillis();

        EchoLogic.getConfig().load();

        Utils.sendMessage(ctx.getSource(),
                "<#00eeee>Reloaded configuration in <#00ccff><time><#00eeee>ms.",
                Placeholder.unparsed("time", String.valueOf(System.currentTimeMillis() - time)));

        return 1;
    }
}
