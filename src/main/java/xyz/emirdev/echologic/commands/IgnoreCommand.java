package xyz.emirdev.echologic.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.utils.Utils;

import java.util.List;
import java.util.UUID;

@Command("ignore")
@CommandPermission("echologic.message")
public class IgnoreCommand {

    @Subcommand("add")
    public void add(Player player, ProxyPlayer target) {
        if (EchoLogic.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId())) {
            Utils.sendError(player,
                    "You already have <target> ignored.",
                    Placeholder.unparsed("target", target.getName()));
            return;
        }

        EchoLogic.getDatabase().ignorePlayer(player.getUniqueId(), target.getUniqueId());
        Utils.sendMessage(player,
                "<red><target> can <bold>no longer</bold> message you</red>",
                Placeholder.unparsed("target", target.getName()));
    }

    @Subcommand("remove")
    public void remove(Player player, ProxyPlayer target) {
        if (!EchoLogic.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId())) {
            Utils.sendError(player,
                    "You do not have <target> ignored.",
                    Placeholder.unparsed("target", target.getName()));
            return;
        }

        EchoLogic.getDatabase().unIgnorePlayer(player.getUniqueId(), target.getUniqueId());
        Utils.sendMessage(player,
                "<green><target> can <bold>now</bold> message you</green>",
                Placeholder.unparsed("target", target.getName()));
    }

    @Subcommand("list")
    public void list(Player player) {
        List<UUID> ignoredPlayers = EchoLogic.getDatabase().getIgnoredPlayers(player.getUniqueId());

        if (ignoredPlayers.isEmpty()) {
            Utils.sendError(player, "You do not have anyone ignored.");
            return;
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
    }
}
