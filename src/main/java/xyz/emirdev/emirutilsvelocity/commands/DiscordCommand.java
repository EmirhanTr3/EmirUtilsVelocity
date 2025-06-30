package xyz.emirdev.emirutilsvelocity.commands;

import java.util.Objects;

import com.velocitypowered.api.command.CommandSource;
import revxrsal.commands.annotation.Command;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class DiscordCommand {

    @Command("discord")
    public void discord(CommandSource sender) {
        String invite = EmirUtilsVelocity.getConfig().getDiscordInvite();
        String message = EmirUtilsVelocity.getConfig().getDiscordMessage();

        Utils.sendMessage(sender, message.replaceAll("<invite>", Objects.requireNonNullElse(invite, "null")));
    }
}
