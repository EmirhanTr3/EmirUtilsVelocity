package xyz.emirdev.echologic.utils.proxy;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.echologic.EchoLogic;

import java.util.Optional;
import java.util.UUID;

public class ProxyPlayer {
    private final ProxyServer proxy = EchoLogic.getProxy();

    private final Player player;
    private final UUID uuid;
    @Getter
    private final String name;

    public ProxyPlayer(String name) {
        Optional<Player> optionalPlayer = proxy.getPlayer(name);

        if (EchoLogic.hasRedisBungee()) {
            RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();

            this.player = null;
            this.uuid = redisBungee.getUuidFromName(name, false);

            if (uuid != null) {
                this.name = optionalPlayer.isPresent() ? optionalPlayer.get().getUsername()
                        : redisBungee.getNameFromUuid(uuid, false);
            } else {
                this.name = null;
            }

        } else {
            if (optionalPlayer.isPresent()) {
                this.player = optionalPlayer.get();
                this.name = optionalPlayer.get().getUsername();
                this.uuid = optionalPlayer.get().getUniqueId();
            } else {
                this.player = null;
                this.uuid = null;
                this.name = null;
            }
        }
    }

    public ProxyPlayer(UUID uuid) {
        this.uuid = uuid;
        Optional<Player> optionalPlayer = proxy.getPlayer(uuid);

        if (EchoLogic.hasRedisBungee()) {
            RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();

            this.player = null;

            if (uuid != null) {
                this.name = optionalPlayer.isPresent() ? optionalPlayer.get().getUsername()
                        : redisBungee.getNameFromUuid(uuid, false);
            } else {
                this.name = null;
            }

        } else {
            if (optionalPlayer.isPresent()) {
                this.player = optionalPlayer.get();
                this.name = optionalPlayer.get().getUsername();
            } else {
                this.player = null;
                this.name = null;
            }
        }
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean isOnline() {
        if (uuid == null)
            return false;
        return (EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().isPlayerOnline(uuid)
                : player.isActive());
    }

    public ServerInfo getServer() {
        return (EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getServerFor(uuid)
                : player.getCurrentServer().get().getServerInfo());
    }

    public String getProxy() {
        return (EchoLogic.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxy(uuid) : null);
    }

    public void sendMessage(String string, TagResolver... resolvers) {
        EchoLogic.getProxyUtils().sendMessage(uuid, string, resolvers);
    }
}
