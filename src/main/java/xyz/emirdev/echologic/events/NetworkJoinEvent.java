package xyz.emirdev.echologic.events;

import java.util.Objects;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;

public class NetworkJoinEvent {

    @Subscribe
    public void onNetworkJoin(PostLoginEvent event) {
        Player player = event.getPlayer();

        EchoLogic.getProxyUtils().broadcastWithPermission("echologic.notifications.connect",
                EchoLogic.hasRedisBungee()
                        ? "<#25BB65>\u2192 <#35EE75>[<#25BB65>N<#35EE75>] [<#25BB65>+<#35EE75>] <#20AA50><player> <#35EE75>[<#25BB65><proxy><#35EE75>] <#259935>(<client> <version>)"
                        : "<#25BB65>\u2192 <#35EE75>[<#25BB65>N<#35EE75>] [<#25BB65>+<#35EE75>] <#20AA50><player> <#259935>(<client> <version>)",
                Placeholder.unparsed("player", player.getUsername()),
                Placeholder.unparsed("proxy",
                        (EchoLogic.hasRedisBungee()
                                ? RedisBungeeAPI.getRedisBungeeApi().getProxyId()
                                : "null")),
                Placeholder.unparsed("client",
                        Objects.requireNonNullElse(player.getClientBrand(), "null")),
                Placeholder.unparsed("version", Objects.requireNonNullElse(
                        player.getProtocolVersion().getVersionIntroducedIn(), "null")));
    }
}
