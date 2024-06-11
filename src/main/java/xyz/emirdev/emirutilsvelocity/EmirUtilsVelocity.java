package xyz.emirdev.emirutilsvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import xyz.emirdev.emirutilsvelocity.commands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static List<UUID> staffChatToggledPlayers = new ArrayList<>();
    public static List<UUID> ownerChatToggledPlayers = new ArrayList<>();

    @Inject
    private Logger logger;

    @Inject
    public void EmirUtilsVelocityPlugin(ProxyServer proxy) {
        EmirUtilsVelocity.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Loading events...");

        proxy.getEventManager().register(this, new StaffChatCommand());
        proxy.getEventManager().register(this, new OwnerChatCommand());

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

        logger.info("Loaded successfully");
    }
}
