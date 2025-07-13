package xyz.emirdev.echologic.commands;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;

public class AnnouncementCommand {

    @Command({ "announcement", "announce" })
    @CommandPermission("echologic.announcement")
    public void discord(CommandSource sender, String message) {
        EchoLogic.getProxyUtils().broadcast(
            EchoLogic.getConfig().getAnnouncementFormat(),
            Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
    }
}
