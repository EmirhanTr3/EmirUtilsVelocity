package xyz.emirdev.echologic.parameters;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.echologic.ELCommandException;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;

public final class ProxyPlayerParameterType implements ParameterType<VelocityCommandActor, ProxyPlayer> {

    @Override
    public ProxyPlayer parse(@NotNull MutableStringStream input,
            @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String name = input.readString();

        ProxyPlayer player = new ProxyPlayer(name);

        if (!player.isOnline())
            throw new ELCommandException(
                    "<red>Invalid player:</red> <yellow><name></yellow>",
                    Placeholder.unparsed("name", name));

        return player;
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull VelocityCommandActor> defaultSuggestions() {
        return (context) -> {
            if (EchoLogic.hasRedisBungee()) {
                RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();
                return redisBungee.getPlayersOnline().stream().map(redisBungee::getNameFromUuid).toList();
            } else {
                return EchoLogic.getProxy().getAllPlayers().stream().map(Player::getUsername).toList();
            }
        };
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}
