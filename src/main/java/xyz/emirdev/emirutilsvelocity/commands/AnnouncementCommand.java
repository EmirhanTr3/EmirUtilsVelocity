package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

public class AnnouncementCommand {

    @Command({ "announcement", "announce" })
    @CommandPermission("emirutilsvelocity.announcement")
    public void discord(CommandSource sender, String message) {
        EmirUtilsVelocity.getProxyUtils().broadcast(
            EmirUtilsVelocity.getConfig().getAnnouncementFormat(),
            Placeholder.component("message", MiniMessage.miniMessage().deserialize(message)));
    }
}
