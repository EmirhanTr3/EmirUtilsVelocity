package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;

import java.util.List;

public class AnnouncementCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("announcement")
                .requires(hasPermission("echologic.announcement"))
                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                        .executes(this::execute))
                .build();
    }

    @Override
    public List<String> getAliases() {
        return List.of("announce");
    }

    @SneakyThrows
    public int execute(CommandContext<CommandSource> ctx) {
        String message = StringArgumentType.getString(ctx, "message");

        EchoLogic.getProxyUtils().broadcast(
            String.join("\n", EchoLogic.getConfig().getRoot().node("announcement", "format").getList(String.class)),
            Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));

        return 1;
    }

}
