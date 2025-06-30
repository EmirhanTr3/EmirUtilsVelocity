package xyz.emirdev.emirutilsvelocity.events;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

public class ChangeServerEvent {

    @Subscribe
    public void onChangeServer(ServerPreConnectEvent event) {
        if (event.getPreviousServer() != null) {
            Player player = event.getPlayer();

            EmirUtilsVelocity.getProxyUtils().broadcastWithPermission(
                    "emirutilsvelocity.notifications.changeserver",
                    EmirUtilsVelocity.hasRedisBungee()
                            ? "<#2070BB>\ud83d\udd01 <#3085DD>[<#2070BB>N<#3090DD>] [<#2070BB>\u2194<#3090DD>] <#1560AA><player> <#3090DD>(<#2070BB><prevserver> <#1560AA>\u2192 <#2070BB><newserver><#3090DD>) <#3090DD>[<#2070BB><proxy><#3090DD>]"
                            : "<#2070BB>\ud83d\udd01 <#3085DD>[<#2070BB>N<#3090DD>] [<#2070BB>\u2194<#3090DD>] <#1560AA><player> <#3090DD>(<#2070BB><prevserer> <#1560AA>\u2192 <#2070BB><newserver><#3090DD>)",
                    Placeholder.unparsed("player", player.getUsername()),
                    Placeholder.unparsed("prevserver", event.getPreviousServer().getServerInfo().getName()),
                    Placeholder.unparsed("newserver", event.getOriginalServer().getServerInfo().getName()),
                    Placeholder.unparsed("proxy",
                            EmirUtilsVelocity.hasRedisBungee() ? RedisBungeeAPI.getRedisBungeeApi().getProxyId()
                                    : "null"));
        }
    }
}
