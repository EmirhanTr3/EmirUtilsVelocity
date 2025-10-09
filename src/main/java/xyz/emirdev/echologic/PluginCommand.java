package xyz.emirdev.echologic;

import java.util.List;
import java.util.function.Predicate;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.SneakyThrows;
import xyz.emirdev.echologic.arguments.CustomArgumentType;

public abstract class PluginCommand {
    public abstract LiteralCommandNode<CommandSource> getCommand();

    public List<String> getAliases() {
        return List.of();
    }

    public Predicate<CommandSource> hasPermission(String permission) {
        return ctx -> ctx.hasPermission(permission);
    }

    public <N, T extends CustomArgumentType<?, N>> RequiredArgumentBuilder<CommandSource, N> requiredCustomArgumentBuilder(String name, T argument) {
        return BrigadierCommand.requiredArgumentBuilder(name, argument.getNativeType())
                .suggests(argument::listSuggestions);
    }

    @SneakyThrows
    public <R, N, T extends CustomArgumentType<R, N>> R getCustomArgument(CommandContext<CommandSource> ctx, String name, Class<T> clazz) {
        T argument = clazz.getConstructor().newInstance();
        return argument.convert((N) ctx.getArgument(name, argument.getNativeType().getClass().getTypeName().getClass()));
    }

    public Player getContextPlayer(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        if (ctx.getSource() instanceof Player player)
            return player;

        throw new ELCommandException("<red>You cannot run this command as console.</red>").create();
    }

//    public Player getPlayer(CommandContext<CommandSource> ctx, String name) throws CommandSyntaxException {
//        Player player = ctx.getArgument(name, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
//        if (player == null) {
//            ctx.getSource().getSender().sendRichMessage("<red>No player was found</red>");
//            return null;
//        }
//        return player;
//    }
//
//    public List<Player> getPlayers(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
//        List<Player> players = ctx.getArgument(name, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
//        if (players == null || players.isEmpty()) {
//            ctx.getSource().getSender().sendRichMessage("<red>No player was found</red>");
//            return null;
//        }
//        return players;
//    }
//
//    public Duration getDuration(CommandContext<CommandSourceStack> ctx, String name) {
//        String durationString = StringArgumentType.getString(ctx, name);
//        Duration duration = TimeUtils.convertStringToDuration(durationString);
//
//        if (duration == null) {
//            ctx.getSource().getSender().sendRichMessage("<red>Invalid duration provided</red>");
//            return null;
//        }
//
//        return duration;
//    }
}