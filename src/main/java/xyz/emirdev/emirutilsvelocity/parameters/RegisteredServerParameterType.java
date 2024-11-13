package xyz.emirdev.emirutilsvelocity.parameters;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import revxrsal.commands.velocity.exception.InvalidPlayerException;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.Optional;

public final class RegisteredServerParameterType implements ParameterType<VelocityCommandActor, RegisteredServer> {

    @Override
    public RegisteredServer parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String name = input.readString();

        Optional<RegisteredServer> server = EmirUtilsVelocity.getProxy().getServer(name);

        if (server.isEmpty()) throw new CommandErrorException(
                Utils.convertComponentToLegacyString(Utils.format(
                        "<red>Invalid server:</red> <yellow>%s</yellow>",
                        name
                ))
        );

        return server.get();
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull VelocityCommandActor> defaultSuggestions() {
        return (context) -> EmirUtilsVelocity.getProxy().getAllServers().stream().map(server -> server.getServerInfo().getName()).toList();
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}
