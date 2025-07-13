package xyz.emirdev.echologic;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.SendableException;
import xyz.emirdev.echologic.utils.Utils;

public class ELCommandException extends SendableException {
    public TagResolver[] resolvers;

    public ELCommandException(String message, TagResolver... resolvers) {
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
