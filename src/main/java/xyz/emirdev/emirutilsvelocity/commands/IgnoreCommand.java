package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.proxy.ProxyPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.List;
import java.util.UUID;

@Command("ignore")
@CommandPermission("emirutilsvelocity.message")
public class IgnoreCommand {

    @Subcommand("add")
    public void add(Player player, ProxyPlayer target) {
        if (EmirUtilsVelocity.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId())) {
            Utils.sendError(player,
                    "You already have {0} ignored.",
                    target.getName()
            );
            return;
        }

        EmirUtilsVelocity.getDatabase().ignorePlayer(player.getUniqueId(), target.getUniqueId());
        Utils.sendMessage(player,
                "<red>{0} can <bold>no longer</bold> message you</red>",
                target.getName()
        );
    }

    @Subcommand("remove")
    public void remove(Player player, ProxyPlayer target) {
        if (!EmirUtilsVelocity.getDatabase().isIgnored(player.getUniqueId(), target.getUniqueId())) {
            Utils.sendError(player,
                    "You do not have {0} ignored.",
                    target.getName()
            );
            return;
        }

        EmirUtilsVelocity.getDatabase().unIgnorePlayer(player.getUniqueId(), target.getUniqueId());
        Utils.sendMessage(player,
                "<green>{0} can <bold>now</bold> message you</green>",
                target.getName()
        );
    }

    @Subcommand("list")
    public void list(Player player) {
        List<UUID> ignoredPlayers = EmirUtilsVelocity.getDatabase().getIgnoredPlayers(player.getUniqueId());

        if (ignoredPlayers.isEmpty()) {
            Utils.sendError(player, "You do not have anyone ignored.");
            return;
        }

        List<String> names = ignoredPlayers.stream().map(uuid ->
                EmirUtilsVelocity.hasRedisBungee() ?
                        RedisBungeeAPI.getRedisBungeeApi().getNameFromUuid(uuid) :
                        //TODO: SOMEHOW MAKE THIS SHOW PLAYER NAME INSTEAD OF UUID IDFK HOW BUT DO IT
                        uuid.toString()
        ).toList();

        Utils.sendMessage(player,
                "<#00eeee><bold>Ignored Players</bold></#00eeee> <#00ccff>({0})</#00ccff><#00eeee>:</#00eeee>",
                names.size()
        );

        for (String name : names) {
            Utils.sendMessage(player,
                    "  <#00eeee>{0}</#00eeee>",
                    name
            );
        }
    }
}