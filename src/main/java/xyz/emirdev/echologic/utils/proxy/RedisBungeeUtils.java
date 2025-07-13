package xyz.emirdev.echologic.utils.proxy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.utils.Utils;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RedisBungeeUtils implements ProxyUtils {
    private final RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();

    public void broadcast(String message, TagResolver... resolvers) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("message", Utils.unformatMessage(Utils.formatMessage(message, resolvers)));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:broadcast", json);
    }

    public void broadcastWithPermission(String perm, String message, TagResolver... resolvers) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("perm", perm);
        map.put("message", Utils.unformatMessage(Utils.formatMessage(message, resolvers)));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:broadcastWithPermission", json);
    }

    public void sendMessage(UUID uuid, String message, TagResolver... resolvers) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("message", Utils.unformatMessage(Utils.formatMessage(message, resolvers)));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:message", json);
    }

    public void connectAllPlayers(String server) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("server", server);

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:connectAllPlayers", json);
    }

    public void connectPlayer(UUID uuid, String server) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("server", server);

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:connectPlayer", json);
    }

    public void connectAllPlayersInServer(String server, String targetServer) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("server", server);
        map.put("targetServer", targetServer);

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:connectAllPlayersInServer", json);
    }

    public void kickPlayer(UUID uuid, String reason, TagResolver... resolvers) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("reason", Utils.unformatMessage(Utils.formatMessage(reason, resolvers)));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:kick", json);
    }

    public void sendSocialSpyMessage(Player player, ProxyPlayer target, String message) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("player", player.getUniqueId().toString());
        map.put("target", target.getUniqueId().toString());
        map.put("message", Utils.unformatMessage(Utils.formatMessage(
                "<#41BBFF>[<#2595CC>SocialSpy<#41BBFF>] <#2595CC><player> <#41BBFF>→ <#2595CC><target><#41BBFF>: <#60CCFF><message>",
                Placeholder.unparsed("player", player.getUsername()),
                Placeholder.unparsed("target", target.getName()),
                Placeholder.unparsed("message", message))));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:socialSpyMessage", json);
    }

    public void transferPlayer(UUID uuid, InetSocketAddress address) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("address", address.getHostName() + ":" + address.getPort());

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("echologic:transfer", json);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        Gson gson = new Gson();
        if (event.getChannel().startsWith("echologic:")) {
            String identifier = event.getChannel().replaceFirst("echologic:", "");
            Map<String, String> map = gson.fromJson(event.getMessage(), new TypeToken<>() {
            });

            switch (identifier) {
                case "broadcast" -> Utils.broadcast(map.get("message"));

                case "broadcastWithPermission" -> Utils.broadcastWithPermission(map.get("perm"), map.get("message"));

                case "message" -> {
                    Optional<Player> optionalPlayer = EchoLogic.getProxy()
                            .getPlayer(UUID.fromString(map.get("uuid")));
                    optionalPlayer.ifPresent(player -> Utils.sendMessage(player, map.get("message")));
                }

                case "connectAllPlayers" -> {
                    Optional<RegisteredServer> optionalServer = EchoLogic.getProxy()
                            .getServer(map.get("server"));
                    optionalServer.ifPresent(server -> EchoLogic.getProxy().getAllPlayers()
                            .forEach(player -> Utils.connectPlayer(player, server)));
                }

                case "connectPlayer" -> {
                    Optional<RegisteredServer> optionalServer = EchoLogic.getProxy()
                            .getServer(map.get("server"));
                    optionalServer.ifPresent(server -> {
                        Optional<Player> optionalPlayer = EchoLogic.getProxy()
                                .getPlayer(UUID.fromString(map.get("uuid")));
                        optionalPlayer.ifPresent(player -> Utils.connectPlayer(player, server));
                    });
                }

                case "connectAllPlayersInServer" -> {
                    Optional<RegisteredServer> optionalServer = EchoLogic.getProxy()
                            .getServer(map.get("server"));
                    optionalServer.ifPresent(server -> {
                        Optional<RegisteredServer> optionalTargetServer = EchoLogic.getProxy()
                                .getServer(map.get("targetServer"));
                        optionalTargetServer.ifPresent(targetServer -> {
                            server.getPlayersConnected().forEach(player -> Utils.connectPlayer(player, targetServer));
                        });
                    });
                }

                case "kick" -> {
                    Optional<Player> optionalPlayer = EchoLogic.getProxy()
                            .getPlayer(UUID.fromString(map.get("uuid")));
                    optionalPlayer.ifPresent(
                            player -> player.disconnect(MiniMessage.miniMessage().deserialize(map.get("reason"))));
                }

                case "socialSpyMessage" -> {
                    for (Player player : EchoLogic.getProxy().getAllPlayers()) {
                        if (EchoLogic.getDatabase().getPlayerData(player.getUniqueId()).hasSocialSpy()) {
                            if (player.hasPermission("echologic.socialspy")) {
                                if (map.get("player").equals(player.getUniqueId().toString()) ||
                                        map.get("target").equals(player.getUniqueId().toString()))
                                    continue;

                                Utils.sendMessage(player, map.get("message"));
                            } else {
                                EchoLogic.getDatabase().updateSocialSpy(player.getUniqueId(), false);
                            }
                        }
                    }

                    Utils.sendMessage(EchoLogic.getProxy().getConsoleCommandSource(), map.get("message"));
                }

                case "transfer" -> {
                    String[] split = map.get("address").split(":");
                    String hostname = split[0];
                    int port = Integer.valueOf(split[1]);
                    InetSocketAddress address = new InetSocketAddress(hostname, port);

                    Optional<Player> optionalPlayer = EchoLogic.getProxy()
                            .getPlayer(UUID.fromString(map.get("uuid")));
                    optionalPlayer.ifPresent(player -> player.transferToHost(address));
                }
            }
        }
    }
}
