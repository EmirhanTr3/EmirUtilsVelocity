package xyz.emirdev.emirutilsvelocity;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.SendableException;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class EUVCommandException extends SendableException {
    public Object[] args;

    public EUVCommandException(String message, Object... args) {
        super(message);
        this.args = args;
    }

    public void sendTo(@NotNull CommandActor actor) {
        if (!this.getMessage().isEmpty()) {
            actor.error(Utils.convertComponentToLegacyString(
                    Utils.format(getMessage(), args)
            ));
        }
    }
}