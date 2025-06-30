package xyz.emirdev.emirutilsvelocity;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.SendableException;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

public class EUVCommandException extends SendableException {
    public TagResolver[] resolvers;

    public EUVCommandException(String message, TagResolver... resolvers) {
        super(message);
        this.resolvers = resolvers;
    }

    @Override
    public void sendTo(@NotNull CommandActor actor) {
        if (!this.getMessage().isEmpty()) {
            actor.error(Utils.convertComponentToLegacyString(Utils.formatMessage(getMessage(), resolvers)));
        }
    }
}
