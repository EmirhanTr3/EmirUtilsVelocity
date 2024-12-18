package xyz.emirdev.emirutilsvelocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

public class NetworkLeaveEvent {

    @Subscribe
    public void onNetworkLeave(DisconnectEvent event) {
        Player player = event.getPlayer();
        
        EmirUtilsVelocity.getProxyUtils().broadcastWithPermission(
                "emirutilsvelocity.notifications.disconnect",
                "<#BB4050>← <#DD6070>[<#BB4050>N<#DD6070>] [<#BB4050>-<#DD6070>] <#AA3545>{0}",
                player.getUsername()
        );
    }
}
