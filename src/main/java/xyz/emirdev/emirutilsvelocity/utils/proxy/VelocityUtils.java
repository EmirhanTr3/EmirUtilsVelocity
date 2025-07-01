package xyz.emirdev.emirutilsvelocity.utils.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VelocityUtils implements ProxyUtils {
    public void broadcast(String message, TagResolver... resolvers) {
        Utils.broadcast(message, resolvers);
    }

    public void broadcastWithPermission(String perm, String message, TagResolver... resolvers) {
        Utils.broadcastWithPermission(perm, message, resolvers);
    }

    public void sendMessage(UUID uuid, String message, TagResolver... resolvers) {
        Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> Utils.sendMessage(player, message, resolvers));
    }

    public void connectAllPlayers(String server) {
        Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy().getServer(server);
        optionalServer.ifPresent(serverv -> EmirUtilsVelocity.getProxy().getAllPlayers()
                .forEach(player -> Utils.connectPlayer(player, serverv)));
    }

    public void connectPlayer(UUID uuid, String server) {
        Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy().getServer(server);
        optionalServer.ifPresent(serverv -> {
            Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
            optionalPlayer.ifPresent(player -> Utils.connectPlayer(player, serverv));
        });
    }

    public void connectAllPlayersInServer(String server, String targetServer) {
        Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy().getServer(server);
        optionalServer.ifPresent(serverv -> {
            Optional<RegisteredServer> optionalTargetServer = EmirUtilsVelocity.getProxy().getServer(targetServer);
            optionalTargetServer.ifPresent(targetServerv -> serverv.getPlayersConnected()
                    .forEach(player -> Utils.connectPlayer(player, targetServerv)));
        });
    }

    public void kickPlayer(UUID uuid, String reason, TagResolver... resolvers) {
        Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> player.disconnect(Utils.formatMessage(reason, resolvers)));
    }

    public void sendSocialSpyMessage(Player player, ProxyPlayer target, String message) {
        String msg = "<#41BBFF>[<#2595CC>SocialSpy<#41BBFF>] <#2595CC><player> <#41BBFF>→ <#2595CC><target><#41BBFF>: <#60CCFF><message>";
        TagResolver[] resolvers = List.of(
                Placeholder.unparsed("player", player.getUsername()),
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("message", message)).toArray(new TagResolver[0]);

        for (Player loopPlayer : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            if (!EmirUtilsVelocity.getDatabase().getPlayerData(loopPlayer.getUniqueId()).hasSocialSpy())
                continue;
            if (player.hasPermission("emirutilsvelocity.socialspy")) {
                if (player.getUniqueId().equals(loopPlayer.getUniqueId())
                        || player.getUniqueId().equals(loopPlayer.getUniqueId()))
                    continue;
                Utils.sendMessage(loopPlayer, msg, resolvers);
                continue;
            }
            EmirUtilsVelocity.getDatabase().updateSocialSpy(loopPlayer.getUniqueId(), false);
        }
        Utils.sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), msg, resolvers);
    }

    public void transferPlayer(UUID uuid, InetSocketAddress address) {
        Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> player.transferToHost(address));
    }
}
