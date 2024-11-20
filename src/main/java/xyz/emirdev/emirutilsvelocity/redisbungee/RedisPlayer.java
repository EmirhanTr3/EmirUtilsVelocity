package xyz.emirdev.emirutilsvelocity.redisbungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.jetbrains.annotations.Nullable;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.util.Optional;
import java.util.UUID;

public class RedisPlayer {
    private final RedisBungeeAPI redisBungee = EmirUtilsVelocity.getRedisBungee();

    private final UUID uuid;
    private final String name;

    public RedisPlayer(String name) {
        this.uuid = redisBungee.getUuidFromName(name, false);

        if (uuid != null) {
            Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
            this.name = optionalPlayer.isPresent() ?
                    optionalPlayer.get().getUsername() :
                    redisBungee.getNameFromUuid(uuid, false);
        } else {
            this.name = null;
        }
    }

    public RedisPlayer(UUID uuid) {
        this.uuid = uuid;

        if (uuid != null) {
            Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
            this.name = optionalPlayer.isPresent() ?
                    optionalPlayer.get().getUsername() :
                    redisBungee.getNameFromUuid(uuid, false);
        } else {
            this.name = null;
        }
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        if (uuid == null) return false;
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
