package xyz.emirdev.emirutilsvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.exlll.configlib.YamlConfigurations;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import xyz.emirdev.emirutilsvelocity.commands.*;
import xyz.emirdev.emirutilsvelocity.events.*;
import xyz.emirdev.emirutilsvelocity.servermanager.ServerManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Plugin(
        id = "emirutilsvelocity",
        name = "EmirUtilsVelocity",
        version = "1.0-SNAPSHOT",
        authors = {"EmirhanTr3", "oCerial"},
        dependencies = {
                @Dependency(id = "redisbungee"),
                @Dependency(id = "luckperms")
        }
)
public class EmirUtilsVelocity {
    public static ProxyServer proxy;
    public static PluginData data;
    public static PluginConfig config;
    public static EmirUtilsVelocity instance;
    public static ServerManager serverManager;

    // Possibly dangerous (hope no one malicious uses this!)
    private static String velocitySecret;

    public static List<UUID> staffChatToggledPlayers = new ArrayList<>();
    public static List<UUID> ownerChatToggledPlayers = new ArrayList<>();
    public static Map<UUID, UUID> playerLastMessagedPlayer = new HashMap<>();

    @Inject
    private Logger logger;

    @Inject
    public void EmirUtilsVelocityPlugin(ProxyServer proxy) {
        EmirUtilsVelocity.proxy = proxy;

    }

    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static String getVelocitySecret() {
        return velocitySecret;
    }

    public static void reloadConfig() {
        if (config != null) config = null;

        Path configPath = Paths.get("plugins/emirutilsvelocity/config.yml");
        PluginConfig pc = new PluginConfig();
        if (!configPath.toFile().exists())
            YamlConfigurations.save(configPath, PluginConfig.class, pc);

        config = YamlConfigurations.load(configPath, PluginConfig.class);
    }

    @SuppressWarnings("deprecation")
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        instance = this;
        
        // Load plugin data
        logger.info("Initializing plugin data...");
        Path dataPath = Paths.get("plugins/emirutilsvelocity/data.yml");
        PluginData pd = new PluginData();
        if (!dataPath.toFile().exists())
            YamlConfigurations.save(dataPath, PluginData.class, pd);
            
        data = YamlConfigurations.load(dataPath, PluginData.class);

        // Load plugin config
        logger.info("Initializing plugin config...");
        reloadConfig();

        // Load server manager
        velocitySecret = Files.readAllLines(new File("forwarding.secret").toPath())
                .get(0);

        if (config.getServerManagerConfig().isEnabled()) {
            logger.info("Loading server manager...");
            serverManager = new ServerManager();

            Path jarsPath = Paths.get("plugins/emirutilsvelocity/servermanager/jars");
            Path serversPath = Paths.get("plugins/emirutilsvelocity/servermanager/servers");
            try {
                if (!Files.exists(jarsPath)) Files.createDirectory(jarsPath);
                if (!Files.exists(serversPath)) Files.createDirectory(serversPath);
            } catch (IOException exception) {
                logger.error("A ServerManager directory could not be created.", exception);
            }

            CommandManager commandManager = proxy.getCommandManager();

            commandManager.register(
                    commandManager.metaBuilder("servermanager")
                            .plugin(this)
                            .aliases("sm")
                            .build(),
                    ServerManagerCommand.createBrigadierCommand(proxy)
            );

            logger.info("Registered command in class " + ServerManagerCommand.class.getName().replace("xyz.emirdev.emirutilsvelocity.commands.", "") + " named servermanager with aliases sm");
        }
        
        // Load events
        logger.info("Loading events...");

        proxy.getEventManager().register(this, new StaffChatCommand());
        proxy.getEventManager().register(this, new OwnerChatCommand());
        proxy.getEventManager().register(this, new MessageCommand());
        proxy.getEventManager().register(this, new SocialSpyCommand());

        proxy.getEventManager().register(this, new NetworkJoinEvent());
        proxy.getEventManager().register(this, new NetworkLeaveEvent());
        proxy.getEventManager().register(this, new ChangeServerEvent());

        proxy.getEventManager().register(this, pd);

        logger.info("Loading commands...");

        CommandManager commandManager = proxy.getCommandManager();

        // Automatic command registration
        for (Class<?> clazz: new Reflections("xyz.emirdev.emirutilsvelocity.commands", new SubTypesScanner(false))
            .getSubTypesOf(Object.class)) {
            // get brigadier command shit
            Method brigadierMethod;
            try {
                brigadierMethod = clazz.getDeclaredMethod("createBrigadierCommand", ProxyServer.class);
            } catch (NoSuchMethodException e) {
                logger.error("Couldn't find the create command method in "+clazz.getName().replace("xyz.emirdev.emirutilsvelocity.commands.", "")+": ", e);
                continue;
            }

            // get command name
            String name;
            try {
                name = (String) clazz.getDeclaredField("name").get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("Couldn't find the command name in "+clazz.getName().replace("xyz.emirdev.emirutilsvelocity.commands.", "")+": ", e);
                continue;
            }

            // get command aliases
            List<String> aliases = null;
            try {
                aliases = (List<String>) clazz.getDeclaredField("aliases").get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}

            // get if command should load automatically
            boolean autoLoad = true;
            try {
                autoLoad = (boolean) clazz.getDeclaredField("autoLoad").get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}

            // get value of command shit
            BrigadierCommand command;
            try {
                command = (BrigadierCommand) brigadierMethod.invoke(null, proxy);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Couldn't find the brigadier data in "+clazz.getName().replace("xyz.emirdev.emirutilsvelocity.commands.", "")+": ", e);
                continue;
            }


            String className = clazz.getName().replace("xyz.emirdev.emirutilsvelocity.commands.", "");

            // run registration magic
            if (autoLoad) {
                CommandMeta.Builder metaBuilder = commandManager.metaBuilder(name);
                if (aliases != null) metaBuilder.aliases(aliases.toArray(new String[0]));

                commandManager.register(
                        metaBuilder
                                .plugin(this)
                                .build(),
                        command
                );

                logger.info("Registered command in class " + className + " named " + name + ((aliases != null) ? " with aliases " + String.join(", ", aliases) : ""));
            } else {
                logger.info("Skipped command in class " + className + " named " + name);
            }
        }

        logger.info("Loaded successfully");
    }
}
