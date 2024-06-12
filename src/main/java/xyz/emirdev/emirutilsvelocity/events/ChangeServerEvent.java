package xyz.emirdev.emirutilsvelocity.events;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChangeServerEvent {
    @Subscribe
    public void onChangeServer(ServerPreConnectEvent event) {
        if (event.getPreviousServer() != null) {
            RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
            Player player = event.getPlayer();

            Gson gson = new Gson();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("proxyId", redisbungee.getProxy(player.getUniqueId()));
            map.put("name", player.getGameProfile().getName());
            map.put("prevServer", event.getPreviousServer().getServerInfo().getName());
            map.put("newServer", event.getOriginalServer().getServerInfo().getName());

            String json = gson.toJson(map);
            redisbungee.sendChannelMessage("emirutilsvelocity:changeserver", json);
        }
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (event.getChannel().equals("emirutilsvelocity:changeserver")) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            Utils.sendToAllWithPermissions(
                "emirutilsvelocity.notifications.changeserver",
                "<#2070BB>\uD83D\uDD01 <#3085DD>[<#2070BB>N<#3090DD>] [<#2070BB>↔<#3090DD>] <#1560AA>" + map.get("name") + " <#3090DD>(<#2070BB>" + map.get("prevServer") + " <#1560AA>→ <#2070BB>" + map.get("newServer") + "<#3090DD>) <#3090DD>[<#2070BB>" + map.get("proxyId") + "<#3090DD>]"
            );
        }
    }
}
