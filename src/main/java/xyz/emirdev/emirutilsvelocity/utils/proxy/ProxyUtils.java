package xyz.emirdev.emirutilsvelocity.utils.proxy;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.UUID;

public interface ProxyUtils {
    void broadcast(String message, TagResolver... resolvers);

    void broadcastWithPermission(String perm, String message, TagResolver... resolvers);

    void sendMessage(UUID uuid, String message, TagResolver... resolvers);

    void connectAllPlayers(String server);

    void connectPlayer(UUID uuid, String server);

    void connectAllPlayersInServer(String server, String targetServer);

    void kickPlayer(UUID uuid, String reason, TagResolver... resolvers);

    void sendSocialSpyMessage(Player player, ProxyPlayer target, String message);
}
