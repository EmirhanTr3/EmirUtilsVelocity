package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.database.Database;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class SocialSpyCommand {

    @Command("socialspy")
    @CommandPermission("emirutilsvelocity.socialspy")
    public void socialspy(Player player) {
        Database.PlayerData playerData = EmirUtilsVelocity.getDatabase().getPlayerData(player.getUniqueId());

        if (!playerData.hasSocialSpy()) {
            EmirUtilsVelocity.getDatabase().updateSocialSpy(playerData.getUniqueId(), true);
            Utils.sendMessage(player, "<green>You can <bold>now</bold> see other's messages.</green>");
        } else {
            EmirUtilsVelocity.getDatabase().updateSocialSpy(playerData.getUniqueId(), false);
            Utils.sendMessage(player, "<red>You can <bold>no longer</bold> see other's messages.</red>");
        }
    }
}