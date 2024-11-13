package xyz.emirdev.emirutilsvelocity.redisbungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.server.ServerInfo;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.util.UUID;

public class RedisPlayer {
    private final RedisBungeeAPI redisBungee = EmirUtilsVelocity.getRedisBungee();

    private final UUID uuid;
    private final String name;

    public RedisPlayer(String name) {
        this.name = name;
        this.uuid = redisBungee.getUuidFromName(name, false);
    }

    public RedisPlayer(UUID uuid) {
        this.uuid = uuid;
        this.name = redisBungee.getNameFromUuid(uuid, false);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return redisBungee.isPlayerOnline(uuid);
    }

    public ServerInfo getServer() {
        return redisBungee.getServerFor(uuid);
    }

    public String getProxy() {
        return redisBungee.getProxy(uuid);
    }

    public void sendMessage(String string, Object... args) {
        RedisBungeeUtils.sendMessage(uuid, string, args);
    }
}
