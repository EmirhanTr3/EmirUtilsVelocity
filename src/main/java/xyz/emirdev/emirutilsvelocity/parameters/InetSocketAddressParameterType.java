package xyz.emirdev.emirutilsvelocity.parameters;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.emirutilsvelocity.EUVCommandException;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.net.InetSocketAddress;

public final class InetSocketAddressParameterType implements ParameterType<VelocityCommandActor, InetSocketAddress> {

    @Override
    public InetSocketAddress parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String name = input.readString();

        if (!name.matches("(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}")) {
            throw new EUVCommandException(
                    "<red>Invalid IP address:</red> <yellow>{0}</yellow>",
                    name
            );
        }

        return new InetSocketAddress(name, 0);
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull VelocityCommandActor> defaultSuggestions() {
        RedisBungeeAPI redisBungee = EmirUtilsVelocity.getRedisBungee();
        return (context) -> redisBungee.getPlayersOnline().stream().map(uuid -> redisBungee.getPlayerIp(uuid).getHostAddress()).toList();
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}
