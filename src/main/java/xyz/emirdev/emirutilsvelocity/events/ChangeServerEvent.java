package xyz.emirdev.emirutilsvelocity.events;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

public class ChangeServerEvent {

    @Subscribe
    public void onChangeServer(ServerPreConnectEvent event) {
        if (event.getPreviousServer() != null) {
            Player player = event.getPlayer();

            EmirUtilsVelocity.getProxyUtils().broadcastWithPermission(
                    "emirutilsvelocity.notifications.changeserver",
                    EmirUtilsVelocity.hasRedisBungee() ?
                            "<#2070BB>\uD83D\uDD01 <#3085DD>[<#2070BB>N<#3090DD>] [<#2070BB>↔<#3090DD>] <#1560AA>{0} <#3090DD>(<#2070BB>{1} <#1560AA>→ <#2070BB>{2}<#3090DD>) <#3090DD>[<#2070BB>{3}<#3090DD>]" :
                            "<#2070BB>\uD83D\uDD01 <#3085DD>[<#2070BB>N<#3090DD>] [<#2070BB>↔<#3090DD>] <#1560AA>{0} <#3090DD>(<#2070BB>{1} <#1560AA>→ <#2070BB>{2}<#3090DD>)",
                    player.getUsername(),
                    event.getPreviousServer().getServerInfo().getName(),
                    event.getOriginalServer().getServerInfo().getName(),
                    EmirUtilsVelocity.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId() : null
            );
        }
    }
}
