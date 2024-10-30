package xyz.emirdev.emirutilsvelocity;

import com.google.inject.Inject;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;
import revxrsal.commands.Lamp;
import revxrsal.commands.velocity.VelocityLamp;
import revxrsal.commands.velocity.VelocityLampConfig;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.emirutilsvelocity.commands.OwnerChatCommand;
import xyz.emirdev.emirutilsvelocity.commands.StaffChatCommand;
import xyz.emirdev.emirutilsvelocity.config.ConfigHandler;
import xyz.emirdev.emirutilsvelocity.database.Database;
import xyz.emirdev.emirutilsvelocity.events.ChatEvent;
import xyz.emirdev.emirutilsvelocity.events.NetworkJoinEvent;
import xyz.emirdev.emirutilsvelocity.events.NetworkLeaveEvent;
import xyz.emirdev.emirutilsvelocity.events.ChangeServerEvent;
import xyz.emirdev.emirutilsvelocity.utils.RedisBungeeUtils;

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

        VelocityLampConfig<VelocityCommandActor> config = VelocityLampConfig
                .createDefault(this, proxy);
        Lamp<VelocityCommandActor> lamp = VelocityLamp.builder(config)
                .build();

        List.of(
                new StaffChatCommand(),
                new OwnerChatCommand()
        ).forEach(lamp::register);

        lamp.accept(brigadier(config));

        List.of(
                new RedisBungeeUtils(),
                new ChatEvent(),
                new NetworkJoinEvent(),
                new NetworkLeaveEvent(),
                new ChangeServerEvent()
        ).forEach(e -> proxy.getEventManager().register(this, e));
    }
}
