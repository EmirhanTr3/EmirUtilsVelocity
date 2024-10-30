package xyz.emirdev.emirutilsvelocity.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.util.LinkedHashMap;
import java.util.Map;

public class RedisBungeeUtils {
    public static void broadcastWithPermission(String perm, String string, Object... args) {
        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("perm", perm);
        map.put("message", String.format(string, args));

        String json = gson.toJson(map);
        EmirUtilsVelocity.getRedisBungee().sendChannelMessage("emirutilsvelocity:broadcastWithPermission", json);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        Gson gson = new Gson();

        if (event.getChannel().equals("emirutilsvelocity:broadcastWithPermission")) {
            Map<String, String> map = gson.fromJson(event.getMessage(), new TypeToken<>(){});
            Utils.broadcastWithPermission(map.get("perm"), map.get("message"));
        }
    }
}
