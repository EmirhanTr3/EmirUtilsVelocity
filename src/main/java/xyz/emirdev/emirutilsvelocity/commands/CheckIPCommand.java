package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.utils.Utils;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CheckIPCommand {

    @Command("checkip")
    @CommandPermission("emirutilsvelocity.checkip")
    public void checkip(CommandSource sender, InetSocketAddress ip) {
        CompletableFuture<Utils.IPData> dataFuture = Utils.getIPData(ip.getHostName());

        dataFuture.thenAcceptAsync(data -> {
            if (data.getStatus().equals("ok") || data.getStatus().equals("warning")) {
                Utils.sendMessage(sender,
                        "<#35EE75><b>IP Address Information</b></#35EE75>\n" +
                                (!data.isUsingKey()
                                        ? "<yellow>WARNING: You have no proxycheck.io token defined in config. Please change it.</yellow>\n"
                                        : "")
                                +
                                (data.getStatus().equals("warning") ? "<gold>⚠ There was an API warning.</gold>\n"
                                        : "<green>✓ There are no API errors.</green>\n")
                                +
                                (data.getStatus().equals("warning")
                                        ? "<gray>-</gray> <#25BB65>Warning:</#25BB65><message><#35EE75>\n"
                                        : "")
                                + """
                                        <gray>-</gray> <#25BB65>IP:</#25BB65> <#35EE75><ip></#35EE75>
                                        <gray>-</gray> <#25BB65>Provider:</#25BB65> <#35EE75><provider></#35EE75>
                                        <gray>-</gray> <#25BB65>Location:</#25BB65> <#35EE75><location></#35EE75>
                                        <gray>-</gray> <#25BB65>Type:</#25BB65> <#35EE75><type></#35EE75>
                                        <gray>-</gray> <#25BB65>VPN:</#25BB65> <vpn>
                                        <gray>-</gray> <#25BB65>Proxy:</#25BB65> <proxy>
                                        <gray>-</gray> <#25BB65>Risk Score:</#25BB65> <risk_score>""",
                        Placeholder.unparsed("ip", data.getIp()),
                        Placeholder.unparsed("provider",
                                Objects.requireNonNullElse(data.getProvider(), data.getOrganisation())),
                        Placeholder.unparsed("location", data.getLocation()),
                        Placeholder.unparsed("type", data.getType()),
                        Placeholder.parsed("vpn", data.isVPN() ? "<green>Yes</green>" : "<red>No</red>"),
                        Placeholder.parsed("proxy", data.isProxy() ? "<green>Yes</green>" : "<red>No</red>"),
                        Placeholder.component("risk_score", Utils.formatMessage(
                                data.getRiskName().equals("Very Risky") || data.getRiskName().equals("Risky")
                                        ? "<red><risk> (<name>)</red>"
                                        : "<green><risk> (<name>)</green>",
                                Placeholder.unparsed("risk", String.valueOf(data.getRisk())),
                                Placeholder.unparsed("name", data.getRiskName()))),
                        Placeholder.unparsed("message", data.getMessage()));
            } else if (data.getStatus().equals("error")) {
                Utils.sendMessage(sender, """
                        <#35EE75><b>IP Address Information</b></#35EE75>
                        <red>✕ There was an API error.</red>
                        <gray>-</gray> <#25BB65>Error:</#25BB65> <#35EE75><message></#35EE75>""",
                        Placeholder.unparsed("message", data.getMessage()));
            }
        });

    }
}
