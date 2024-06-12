package xyz.emirdev.emirutilsvelocity.events;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PlayerLeftNetworkEvent;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class NetworkLeaveEvent {
    @Subscribe
    public void onNetworkLeave(DisconnectEvent event) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
        Player player = event.getPlayer();

        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", player.getGameProfile().getName());

        String json = gson.toJson(map);
        redisbungee.sendChannelMessage("emirutilsvelocity:networkleave", json);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (event.getChannel().equals("emirutilsvelocity:networkleave")) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            Utils.sendToAllWithPermissions(
                "emirutilsvelocity.notifications.disconnect",
                "<#BB4050>← <#DD6070>[<#BB4050>N<#DD6070>] [<#BB4050>-<#DD6070>] <#AA3545>"+ map.get("name")
            );
        }
    }
}
