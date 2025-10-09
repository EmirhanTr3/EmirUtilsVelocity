package xyz.emirdev.echologic.commands;

import java.util.List;
import java.util.Objects;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.ProxyPlayerArgumentType;
import xyz.emirdev.echologic.utils.Utils;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;

public class FindCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("find")
                .requires(hasPermission("echologic.find"))
                .then(requiredCustomArgumentBuilder("player", new ProxyPlayerArgumentType())
                        .executes(this::execute))
                .build();
    }

    @Override
    public List<String> getAliases() {
        return List.of("sfind");
    }

    public int execute(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ProxyPlayer player = getCustomArgument(ctx, "player", ProxyPlayerArgumentType.class);

        Utils.sendMessage(ctx.getSource(),
                EchoLogic.hasRedisBungee()
                        ? "<#00ccff><player> <#00eeee>is found in <#00ccff><server> <#00eeee>from proxy <#00ccff><proxy>"
                        : "<#00ccff><player> <#00eeee>is found in <#00ccff><server>",
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("server", player.getServer().getName()),
                Placeholder.unparsed("proxy", Objects.requireNonNullElse(player.getProxy(), "")));
        return 1;
    }
}
