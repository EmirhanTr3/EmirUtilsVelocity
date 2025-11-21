package xyz.emirdev.echologic.utils.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.commands.SudoCommand;
import xyz.emirdev.echologic.utils.Utils;

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
        Optional<Player> optionalPlayer = EchoLogic.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> Utils.sendMessage(player, message, resolvers));
    }

    public void connectAllPlayers(String server) {
        Optional<RegisteredServer> optionalServer = EchoLogic.getProxy().getServer(server);
        optionalServer.ifPresent(serverv -> EchoLogic.getProxy().getAllPlayers()
                .forEach(player -> Utils.connectPlayer(player, serverv)));
    }

    public void connectPlayer(UUID uuid, String server) {
        Optional<RegisteredServer> optionalServer = EchoLogic.getProxy().getServer(server);
        optionalServer.ifPresent(serverv -> {
            Optional<Player> optionalPlayer = EchoLogic.getProxy().getPlayer(uuid);
            optionalPlayer.ifPresent(player -> Utils.connectPlayer(player, serverv));
        });
    }

    public void connectAllPlayersInServer(String server, String targetServer) {
        Optional<RegisteredServer> optionalServer = EchoLogic.getProxy().getServer(server);
        optionalServer.ifPresent(serverv -> {
            Optional<RegisteredServer> optionalTargetServer = EchoLogic.getProxy().getServer(targetServer);
            optionalTargetServer.ifPresent(targetServerv -> serverv.getPlayersConnected()
                    .forEach(player -> Utils.connectPlayer(player, targetServerv)));
        });
    }

    public void kickPlayer(UUID uuid, String reason, TagResolver... resolvers) {
        Optional<Player> optionalPlayer = EchoLogic.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> player.disconnect(Utils.formatMessage(reason, resolvers)));
    }

    public void sendSocialSpyMessage(Player player, ProxyPlayer target, String message) {
        String msg = "<#41BBFF>[<#2595CC>SocialSpy<#41BBFF>] <#2595CC><player> <#41BBFF>→ <#2595CC><target><#41BBFF>: <#60CCFF><message>";
        TagResolver[] resolvers = List.of(
                Placeholder.unparsed("player", player.getUsername()),
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("message", message)).toArray(new TagResolver[0]);

        for (Player loopPlayer : EchoLogic.getProxy().getAllPlayers()) {
            if (!EchoLogic.getDatabase().getPlayerData(loopPlayer.getUniqueId()).hasSocialSpy())
                continue;
            if (player.hasPermission("echologic.socialspy")) {
                if (player.getUniqueId().equals(loopPlayer.getUniqueId())
                        || player.getUniqueId().equals(loopPlayer.getUniqueId()))
                    continue;
                Utils.sendMessage(loopPlayer, msg, resolvers);
                continue;
            }
            EchoLogic.getDatabase().updateSocialSpy(loopPlayer.getUniqueId(), false);
        }
        Utils.sendMessage(EchoLogic.getProxy().getConsoleCommandSource(), msg, resolvers);
    }

    public void transferPlayer(UUID uuid, InetSocketAddress address) {
        Optional<Player> optionalPlayer = EchoLogic.getProxy().getPlayer(uuid);
        optionalPlayer.ifPresent(player -> player.transferToHost(address));
    }

    @Override
    public void sudoPlayer(SudoCommand.SudoMode mode, ProxyPlayer proxyPlayer, String message) {
        Optional<Player> optionalPlayer = EchoLogic.getProxy().getPlayer(proxyPlayer.getUniqueId());
        if (optionalPlayer.isEmpty()) return;
        Player player = optionalPlayer.get();

        if (mode == SudoCommand.SudoMode.PROXY && message.startsWith("/")) {
            EchoLogic.getProxy().getCommandManager().executeAsync(player, message.replaceFirst("/", ""));
            return;
        }

        player.spoofChatInput(message);
    }
}
