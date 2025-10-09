package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.ProxyPlayerArgumentType;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageCommand extends PluginCommand {
    public static Map<UUID, UUID> lastMessagedPlayer = new HashMap<>();

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("message")
                .requires(hasPermission("echologic.message"))
                .then(requiredCustomArgumentBuilder("target", new ProxyPlayerArgumentType())
                        .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                                .executes(this::execute)))
                .build();
    }

    @Override
    public List<String> getAliases() {
        return List.of("msg", "m", "tell", "t", "whisper", "w", "emsg", "smsg");
    }

    public int execute(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ProxyPlayer target = getCustomArgument(ctx, "target", ProxyPlayerArgumentType.class);
        String message = StringArgumentType.getString(ctx, "message");

        if (ctx.getSource() instanceof Player player) {
            sendMessage(player, target, message);
        } else {
            sendMessage(target, message);
        }

        return 1;
    }

    private static void _sendMessage(CommandSource sender, ProxyPlayer target, String message) {
        Utils.sendMessage(sender,
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>me <#41BBFF>→ <#2595CC><target><#41BBFF>: <#60CCFF><message>",
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("message", message));
    }

    public static void sendMessage(Player player, ProxyPlayer target, String message) {
        if (EchoLogic.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId()) ||
                EchoLogic.getDatabase().isIgnored(target.getUniqueId(), player.getUniqueId())) {
            Utils.sendError(player,
                    "You can't send a message to <target>.",
                    Placeholder.unparsed("target", target.getName()));
            return;
        }

        _sendMessage(player, target, message);

        target.sendMessage(
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC><player> <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF><message>",
                Placeholder.unparsed("player", player.getUsername()),
                Placeholder.unparsed("message", message));
        EchoLogic.getProxyUtils().sendSocialSpyMessage(player, target, message);

        lastMessagedPlayer.put(player.getUniqueId(), target.getUniqueId());
        lastMessagedPlayer.put(target.getUniqueId(), player.getUniqueId());
    }

    public static void sendMessage(ProxyPlayer target, String message) {
        _sendMessage(EchoLogic.getProxy().getConsoleCommandSource(), target, message);

        target.sendMessage(
                "<#41BBFF>[<#2595CC>MSG<#41BBFF>] <#2595CC>Console <#41BBFF>→ <#2595CC>me<#41BBFF>: <#60CCFF><message>",
                Placeholder.unparsed("message", message));
    }
}
