package xyz.emirdev.echologic.arguments;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.ELCommandException;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;

import java.util.concurrent.CompletableFuture;

public class ProxyPlayerArgumentType implements CustomArgumentType<ProxyPlayer, String> {

    public ProxyPlayer convert(String nativeType) throws CommandSyntaxException {
        ProxyPlayer player = new ProxyPlayer(nativeType);

        if (!player.isOnline())
            throw new ELCommandException(
                    "<red>Invalid player:</red> <yellow><name></yellow>",
                    Placeholder.unparsed("name", nativeType)).create();

        return player;
    }

    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (EchoLogic.hasRedisBungee()) {
            RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();
            redisBungee.getPlayersOnline().stream().map(redisBungee::getNameFromUuid).forEach(builder::suggest);
        } else {
            EchoLogic.getProxy().getAllPlayers().stream().map(Player::getUsername).forEach(builder::suggest);
        }
        return builder.buildFuture();
    }
}
