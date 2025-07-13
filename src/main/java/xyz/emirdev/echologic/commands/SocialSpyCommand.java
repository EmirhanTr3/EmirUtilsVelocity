package xyz.emirdev.echologic.commands;

import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.database.Database;
import xyz.emirdev.echologic.utils.Utils;

public class SocialSpyCommand {

    @Command("socialspy")
    @CommandPermission("echologic.socialspy")
    public void socialspy(Player player) {
        Database.PlayerData playerData = EchoLogic.getDatabase().getPlayerData(player.getUniqueId());

        if (!playerData.hasSocialSpy()) {
            EchoLogic.getDatabase().updateSocialSpy(playerData.getUniqueId(), true);
            Utils.sendMessage(player, "<green>You can <bold>now</bold> see other's messages.</green>");
        } else {
            EchoLogic.getDatabase().updateSocialSpy(playerData.getUniqueId(), false);
            Utils.sendMessage(player, "<red>You can <bold>no longer</bold> see other's messages.</red>");
        }
    }
}
