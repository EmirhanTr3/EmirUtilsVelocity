package xyz.emirdev.echologic;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;
import revxrsal.commands.Lamp;
import revxrsal.commands.velocity.VelocityLamp;
import revxrsal.commands.velocity.VelocityLampConfig;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.echologic.commands.*;
import xyz.emirdev.echologic.config.ConfigHandler;
import xyz.emirdev.echologic.database.Database;
import xyz.emirdev.echologic.events.ChatEvent;
import xyz.emirdev.echologic.events.NetworkJoinEvent;
import xyz.emirdev.echologic.events.NetworkLeaveEvent;
import xyz.emirdev.echologic.events.ChangeServerEvent;
import xyz.emirdev.echologic.managers.BackendIntegrationManager;
import xyz.emirdev.echologic.parameters.InetSocketAddressParameterType;
import xyz.emirdev.echologic.parameters.PlayerIPAddress;
import xyz.emirdev.echologic.parameters.PlayerIPAddressParameterType;
import xyz.emirdev.echologic.parameters.RegisteredServerParameterType;
import xyz.emirdev.echologic.utils.proxy.RedisBungeeUtils;
import xyz.emirdev.echologic.utils.proxy.ProxyPlayer;
import xyz.emirdev.echologic.parameters.ProxyPlayerParameterType;
import xyz.emirdev.echologic.utils.proxy.ProxyUtils;
import xyz.emirdev.echologic.utils.proxy.VelocityUtils;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

import static revxrsal.commands.velocity.VelocityVisitors.brigadier;

@Plugin(id = "echologic", name = "EchoLogic", version = "2.1.0", authors = {
        "EmirhanTr3" }, dependencies = {
                @Dependency(id = "luckperms"),
                @Dependency(id = "redisbungee", optional = true)
        })
public class EchoLogic {
    private static EchoLogic instance;
    private static ProxyServer proxy;
    private static Path dataDirectory;
    private static ConfigHandler config;
    private static Database database;
    private static Lamp<VelocityCommandActor> lamp;
    private static LuckPerms luckPerms;
    private static BackendIntegrationManager backendIntegrationManager;
    private static ProxyUtils proxyUtils;

    @Inject
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public static EchoLogic get() {
        return instance;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }

    public static Path getDataDirectory() {
        return dataDirectory;
    }

    public static boolean hasRedisBungee() {
        return proxy.getPluginManager().isLoaded("redisbungee");
    }

    public static ConfigHandler getConfig() {
        return config;
    }

    public static Database getDatabase() {
        return database;
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public static ProxyUtils getProxyUtils() {
        return proxyUtils;
    }

    @Inject
    public EchoLogic(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        instance = this;
        EchoLogic.proxy = proxy;
        EchoLogic.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void ProxyInitializeEvent(ProxyInitializeEvent event) {
        config = new ConfigHandler();
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

        VelocityLampConfig<VelocityCommandActor> lampConfig = VelocityLampConfig.createDefault(this, proxy);
        lamp = VelocityLamp.builder(lampConfig)
                .parameterTypes(builder -> {
                    builder.addParameterType(ProxyPlayer.class, new ProxyPlayerParameterType());
                    builder.addParameterType(RegisteredServer.class, new RegisteredServerParameterType());
                    builder.addParameterType(InetSocketAddress.class, new InetSocketAddressParameterType());
                    builder.addParameterType(PlayerIPAddress.class, new PlayerIPAddressParameterType());
                }).build();

        loadCommands();

        lamp.accept(brigadier(lampConfig));

        List.of(
                backendIntegrationManager,
                new ChatEvent(),
                new NetworkJoinEvent(),
                new NetworkLeaveEvent(),
                new ChangeServerEvent()).forEach(e -> proxy.getEventManager().register(this, e));
    }

    public static void loadCommands() {
        lamp.unregisterAllCommands();

        List.of(
                new MainCommand(),
                new StaffChatCommand(),
                new OwnerChatCommand(),
                new FindCommand(),
                new ListCommand(),
                new ServerCommand(),
                new SendCommand(),
                new MessageCommand(),
                new ReplyCommand(),
                new KickCommand(),
                new SocialSpyCommand(),
                new IgnoreCommand(),
                new AnnouncementCommand(),
                new TransferCommand()).forEach(c -> lamp.register(c));

        if (config.getHubServer() != null) {
            lamp.register(new HubCommand());
        }
        if (config.getDiscordInvite() != null) {
            lamp.register(new DiscordCommand());
        }
        if (config.isIPCheckEnabled()) {
            lamp.register(new CheckIPCommand());
        }
    }
}
