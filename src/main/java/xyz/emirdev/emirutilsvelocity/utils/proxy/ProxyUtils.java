package xyz.emirdev.emirutilsvelocity.utils.proxy;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.UUID;

public interface ProxyUtils {
    void broadcastWithPermission(String perm, String message, TagResolver... resolver);

    void sendMessage(UUID uuid, String message, TagResolver... resolver);

    void connectAllPlayers(String server);

    void connectPlayer(UUID uuid, String server);

    void connectAllPlayersInServer(String server, String targetServer);

    void kickPlayer(UUID uuid, String reason, TagResolver... resolver);

    void sendSocialSpyMessage(Player player, ProxyPlayer target, String message);
}
