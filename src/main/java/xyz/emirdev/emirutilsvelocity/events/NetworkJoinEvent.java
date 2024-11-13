package xyz.emirdev.emirutilsvelocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.redisbungee.RedisBungeeUtils;

public class NetworkJoinEvent {

    @Subscribe
    public void onNetworkJoin(PostLoginEvent event) {
        Player player = event.getPlayer();

        RedisBungeeUtils.broadcastWithPermission(
                "emirutilsvelocity.notifications.connect",
                "<#25BB65>→ <#35EE75>[<#25BB65>N<#35EE75>] [<#25BB65>+<#35EE75>] <#20AA50>%s <#35EE75>[<#25BB65>%s<#35EE75>] <#259935>(%s %s)",
                player.getUsername(),
                EmirUtilsVelocity.getRedisBungee().getProxyId(),
                player.getClientBrand(),
                player.getProtocolVersion().getVersionIntroducedIn()
        );
    }
}
