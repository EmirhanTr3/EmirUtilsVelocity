package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.Utils;
import xyz.emirdev.emirutilsvelocity.servermanager.Server;
import xyz.emirdev.emirutilsvelocity.servermanager.ServerType;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class ServerManagerCommand {
    // these are useless since its registered manually
    public static String name = "servermanager";
    public static List<String> aliases = List.of("sm");
    public static boolean autoLoad = false;

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("servermanager")
                .requires(source -> source.hasPermission("emirutilsvelocity.servermanager"))
                .executes(context -> {
                    context.getSource().sendMessage(Utils.deserialize("<red>You need to a subcommand."));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.literalArgumentBuilder("create")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                        .then(BrigadierCommand.requiredArgumentBuilder("port", IntegerArgumentType.integer(0, 65353))
                        .then(BrigadierCommand.requiredArgumentBuilder("jar", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    File jars = new File("plugins/emirutilsvelocity/servermanager/jars");
                                    String[] jarList = jars.list((dir, name) -> name.endsWith(".jar"));

                                    Arrays.stream(jarList).forEach(builder::suggest);

                                    return builder.buildFuture();
                                })
                        .then(BrigadierCommand.requiredArgumentBuilder("ram", IntegerArgumentType.integer(0))
                                .suggests((ctx, builder) -> {
                                    builder.suggest("512", VelocityBrigadierMessage.tooltip(Utils.deserialize("512 MB")));
                                    builder.suggest("1024", VelocityBrigadierMessage.tooltip(Utils.deserialize("1 GB")));
                                    builder.suggest("2048", VelocityBrigadierMessage.tooltip(Utils.deserialize("2 GB")));
                                    builder.suggest("4096", VelocityBrigadierMessage.tooltip(Utils.deserialize("4 GB")));
                                    builder.suggest("8192", VelocityBrigadierMessage.tooltip(Utils.deserialize("8 GB")));
                                    builder.suggest("16384", VelocityBrigadierMessage.tooltip(Utils.deserialize("16 GB (should never be needed)")));

                                    return builder.buildFuture();
                                })
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);
                            int port = context.getArgument("port", int.class);
                            String jar = context.getArgument("jar", String.class);
                            int ram = context.getArgument("ram", int.class);

                            context.getSource().sendMessage(Utils.deserialize(
                                    "name: " + name +
                                    "\nport: " + port +
                                    "\njar: " + jar +
                                    "\nram: " + ram
                            ));

                            Server server = EmirUtilsVelocity.getServerManager().createServer(name, port, jar, ram);
                            server.start();

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("flags", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);
                                    int port = context.getArgument("port", int.class);
                                    String jar = context.getArgument("jar", String.class);
                                    int ram = context.getArgument("ram", int.class);
                                    String flags = context.getArgument("flags", String.class);

                                    context.getSource().sendMessage(Utils.deserialize(
                                        "name: " + name +
                                            "\nport: " + port +
                                            "\njar: " + jar +
                                            "\nram: " + ram +
                                            "\nflags: " + flags
                                    ));

                                    Server server = EmirUtilsVelocity.getServerManager().createServer(name, port, jar, ram, flags);
                                    server.start();

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        ))))
                )
                .build();

        return new BrigadierCommand(node);
    }
}