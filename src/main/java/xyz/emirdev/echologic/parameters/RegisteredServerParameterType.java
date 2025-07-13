package xyz.emirdev.echologic.parameters;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.echologic.ELCommandException;
import xyz.emirdev.echologic.EchoLogic;

import java.util.Optional;

public final class RegisteredServerParameterType implements ParameterType<VelocityCommandActor, RegisteredServer> {

    @Override
    public RegisteredServer parse(@NotNull MutableStringStream input,
            @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String name = input.readString();

        Optional<RegisteredServer> server = EchoLogic.getProxy().getServer(name);

        if (server.isEmpty())
            throw new ELCommandException(
                    "<red>Invalid server:</red> <yellow><name></yellow>",
                    Placeholder.unparsed("name", name));

        return server.get();
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull VelocityCommandActor> defaultSuggestions() {
        return (context) -> EchoLogic.getProxy().getAllServers().stream()
                .map(server -> server.getServerInfo().getName()).toList();
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}
