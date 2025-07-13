package xyz.emirdev.echologic.parameters;

import org.jetbrains.annotations.NotNull;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.echologic.ELCommandException;
import xyz.emirdev.echologic.EchoLogic;

public class PlayerIPAddressParameterType implements ParameterType<VelocityCommandActor, PlayerIPAddress> {

    @Override
    public PlayerIPAddress parse(@NotNull MutableStringStream input,
            @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String ip = input.readString();

        if (!ip.matches(
                "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}")) {
            throw new ELCommandException(
                    "<red>Invalid IP address:</red> <yellow><ip></yellow>",
                    Placeholder.unparsed("ip", ip));
        }

        return new PlayerIPAddress(ip, 0);
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull VelocityCommandActor> defaultSuggestions() {
        return (context) -> {
            if (EchoLogic.hasRedisBungee()) {
                RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();
                return redisBungee.getPlayersOnline().stream()
                        .map(uuid -> redisBungee.getPlayerIp(uuid).getHostAddress()).toList();
            } else {
                return EchoLogic.getProxy().getAllPlayers().stream()
                        .map(player -> player.getRemoteAddress().getHostName()).toList();
            }
        };
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}
