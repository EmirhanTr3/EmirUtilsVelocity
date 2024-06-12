package xyz.emirdev.emirutilsvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import xyz.emirdev.emirutilsvelocity.commands.*;
import xyz.emirdev.emirutilsvelocity.events.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Plugin(
        id = "emirutilsvelocity",
        name = "EmirUtilsVelocity",
        version = "1.0-SNAPSHOT",
        authors = "EmirhanTr3",
        dependencies = {
                @Dependency(id = "redisbungee"),
                @Dependency(id = "luckperms")
        }
)
public class EmirUtilsVelocity {
    public static ProxyServer proxy;
    public static PluginData data;

    public static List<UUID> staffChatToggledPlayers = new ArrayList<>();
    public static List<UUID> ownerChatToggledPlayers = new ArrayList<>();
    public static Map<UUID, UUID> playerLastMessagedPlayer = new HashMap<>();

    @Inject
    private Logger logger;

    @Inject
    public void EmirUtilsVelocityPlugin(ProxyServer proxy) {
        EmirUtilsVelocity.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Initializing configuration...");

        File configPath = new File("plugins/emirutilsvelocity/data.yml");
        if (!configPath.exists() && !configPath.mkdirs()) logger.warn("Couldn't make the path.");

        try {
            if (!configPath.exists() && !configPath.createNewFile()) logger.warn("Couldn't make the file.");
        } catch (IOException e) {
            logger.warn("Couldn't make the file:", e);
        }


        logger.info("Loading events...");

        proxy.getEventManager().register(this, new StaffChatCommand());
        proxy.getEventManager().register(this, new OwnerChatCommand());
        proxy.getEventManager().register(this, new MessageCommand());

        proxy.getEventManager().register(this, new NetworkJoinEvent());
        proxy.getEventManager().register(this, new NetworkLeaveEvent());
        proxy.getEventManager().register(this, new ChangeServerEvent());

        logger.info("Loading commands...");

        CommandManager commandManager = proxy.getCommandManager();

        commandManager.register(
            commandManager.metaBuilder("staffchat")
                .aliases("sc")
                .plugin(this)
                .build(),
            StaffChatCommand.createBrigadierCommand(proxy)
        );

        commandManager.register(
            commandManager.metaBuilder("ownerchat")
                .aliases("oc")
                .plugin(this)
                .build(),
            OwnerChatCommand.createBrigadierCommand(proxy)
        );

        commandManager.register(
            commandManager.metaBuilder("find")
                .aliases("sfind")
                .plugin(this)
                .build(),
            FindCommand.createBrigadierCommand(proxy)
        );

        commandManager.register(
            commandManager.metaBuilder("list")
                .aliases("slist")
                .plugin(this)
                .build(),
            ListCommand.createBrigadierCommand(proxy)
        );

        commandManager.register(
            commandManager.metaBuilder("message")
                .aliases("msg", "tell", "whisper", "m", "t", "w", "emsg")
                .plugin(this)
                .build(),
            MessageCommand.createBrigadierCommand(proxy)
        );

        commandManager.register(
            commandManager.metaBuilder("reply")
                .aliases("r", "er")
                .plugin(this)
                .build(),
            ReplyCommand.createBrigadierCommand(proxy)
        );

        logger.info("Loaded successfully");
    }
}
