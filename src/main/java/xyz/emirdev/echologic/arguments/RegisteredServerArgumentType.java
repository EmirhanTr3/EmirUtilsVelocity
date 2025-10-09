package xyz.emirdev.echologic.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.ELCommandException;
import xyz.emirdev.echologic.EchoLogic;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RegisteredServerArgumentType implements CustomArgumentType<RegisteredServer, String> {

    @Override
    public RegisteredServer convert(String nativeType) throws CommandSyntaxException {
        Optional<RegisteredServer> server = EchoLogic.getProxy().getServer(nativeType);

        if (server.isEmpty())
            throw new ELCommandException(
                    "<red>Invalid server:</red> <yellow><name></yellow>",
                    Placeholder.unparsed("name", nativeType)).create();

        return server.get();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        EchoLogic.getProxy().getAllServers()
                .forEach(server -> builder.suggest(server.getServerInfo().getName()));
        return builder.buildFuture();
    }
}
