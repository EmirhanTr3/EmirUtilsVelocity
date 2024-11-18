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
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;

public final class RedisPlayerParameterType implements ParameterType<VelocityCommandActor, RedisPlayer> {

    @Override
    public RedisPlayer parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String name = input.readString();

        RedisPlayer player = new RedisPlayer(name);

        if (!player.isOnline()) throw new EUVCommandException(
                "<red>Invalid player:</red> <yellow>%s</yellow>",
                name
        );

        return player;
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull VelocityCommandActor> defaultSuggestions() {
        RedisBungeeAPI redisBungee = EmirUtilsVelocity.getRedisBungee();
        return (context) -> redisBungee.getPlayersOnline().stream().map(redisBungee::getNameFromUuid).toList();
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}
