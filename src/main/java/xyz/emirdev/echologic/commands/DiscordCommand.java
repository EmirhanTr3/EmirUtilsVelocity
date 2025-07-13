package xyz.emirdev.echologic.commands;

import java.util.Objects;

import com.velocitypowered.api.command.CommandSource;
import revxrsal.commands.annotation.Command;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.Utils;

public class DiscordCommand {

    @Command("discord")
    public void discord(CommandSource sender) {
        String invite = EchoLogic.getConfig().getDiscordInvite();
        String message = EchoLogic.getConfig().getDiscordMessage();

        Utils.sendMessage(sender, message.replaceAll("<invite>", Objects.requireNonNullElse(invite, "null")));
    }
}
