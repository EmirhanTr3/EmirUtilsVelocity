package xyz.emirdev.emirutilsvelocity.utils.proxy;

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
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RedisBungeeUtils implements ProxyUtils {
    private final RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();

    public void broadcastWithPermission(String perm, String message, TagResolver... resolvers) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("perm", perm);
        map.put("message", Utils.unformatMessage(Utils.formatMessage(message, resolvers)));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("emirutilsvelocity:broadcastWithPermission", json);
    }

    public void sendMessage(UUID uuid, String message, TagResolver... resolvers) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("message", Utils.unformatMessage(Utils.formatMessage(message, resolvers)));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("emirutilsvelocity:message", json);
    }

    public void connectAllPlayers(String server) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("server", server);

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("emirutilsvelocity:connectAllPlayers", json);
    }

    public void connectPlayer(UUID uuid, String server) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("server", server);

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("emirutilsvelocity:connectPlayer", json);
    }

    public void connectAllPlayersInServer(String server, String targetServer) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("server", server);
        map.put("targetServer", targetServer);

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("emirutilsvelocity:connectAllPlayersInServer", json);
    }

    public void kickPlayer(UUID uuid, String reason, TagResolver... resolvers) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("reason", Utils.unformatMessage(Utils.formatMessage(reason, resolvers)));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("emirutilsvelocity:kick", json);
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
                Placeholder.unparsed("message", MiniMessage.miniMessage().escapeTags(message)))));

        String json = gson.toJson(map);
        redisBungee.sendChannelMessage("emirutilsvelocity:socialSpyMessage", json);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        Gson gson = new Gson();
        if (event.getChannel().startsWith("emirutilsvelocity:")) {
            String identifier = event.getChannel().replaceFirst("emirutilsvelocity:", "");
            Map<String, String> map = gson.fromJson(event.getMessage(), new TypeToken<>() {
            });

            switch (identifier) {
                case "broadcastWithPermission" -> Utils.broadcastWithPermission(map.get("perm"), map.get("message"));

                case "message" -> {
                    Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy()
                            .getPlayer(UUID.fromString(map.get("uuid")));
                    optionalPlayer.ifPresent(player -> Utils.sendMessage(player, map.get("message")));
                }

                case "connectAllPlayers" -> {
                    Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy()
                            .getServer(map.get("server"));
                    optionalServer.ifPresent(server -> EmirUtilsVelocity.getProxy().getAllPlayers()
                            .forEach(player -> Utils.connectPlayer(player, server)));
                }

                case "connectPlayer" -> {
                    Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy()
                            .getServer(map.get("server"));
                    optionalServer.ifPresent(server -> {
                        Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy()
                                .getPlayer(UUID.fromString(map.get("uuid")));
                        optionalPlayer.ifPresent(player -> Utils.connectPlayer(player, server));
                    });
                }

                case "connectAllPlayersInServer" -> {
                    Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy()
                            .getServer(map.get("server"));
                    optionalServer.ifPresent(server -> {
                        Optional<RegisteredServer> optionalTargetServer = EmirUtilsVelocity.getProxy()
                                .getServer(map.get("targetServer"));
                        optionalTargetServer.ifPresent(targetServer -> {
                            server.getPlayersConnected().forEach(player -> Utils.connectPlayer(player, targetServer));
                        });
                    });
                }

                case "kick" -> {
                    Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy()
                            .getPlayer(UUID.fromString(map.get("uuid")));
                    optionalPlayer.ifPresent(
                            player -> player.disconnect(MiniMessage.miniMessage().deserialize(map.get("reason"))));
                }

                case "socialSpyMessage" -> {
                    for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
                        if (EmirUtilsVelocity.getDatabase().getPlayerData(player.getUniqueId()).hasSocialSpy()) {
                            if (player.hasPermission("emirutilsvelocity.socialspy")) {
                                if (map.get("player").equals(player.getUniqueId().toString()) ||
                                        map.get("target").equals(player.getUniqueId().toString()))
                                    continue;

                                Utils.sendMessage(player, map.get("message"));
                            } else {
                                EmirUtilsVelocity.getDatabase().updateSocialSpy(player.getUniqueId(), false);
                            }
                        }
                    }

                    Utils.sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), map.get("message"));
                }
            }
        }
    }
}
