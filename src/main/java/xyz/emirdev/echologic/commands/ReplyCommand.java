package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.Utils;

import java.util.List;

public class ReplyCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("reply")
                .requires(hasPermission("echologic.message"))
                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                        .executes(this::execute))
                .build();
    }

    @Override
    public List<String> getAliases() {
        return List.of("r", "ereply", "er", "sr");
    }

    public int execute(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        String message = StringArgumentType.getString(ctx, "message");

        if (!MessageCommand.lastMessagedPlayer.containsKey(player.getUniqueId())) {
            Utils.sendError(player, "You did not message anyone.");
            return 1;
        }

        ProxyPlayer target = new ProxyPlayer(MessageCommand.lastMessagedPlayer.get(player.getUniqueId()));
        if (!target.isOnline()) {
            Utils.sendError(player,
                    "<target> is not online.",
                    Placeholder.unparsed("target", target.getName()));
            return 1;
        }

        MessageCommand.sendMessage(player, target, message);
        return 1;
    }
}
