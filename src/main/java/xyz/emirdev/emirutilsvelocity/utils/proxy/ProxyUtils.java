package xyz.emirdev.emirutilsvelocity.utils.proxy;

import com.velocitypowered.api.proxy.Player;

import java.util.UUID;

public interface ProxyUtils {
    void broadcastWithPermission(String perm, String message, Object... args);

    void sendMessage(UUID uuid, String message, Object... args);

    void connectAllPlayers(String server);

    void connectPlayer(UUID uuid, String server);

    void connectAllPlayersInServer(String server, String targetServer);

    void kickPlayer(UUID uuid, String reason, Object... args);

    void sendSocialSpyMessage(Player player, ProxyPlayer target, String message);
}
