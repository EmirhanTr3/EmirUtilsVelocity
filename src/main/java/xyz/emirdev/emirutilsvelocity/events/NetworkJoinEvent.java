package xyz.emirdev.emirutilsvelocity.events;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class NetworkJoinEvent {
    @Subscribe
    public void onNetworkJoin(PostLoginEvent event) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
        Player player = event.getPlayer();

        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("proxyId", redisbungee.getProxy(player.getUniqueId()));
        map.put("name", player.getGameProfile().getName());
        map.put("clientbrand", player.getClientBrand());
        map.put("version", player.getProtocolVersion().getVersionIntroducedIn());

        String json = gson.toJson(map);
        redisbungee.sendChannelMessage("emirutilsvelocity:networkjoin", json);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (event.getChannel().equals("emirutilsvelocity:networkjoin")) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            Utils.sendToAllWithPermissions(
                "emirutilsvelocity.notifications.connect",
                "<#25BB65>→ <#35EE75>[<#25BB65>N<#35EE75>] [<#25BB65>+<#35EE75>] <#20AA50>"+ map.get("name") +" <#35EE75>[<#25BB65>"+ map.get("proxyId") +"<#35EE75>] <#259935>("+ map.get("clientbrand") +" "+ map.get("version") +")"
            );
        }
    }
}
