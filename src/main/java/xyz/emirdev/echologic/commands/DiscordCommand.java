package xyz.emirdev.echologic.commands;

import java.util.Objects;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.utils.Utils;

public class DiscordCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("discord")
                .requires(ctx -> !Objects.equals(EchoLogic.getConfig().getRoot().node("discord", "invite").getString(), "none"))
                .executes(this::execute)
                .build();
    }

    public int execute(CommandContext<CommandSource> ctx) {
        String invite = EchoLogic.getConfig().getRoot().node("discord", "invite").getString();
        String message = EchoLogic.getConfig().getRoot().node("discord", "message").getString();

        Utils.sendMessage(ctx.getSource(), message.replaceAll("<invite>", Objects.requireNonNullElse(invite, "null")));
        return 1;
    }
}
