package xyz.emirdev.emirutilsvelocity;

import com.google.inject.Inject;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;
import revxrsal.commands.Lamp;
import revxrsal.commands.velocity.VelocityLamp;
import revxrsal.commands.velocity.VelocityLampConfig;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.emirutilsvelocity.commands.*;
import xyz.emirdev.emirutilsvelocity.config.ConfigHandler;
import xyz.emirdev.emirutilsvelocity.database.Database;
import xyz.emirdev.emirutilsvelocity.events.ChatEvent;
import xyz.emirdev.emirutilsvelocity.events.NetworkJoinEvent;
import xyz.emirdev.emirutilsvelocity.events.NetworkLeaveEvent;
import xyz.emirdev.emirutilsvelocity.events.ChangeServerEvent;
import xyz.emirdev.emirutilsvelocity.parameters.InetSocketAddressParameterType;
import xyz.emirdev.emirutilsvelocity.parameters.RegisteredServerParameterType;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisBungeeUtils;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisPlayer;
import xyz.emirdev.emirutilsvelocity.parameters.RedisPlayerParameterType;

import java.net.InetSocketAddress;
import java.util.List;

import static revxrsal.commands.velocity.VelocityVisitors.brigadier;

@Plugin(
        id = "emirutilsvelocity",
        name = "EmirUtilsVelocity",
        version = "2.0.0",
        authors = {"EmirhanTr3"},
        dependencies = {
                @Dependency(id = "luckperms"),
                @Dependency(id = "redisbungee")
        }
)
public class EmirUtilsVelocity {
    private static EmirUtilsVelocity instance;
    private static ProxyServer proxy;
    private static RedisBungeeAPI redisBungee;
    private static ConfigHandler config;
    private static Database database;
    private static LuckPerms luckPerms;

    @Inject
    private Logger logger;

    public static EmirUtilsVelocity get() {
        return instance;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }

    public static RedisBungeeAPI getRedisBungee() {
        return redisBungee;
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

    @Inject
    public EmirUtilsVelocity(ProxyServer proxy) {
        instance = this;
        EmirUtilsVelocity.proxy = proxy;
    }

    @Subscribe
    public void ProxyInitializeEvent(ProxyInitializeEvent event) {
        redisBungee = RedisBungeeAPI.getRedisBungeeApi();
        config = new ConfigHandler();
        database = new Database();
        luckPerms = LuckPermsProvider.get();

        VelocityLampConfig<VelocityCommandActor> lampConfig = VelocityLampConfig
                .createDefault(this, proxy);
        Lamp<VelocityCommandActor> lamp = VelocityLamp.builder(lampConfig)
                .parameterTypes(builder -> {
                    builder.addParameterType(RedisPlayer.class, new RedisPlayerParameterType());
                    builder.addParameterType(RegisteredServer.class, new RegisteredServerParameterType());
                    builder.addParameterType(InetSocketAddress.class, new InetSocketAddressParameterType());
                })
                .build();

        List.of(
                new StaffChatCommand(),
                new OwnerChatCommand(),
                new FindCommand(),
                new ListCommand(),
                new ServerCommand(),
                new CheckIPCommand()
        ).forEach(lamp::register);

        if (config.getHubServer() != null) lamp.register(new HubCommand());
        if (config.getDiscordInvite() != null) lamp.register(new DiscordCommand());

        lamp.accept(brigadier(lampConfig));

        List.of(
                new RedisBungeeUtils(),
                new ChatEvent(),
                new NetworkJoinEvent(),
                new NetworkLeaveEvent(),
                new ChangeServerEvent()
        ).forEach(e -> proxy.getEventManager().register(this, e));
    }
}
