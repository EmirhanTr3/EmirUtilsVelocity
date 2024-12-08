package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.io.IOException;

@Command("emirutilsvelocity")
@CommandPermission("emirutilsvelocity.command")
public class MainCommand {

    @Subcommand("reload")
    @CommandPermission("emirutilsvelocity.command.reload")
    public void reload(CommandSource sender) {
        long time = System.currentTimeMillis();

        try {
            EmirUtilsVelocity.getConfig().reload();
        } catch (IOException e) {
            e.printStackTrace();
            Utils.sendError(sender, "There was an error reloading configuration. Check console for more information.");
            return;
        }

        Utils.sendMessage(sender,
                "<#00eeee>Reloaded configuration in <#00ccff>{0}<#00eeee>ms.",
                System.currentTimeMillis() - time
        );
    }
}
