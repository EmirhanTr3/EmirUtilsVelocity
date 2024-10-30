package xyz.emirdev.emirutilsvelocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.utils.RedisBungeeUtils;

public class ChangeServerEvent {

    @Subscribe
    public void onChangeServer(ServerPreConnectEvent event) {
        if (event.getPreviousServer() != null) {
            Player player = event.getPlayer();

            RedisBungeeUtils.broadcastWithPermission(
                    "emirutilsvelocity.notifications.changeserver",
                    "<#2070BB>\uD83D\uDD01 <#3085DD>[<#2070BB>N<#3090DD>] [<#2070BB>↔<#3090DD>] <#1560AA>%s <#3090DD>(<#2070BB>%s <#1560AA>→ <#2070BB>%s<#3090DD>) <#3090DD>[<#2070BB>%s<#3090DD>]",
                    player.getUsername(),
                    event.getPreviousServer().getServerInfo().getName(),
                    event.getOriginalServer().getServerInfo().getName(),
                    EmirUtilsVelocity.getRedisBungee().getProxyId()
            );
        }
    }
}
