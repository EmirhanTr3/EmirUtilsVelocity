package xyz.emirdev.echologic.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
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

import java.util.List;
import java.util.UUID;

public class IgnoreCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("ignore")
                .requires(hasPermission("echologic.message"))
                .then(BrigadierCommand.literalArgumentBuilder("add")
                        .then(requiredCustomArgumentBuilder("target", new ProxyPlayerArgumentType())
                                .executes(this::add)))
                .then(BrigadierCommand.literalArgumentBuilder("remove")
                        .then(requiredCustomArgumentBuilder("target", new ProxyPlayerArgumentType())
                                .executes(this::remove)))
                .then(BrigadierCommand.literalArgumentBuilder("list")
                        .executes(this::list))
                .build();
    }

    public int add(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        ProxyPlayer target = getCustomArgument(ctx, "target", ProxyPlayerArgumentType.class);

        if (EchoLogic.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId())) {
            Utils.sendError(player,
                    "You already have <target> ignored.",
                    Placeholder.unparsed("target", target.getName()));
            return 1;
        }

        EchoLogic.getDatabase().ignorePlayer(player.getUniqueId(), target.getUniqueId());
        Utils.sendMessage(player,
                "<red><target> can <bold>no longer</bold> message you</red>",
                Placeholder.unparsed("target", target.getName()));

        return 1;
    }

    public int remove(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        ProxyPlayer target = getCustomArgument(ctx, "target", ProxyPlayerArgumentType.class);

        if (!EchoLogic.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId())) {
            Utils.sendError(player,
                    "You do not have <target> ignored.",
                    Placeholder.unparsed("target", target.getName()));
            return 1;
        }

        EchoLogic.getDatabase().unIgnorePlayer(player.getUniqueId(), target.getUniqueId());
        Utils.sendMessage(player,
                "<green><target> can <bold>now</bold> message you</green>",
                Placeholder.unparsed("target", target.getName()));

        return 1;
    }

    public int list(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);

        List<UUID> ignoredPlayers = EchoLogic.getDatabase().getIgnoredPlayers(player.getUniqueId());

        if (ignoredPlayers.isEmpty()) {
            Utils.sendError(player, "You do not have anyone ignored.");
            return 1;
        }

        List<String> names = ignoredPlayers.stream().map(
                uuid -> EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getNameFromUuid(uuid) :
                // TODO: SOMEHOW MAKE THIS SHOW PLAYER NAME INSTEAD OF UUID IDFK HOW BUT DO IT
                        uuid.toString())
                .toList();

        Utils.sendMessage(player,
                "<#00eeee><bold>Ignored Players</bold></#00eeee> <#00ccff>(<size>)</#00ccff><#00eeee>:</#00eeee>",
                Placeholder.unparsed("size", String.valueOf(names.size())));

        for (String name : names) {
            Utils.sendMessage(player,
                    "  <#00eeee><name></#00eeee>",
                    Placeholder.unparsed("name", name));
        }

        return 1;
    }
}
