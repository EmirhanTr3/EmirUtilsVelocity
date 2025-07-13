package xyz.emirdev.echologic.commands;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.Utils;

import java.io.IOException;

@Command("echologic")
@CommandPermission("echologic.command")
public class MainCommand {

    @Subcommand("reload")
    @CommandPermission("echologic.command.reload")
    public void reload(CommandSource sender) {
        long time = System.currentTimeMillis();

        try {
            EchoLogic.getConfig().reload();
            EchoLogic.loadCommands();
        } catch (IOException e) {
            e.printStackTrace();
            Utils.sendError(sender, "There was an error reloading configuration. Check console for more information.");
            return;
        }

        Utils.sendMessage(sender,
                "<#00eeee>Reloaded configuration in <#00ccff><time><#00eeee>ms.",
                Placeholder.unparsed("time", String.valueOf(System.currentTimeMillis() - time)));
    }
}
