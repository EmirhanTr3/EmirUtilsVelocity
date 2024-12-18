package xyz.emirdev.emirutilsvelocity.utils.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.Optional;
import java.util.UUID;

public class VelocityUtils implements ProxyUtils {
    public void broadcastWithPermission(String perm, String message, Object... args) {
        Utils.broadcastWithPermission(perm, message, args);
    }

    public void sendMessage(UUID uuid, String message, Object... args) {
        Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> Utils.sendMessage(player, message, args));
    }

    public void connectAllPlayers(String server) {
        Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy().getServer(server);
        optionalServer.ifPresent(serverv ->
                EmirUtilsVelocity.getProxy().getAllPlayers().forEach(player -> Utils.connectPlayer(player, serverv))
        );
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
            optionalTargetServer.ifPresent(targetServerv -> {
                serverv.getPlayersConnected().forEach(player -> Utils.connectPlayer(player, targetServerv));
            });
        });
    }

    public void kickPlayer(UUID uuid, String reason, Object... args) {
        Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> player.disconnect(MiniMessage.miniMessage().deserialize(Utils.stringFormat(reason, args))));
    }

    public void sendSocialSpyMessage(Player player, ProxyPlayer target, String message) {
        String msg = Utils.stringFormat(
                "<#41BBFF>[<#2595CC>SocialSpy<#41BBFF>] <#2595CC>{0} <#41BBFF>→ <#2595CC>{1}<#41BBFF>: <#60CCFF>{2}",
                player.getUsername(),
                target.getName(),
                message
        );

        for (Player loopPlayer : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            if (EmirUtilsVelocity.getDatabase().getPlayerData(loopPlayer.getUniqueId()).hasSocialSpy()) {
                if (player.hasPermission("emirutilsvelocity.socialspy")) {
                    if (
                            player.getUniqueId().equals(loopPlayer.getUniqueId()) ||
                            player.getUniqueId().equals(loopPlayer.getUniqueId())
                    ) continue;

                    Utils.sendMessage(loopPlayer, msg);
                } else {
                    EmirUtilsVelocity.getDatabase().updateSocialSpy(loopPlayer.getUniqueId(), false);
                }
            }
        }

        Utils.sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), msg);
    }
}
