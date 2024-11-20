package xyz.emirdev.emirutilsvelocity.redisbungee;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RedisBungeeUtils {
    public static void broadcastWithPermission(String perm, String message, Object... args) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("perm", perm);
        map.put("message", String.format(message, args));

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:broadcastWithPermission", json);
    }

    public static void sendMessage(UUID uuid, String message, Object... args) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("message", String.format(message, args));

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:message", json);
    }

    public static void connectAllPlayers(String server) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("server", server);

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:connectAllPlayers", json);
    }

    public static void connectPlayer(UUID uuid, String server) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("server", server);

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:connectPlayer", json);
    }

    public static void connectAllPlayersInServer(String server, String targetServer) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("server", server);
        map.put("targetServer", targetServer);

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:connectAllPlayersInServer", json);
    }

    public static void kickPlayer(UUID uuid, String reason, Object... args) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("reason", String.format(reason, args));

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:kick", json);
    }

    public static void sendSocialSpyMessage(Player player, RedisPlayer target, String message) {
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("player", player.getUniqueId().toString());
        map.put("target", target.getUniqueId().toString());
        map.put("message", String.format(
                "<#41BBFF>[<#2595CC>SocialSpy<#41BBFF>] <#2595CC>%s <#41BBFF>→ <#2595CC>%s<#41BBFF>: <#60CCFF>%s",
                player.getUsername(),
                target.getName(),
                Utils.sanitize(message)
        ));

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:socialSpyMessage", json);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        Gson gson = new Gson();
        if (event.getChannel().startsWith("emirutilsvelocity:")) {
            String identifier = event.getChannel().replaceFirst("emirutilsvelocity:", "");
            Map<String, String> map = gson.fromJson(event.getMessage(), new TypeToken<>(){});

            if (identifier.equals("broadcastWithPermission")) {
                Utils.broadcastWithPermission(map.get("perm"), map.get("message"));

            } else if (identifier.equals("message")) {
                Optional<Player> player = EmirUtilsVelocity.getProxy().getPlayer(UUID.fromString(map.get("uuid")));
                player.ifPresent(value -> Utils.sendMessage(value, map.get("message")));

            } else if (identifier.equals("connectAllPlayers")) {
                Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy().getServer(map.get("server"));
                optionalServer.ifPresent(server ->
                        server.getPlayersConnected().forEach(player -> Utils.connectPlayer(player, server))
                );

            } else if (identifier.equals("connectPlayer")) {
                Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy().getServer(map.get("server"));
                optionalServer.ifPresent(server -> {
                    Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(UUID.fromString(map.get("uuid")));
                    optionalPlayer.ifPresent(player -> Utils.connectPlayer(player, server));
                });

            } else if (identifier.equals("connectAllPlayersInServer")) {
                Optional<RegisteredServer> optionalServer = EmirUtilsVelocity.getProxy().getServer(map.get("server"));
                optionalServer.ifPresent(server -> {
                    Optional<RegisteredServer> optionalTargetServer = EmirUtilsVelocity.getProxy().getServer(map.get("targetServer"));
                    optionalTargetServer.ifPresent(targetServer -> {
                        targetServer.getPlayersConnected().forEach(player -> Utils.connectPlayer(player, server));
                    });
                });

            } else if (identifier.equals("kick")) {
                Optional<Player> optionalPlayer = EmirUtilsVelocity.getProxy().getPlayer(UUID.fromString(map.get("uuid")));
                optionalPlayer.ifPresent(player -> player.disconnect(MiniMessage.miniMessage().deserialize(map.get("reason"))));

            } else if (identifier.equals("socialSpyMessage")) {
                for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
                    if (EmirUtilsVelocity.getDatabase().getPlayerData(player.getUniqueId()).hasSocialSpy()) {
                        if (player.hasPermission("emirutilsvelocity.socialspy")) {
                            if (
                                    map.get("player").equals(player.getUniqueId().toString()) ||
                                    map.get("target").equals(player.getUniqueId().toString())
                            ) continue;

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
