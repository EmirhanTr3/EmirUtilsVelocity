package xyz.emirdev.emirutilsvelocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisBungeeUtils;

public class NetworkLeaveEvent {

    @Subscribe
    public void onNetworkLeave(DisconnectEvent event) {
        Player player = event.getPlayer();
        
        RedisBungeeUtils.broadcastWithPermission(
                "emirutilsvelocity.notifications.disconnect",
                "<#BB4050>← <#DD6070>[<#BB4050>N<#DD6070>] [<#BB4050>-<#DD6070>] <#AA3545>%s",
                player.getUsername()
        );
    }
}
