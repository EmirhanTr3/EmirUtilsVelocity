package xyz.emirdev.echologic;

import com.google.inject.Inject;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;
import xyz.emirdev.echologic.managers.BackendIntegrationManager;
import xyz.emirdev.echologic.utils.ClassUtils;
import xyz.emirdev.echologic.utils.proxy.RedisBungeeUtils;
import xyz.emirdev.echologic.utils.proxy.ProxyUtils;
import xyz.emirdev.echologic.utils.proxy.VelocityUtils;

import java.nio.file.Path;

@Plugin(
        id = "echologic",
        name = "EchoLogic",
        version = BuildConstants.VERSION,
        authors = {
                "EmirhanTr3"
        },
        dependencies = {
                @Dependency(id = "luckperms"),
                @Dependency(id = "redisbungee", optional = true)
        })
public class EchoLogic {
    @Getter
    private static EchoLogic instance;
    @Getter
    private static ProxyServer proxy;
    private static Path dataDirectory;
    @Getter
    private static PluginConfig config;
    @Getter
    private static Database database;
    @Getter
    private static LuckPerms luckPerms;
    private static BackendIntegrationManager backendIntegrationManager;
    @Getter
    private static ProxyUtils proxyUtils;

    @Getter
    @Inject
    private Logger logger;

    public static EchoLogic get() {
        return instance;
    }

    public static boolean hasRedisBungee() {
        return proxy.getPluginManager().isLoaded("redisbungee");
    }

    @Inject
    public EchoLogic(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        instance = this;
        EchoLogic.proxy = proxy;
        EchoLogic.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void ProxyInitializeEvent(ProxyInitializeEvent event) {
        config = new PluginConfig(dataDirectory);
        config.load();
        database = new Database();
        luckPerms = LuckPermsProvider.get();
        backendIntegrationManager = new BackendIntegrationManager();

        if (proxy.getPluginManager().isLoaded("redisbungee")) {
            logger.info("Found redisbungee. Using redisbungee api.");

            proxyUtils = new RedisBungeeUtils();
            proxy.getEventManager().register(this, proxyUtils);
        } else {
            proxyUtils = new VelocityUtils();
        }

        ClassUtils.findClasses(
                "xyz.emirdev.echologic.commands",
                clazz -> clazz.extendsSuperclass(PluginCommand.class),
                clazz -> {
                    PluginCommand pluginCommand;
                    try {
                        pluginCommand = clazz.loadClass().asSubclass(PluginCommand.class).getConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    LiteralCommandNode<CommandSource> command = pluginCommand.getCommand();
                    BrigadierCommand brigadierCommand = new BrigadierCommand(command);

                    CommandMeta commandMeta = proxy.getCommandManager().metaBuilder(command.getLiteral())
                            .aliases(pluginCommand.getAliases().toArray(String[]::new))
                            .plugin(this)
                            .build();

                    proxy.getCommandManager().register(commandMeta, brigadierCommand);
                }
        );

        ClassUtils.findClasses(
                "xyz.emirdev.echologic.events",
                clazz -> true,
                clazz -> {
                    try {
                        proxy.getEventManager().register(this, clazz.loadClass().getConstructor().newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        proxy.getEventManager().register(this, backendIntegrationManager);
    }
}
