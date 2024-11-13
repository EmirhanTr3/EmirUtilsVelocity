package xyz.emirdev.emirutilsvelocity.redisbungee;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
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

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        Gson gson = new Gson();
        if (event.getChannel().startsWith("emirutilsvelocity:")) {
            String identifier = event.getChannel().replaceFirst("emirutilsvelocity:", "");
            Map<String, String> map = gson.fromJson(event.getMessage(), new TypeToken<>() {});

            if (identifier.equals("broadcastWithPermission")) {
                Utils.broadcastWithPermission(map.get("perm"), map.get("message"));

            } else if (identifier.equals("emirutilsvelocity:message")) {
                Optional<Player> player = EmirUtilsVelocity.getProxy().getPlayer(UUID.fromString(map.get("uuid")));
                player.ifPresent(value -> Utils.sendMessage(value, map.get("message")));
            }
        }
    }
}
