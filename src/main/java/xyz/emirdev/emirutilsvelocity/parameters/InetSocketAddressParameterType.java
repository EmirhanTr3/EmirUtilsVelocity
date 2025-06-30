package xyz.emirdev.emirutilsvelocity.parameters;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

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
    public InetSocketAddress parse(@NotNull MutableStringStream input,
            @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String ip = input.readString();

        if (!ip.matches(
                "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}")) {
            throw new EUVCommandException(
                    "<red>Invalid IP address:</red> <yellow><ip></yellow>",
                    Placeholder.unparsed("ip", ip));
        }

        return new InetSocketAddress(ip, 0);
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull VelocityCommandActor> defaultSuggestions() {
        return (context) -> {
            if (EmirUtilsVelocity.hasRedisBungee()) {
                RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();
                return redisBungee.getPlayersOnline().stream()
                        .map(uuid -> redisBungee.getPlayerIp(uuid).getHostAddress()).toList();
            } else {
                return EmirUtilsVelocity.getProxy().getAllPlayers().stream()
                        .map(player -> player.getRemoteAddress().getHostName()).toList();
            }
        };
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}
