package xyz.emirdev.echologic.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;

public class NetworkLeaveEvent {

    @Subscribe
    public void onNetworkLeave(DisconnectEvent event) {
        Player player = event.getPlayer();

        EchoLogic.getProxyUtils().broadcastWithPermission(
                "echologic.notifications.disconnect",
                "<#BB4050>← <#DD6070>[<#BB4050>N<#DD6070>] [<#BB4050>-<#DD6070>] <#AA3545><player>",
                Placeholder.unparsed("player", player.getUsername()));
    }
}
