package xyz.emirdev.emirutilsvelocity.commands;

import com.mojang.brigadier.Command;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ServerManagerCommand extends xyz.emirdev.emirutilsvelocity.Command {
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

                            if (EmirUtilsVelocity.getServerManager().getServer(name) != null || Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name))) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " already exists."));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (EmirUtilsVelocity.proxy.getServer(name).isPresent()) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " already exists in the proxy."));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/jars/" + jar))) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server jar not found: " + jar));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendMessage(Utils.deserialize(
                                    "<gray>Created a new server named <yellow>"+ name +"</yellow>:\n" +
                                    "<gray>- <yellow>Port:</yellow> "+ port +"</gray>\n" +
                                    "<gray>- <yellow>Jar:</yellow> "+ jar +"</gray>\n" +
                                    "<gray>- <yellow>Ram:</yellow> "+ ram +" MB</gray>"
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

                                    if (EmirUtilsVelocity.getServerManager().getServer(name) != null || Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name))) {
                                        context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " already exists."));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (EmirUtilsVelocity.proxy.getServer(name).isPresent()) {
                                        context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " already exists in the proxy."));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (!Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/jars/" + jar))) {
                                        context.getSource().sendMessage(Utils.deserialize("<red>Server jar not found: " + jar));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    context.getSource().sendMessage(Utils.deserialize(
                                            "<gray>Created a new server named <yellow>"+ name +"</yellow>:\n" +
                                            "<gray>- <yellow>Port:</yellow> "+ port +"</gray>\n" +
                                            "<gray>- <yellow>Jar:</yellow> "+ jar +"</gray>\n" +
                                            "<gray>- <yellow>Ram:</yellow> "+ ram +" MB</gray>\n" +
                                            "<gray>- <yellow>Flags:</yellow> "+ flags +"</gray>"
                                    ));

                                    Server server = EmirUtilsVelocity.getServerManager().createServer(name, port, jar, ram, flags);
                                    server.start();

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        ))))
                )
                .then(BrigadierCommand.literalArgumentBuilder("list")
                        .executes(context -> {
                            Map<String, Server> servers = EmirUtilsVelocity.getServerManager().getServers();
                            File serversFolder = new File("plugins/emirutilsvelocity/servermanager/servers");
                            String[] serversInFolder = serversFolder.list(((dir, name) -> dir.isDirectory() && !servers.containsKey(name)));

                            context.getSource().sendMessage(Utils.deserialize("<gray>All servers (<yellow>"+ (servers.size() + serversInFolder.length) +"</yellow>):</gray>"));
                            servers.forEach((name, server) -> {
                                context.getSource().sendMessage(Utils.deserialize(String.format(
                                        switch (server.getStatus()) {
                                            case Online -> "<gray>- <yellow>%s</yellow> (<green>%s</green>)</gray>";
                                            case Offline -> "<gray>- <yellow>%s</yellow> (<red>%s</red>)</gray>";
                                            case Invalid -> "<gray>- <yellow>%s</yellow> (<dark_red>%s</dark_red>)</gray>";
                                        },
                                        name,
                                        server.getStatus()
                                )));
                            });

                            if (serversInFolder.length > 0) {
                                Arrays.stream(serversInFolder).forEach(name -> {
                                    context.getSource().sendMessage(Utils.deserialize(String.format(
                                            "<gray>- <yellow>%s</yellow> (Unloaded)</gray>",
                                            name
                                    )));
                                });
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(BrigadierCommand.literalArgumentBuilder("info")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    EmirUtilsVelocity.getServerManager().getServers().forEach((name1, server) -> builder.suggest(name1));

                                    return builder.buildFuture();
                                })
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);

                            if (EmirUtilsVelocity.getServerManager().getServers().containsKey(name)) {
                                Server server = EmirUtilsVelocity.getServerManager().getServer(name);

                                String status = switch (server.getStatus()) {
                                    case Online -> "<green>Online</green>";
                                    case Offline -> "<red>Offline</red>";
                                    case Invalid -> "<dark_red>Invalid</dark_red>";
                                };

                                context.getSource().sendMessage(Utils.deserialize(
                                        "<gray>Information of server <yellow>"+ server.getName() +"</yellow>:\n" +
                                        "<gray>- <yellow>Status:</yellow> "+ status +"</gray>\n" +
                                        "<gray>- <yellow>Port:</yellow> "+ server.getPort() +"</gray>\n" +
                                        "<gray>- <yellow>Jar:</yellow> "+ server.getJar() +"</gray>\n" +
                                        "<gray>- <yellow>Ram:</yellow> "+ server.getRam() +" MB</gray>" +
                                        (!server.getFlags().isEmpty() ? "\n<gray>- <yellow>Flags:</yellow> "+ server.getFlags() +"</gray>" : "")
                                ));

                            } else {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " was not found."));
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                        )
                )
                .then(BrigadierCommand.literalArgumentBuilder("load")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    String serversPath = "plugins/emirutilsvelocity/servermanager/servers";
                                    File serverFolder = new File(serversPath);

                                    Arrays.stream(serverFolder.list()).forEach(builder::suggest);

                                    return builder.buildFuture();
                                })
                        .then(BrigadierCommand.requiredArgumentBuilder("port", IntegerArgumentType.integer(0, 65353))
                        .then(BrigadierCommand.requiredArgumentBuilder("jar", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    String name = ctx.getArgument("name", String.class);
                                    String serverFolderPath = "plugins/emirutilsvelocity/servermanager/servers/" + name;
                                    File serverFolder = new File(serverFolderPath);

                                    if (Files.exists(Path.of(serverFolderPath))) {
                                        String[] jars = serverFolder.list((dir, name1) -> name1.endsWith(".jar"));
                                        Arrays.stream(jars).forEach(builder::suggest);
                                    }

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

                            if (EmirUtilsVelocity.getServerManager().getServer(name) != null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " is already loaded."));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (EmirUtilsVelocity.proxy.getServer(name).isPresent()) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " already exists in the proxy."));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!Files.exists(Path.of("plugins/emirutilsvelocity/servermanager/servers/" + name))) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " does not exist."));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name + "/" + jar))) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " does not contain a jar named " + jar + " ."));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendMessage(Utils.deserialize(
                                    "<gray>Loaded the server named <yellow>"+ name +"</yellow>:\n" +
                                    "<gray>- <yellow>Port:</yellow> "+ port +"</gray>\n" +
                                    "<gray>- <yellow>Jar:</yellow> "+ jar +"</gray>\n" +
                                    "<gray>- <yellow>Ram:</yellow> "+ ram +" MB</gray>\n"
                            ));

                            EmirUtilsVelocity.getServerManager().loadServer(name, port, jar, ram);

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("flags", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);
                                    int port = context.getArgument("port", int.class);
                                    String jar = context.getArgument("jar", String.class);
                                    int ram = context.getArgument("ram", int.class);
                                    String flags = context.getArgument("flags", String.class);

                                    if (EmirUtilsVelocity.getServerManager().getServer(name) != null) {
                                        context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " is already loaded."));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (!Files.exists(Path.of("plugins/emirutilsvelocity/servermanager/servers/" + name))) {
                                        context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " does not exist."));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    context.getSource().sendMessage(Utils.deserialize(
                                            "<gray>Loaded the server named <yellow>"+ name +"</yellow>:\n" +
                                            "<gray>- <yellow>Port:</yellow> "+ port +"</gray>\n" +
                                            "<gray>- <yellow>Jar:</yellow> "+ jar +"</gray>\n" +
                                            "<gray>- <yellow>Ram:</yellow> "+ ram +" MB</gray>\n" +
                                            "<gray>- <yellow>Flags:</yellow> "+ flags +"</gray>"
                                    ));

                                    EmirUtilsVelocity.getServerManager().loadServer(name, port, jar, ram, flags);

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        ))))
                )
                .then(BrigadierCommand.literalArgumentBuilder("unload")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    EmirUtilsVelocity.getServerManager().getServers().forEach((name1, server) -> builder.suggest(name1));

                                    return builder.buildFuture();
                                })
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);

                            if (EmirUtilsVelocity.getServerManager().getServer(name) == null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " is not loaded."));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendMessage(Utils.deserialize("<gray>Unloaded the server named <yellow>"+ name +"</yellow>.</gray>"));

                            EmirUtilsVelocity.getServerManager().unloadServer(name);

                            return Command.SINGLE_SUCCESS;
                        })
                        )
                )
                .then(BrigadierCommand.literalArgumentBuilder("start")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    EmirUtilsVelocity.getServerManager().getServers().values().stream()
                                            .filter(server -> server.getProcess() == null)
                                            .forEach(server -> builder.suggest(server.getName()));

                                    return builder.buildFuture();
                                })
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);

                            if (EmirUtilsVelocity.getServerManager().getServer(name) == null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " does not exist."));
                                return Command.SINGLE_SUCCESS;
                            }

                            Server server = EmirUtilsVelocity.getServerManager().getServer(name);

                            if (server.getProcess() != null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " is already online."));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendMessage(Utils.deserialize("<gray>Starting the server named <yellow>"+ name +"</yellow>...</gray>"));

                            server.start();

                            return Command.SINGLE_SUCCESS;
                        })
                        )
                )
                .then(BrigadierCommand.literalArgumentBuilder("stop")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    EmirUtilsVelocity.getServerManager().getServers().values().stream()
                                            .filter(server -> server.getProcess() != null)
                                            .forEach(server -> builder.suggest(server.getName()));

                                    return builder.buildFuture();
                                })
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);

                            if (EmirUtilsVelocity.getServerManager().getServer(name) == null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " does not exist."));
                                return Command.SINGLE_SUCCESS;
                            }

                            Server server = EmirUtilsVelocity.getServerManager().getServer(name);

                            if (server.getProcess() == null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " is not online."));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendMessage(Utils.deserialize("<gray>Stopping the server named <yellow>"+ name +"</yellow>...</gray>"));

                            server.stop();

                            return Command.SINGLE_SUCCESS;
                        })
                        )
                )
                .then(BrigadierCommand.literalArgumentBuilder("kill")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    EmirUtilsVelocity.getServerManager().getServers().values().stream()
                                            .filter(server -> server.getProcess() != null)
                                            .forEach(server -> builder.suggest(server.getName()));

                                    return builder.buildFuture();
                                })
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);

                            if (EmirUtilsVelocity.getServerManager().getServer(name) == null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " does not exist."));
                                return Command.SINGLE_SUCCESS;
                            }

                            Server server = EmirUtilsVelocity.getServerManager().getServer(name);

                            if (server.getProcess() == null) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " is not online."));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendMessage(Utils.deserialize("<gray>Killing the server named <yellow>"+ name +"</yellow>...</gray>"));

                            server.kill();

                            return Command.SINGLE_SUCCESS;
                        })
                        )
                )
                .then(BrigadierCommand.literalArgumentBuilder("delete")
                        .then(BrigadierCommand.requiredArgumentBuilder("name", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    String serversPath = "plugins/emirutilsvelocity/servermanager/servers";
                                    File serverFolder = new File(serversPath);

                                    Arrays.stream(serverFolder.list()).forEach(builder::suggest);

                                    return builder.buildFuture();
                                })
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);

                            Path serverPath = Path.of("plugins/emirutilsvelocity/servermanager/servers/" + name);

                            if (!Files.exists(serverPath)) {
                                context.getSource().sendMessage(Utils.deserialize("<red>Server named " + name + " does not exist."));
                                return Command.SINGLE_SUCCESS;
                            }

                            context.getSource().sendMessage(Utils.deserialize("<gray>Deleted the server named <yellow>"+ name +"</yellow>.</gray>"));

                            EmirUtilsVelocity.getServerManager().deleteServer(name);

                            return Command.SINGLE_SUCCESS;
                        })
                        )
                )
                .build();

        return new BrigadierCommand(node);
    }
}