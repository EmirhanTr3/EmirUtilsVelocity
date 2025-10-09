package xyz.emirdev.echologic.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.utils.LuckPermsUtils;
import xyz.emirdev.echologic.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffChatCommand extends PluginCommand {
    public static List<UUID> toggledPlayers = new ArrayList<>();

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("staffchat")
                .requires(hasPermission("echologic.staffchat"))
                .executes(this::toggle)
                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                        .executes(this::execute))
                .build();
    }

    @Override
    public List<String> getAliases() {
        return List.of("sc");
    }

    public int toggle(CommandContext<CommandSource> ctx) {
        if (ctx.getSource() instanceof Player player) {
            if (!toggledPlayers.contains(player.getUniqueId())) {
                toggledPlayers.add(player.getUniqueId());
                Utils.sendMessage(player, "<green>You are <bold>now</bold> chatting in staff chat.");
            } else {
                toggledPlayers.remove(player.getUniqueId());
                Utils.sendMessage(player, "<green>You are <bold>no longer</bold> chatting in staff chat.");
            }
        } else {
            Utils.sendError(ctx.getSource(), "You cannot toggle staff chat as console.");
        }

        return 1;
    }

    public int execute(CommandContext<CommandSource> ctx) {
        String message = StringArgumentType.getString(ctx, "message");
        if (ctx.getSource() instanceof Player player) {
            sendStaffChatMessage(player, message);
        } else {
            sendStaffChatMessage(message);
        }

        return 1;
    }

    public static void sendStaffChatMessage(Player player, String message) {
        LuckPermsUtils.getDisplayName(player).thenAcceptAsync(displayname -> {
            EchoLogic.getProxyUtils().broadcastWithPermission(
                    "echologic.staffchat",
                    EchoLogic.hasRedisBungee()
                            ? "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua><proxy><dark_aqua>] <dark_aqua>[<aqua><server><dark_aqua>] <aqua><displayname><aqua>: <message>"
                            : "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua><server><dark_aqua>] <aqua><displayname><aqua>: <message>",
                    Placeholder.unparsed("proxy",
                            EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId()
                                    : "null"),
                    EchoLogic.hasRedisBungee()
                            ? Placeholder.unparsed("server",
                                    RedisBungeeAPI.getRedisBungeeApi().getServerFor(player.getUniqueId()).getName())
                            : Placeholder.unparsed("server", player.getCurrentServer().get().getServerInfo().getName()),
                    Placeholder.component("displayname", displayname),
                    Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
        });
    }

    public static void sendStaffChatMessage(String message) {
        EchoLogic.getProxyUtils().broadcastWithPermission(
                "echologic.staffchat",
                EchoLogic.hasRedisBungee()
                        ? "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua><proxy><dark_aqua>] <aqua>Console<aqua>: <message>"
                        : "<dark_aqua>[<aqua>SC<dark_aqua>] <aqua>Console<aqua>: <message>",
                Placeholder.unparsed("proxy",
                        EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId() : "null"),
                Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
    }
}
