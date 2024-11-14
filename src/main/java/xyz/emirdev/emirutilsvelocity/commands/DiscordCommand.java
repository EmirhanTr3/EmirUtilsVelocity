package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.proxy.Player;
import revxrsal.commands.annotation.Command;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class DiscordCommand {

    @Command("discord")
    public void discord(Player player) {
        String invite = EmirUtilsVelocity.getConfig().getDiscordInvite();
        String message = EmirUtilsVelocity.getConfig().getDiscordMessage();

        Utils.sendMessage(player, message, invite);
    }
}