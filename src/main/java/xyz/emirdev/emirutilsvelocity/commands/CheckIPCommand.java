package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
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
                        (!data.isUsingKey() ?
                                "<yellow>WARNING: You have no proxycheck.io token defined in config. Please change it.</yellow>\n" : ""
                        ) +
                        (data.getStatus().equals("warning") ?
                                "<gold>⚠ There was an API warning.</gold>\n" :
                                "<green>✓ There are no API errors.</green>\n"
                        ) +
                        (data.getStatus().equals("warning") ?
                                "<gray>-</gray> <#25BB65>Warning:</#25BB65> <#35EE75>" + data.getMessage() + "\n" : ""
                        ) + """
                        <gray>-</gray> <#25BB65>IP:</#25BB65> <#35EE75>{0}</#35EE75>
                        <gray>-</gray> <#25BB65>Provider:</#25BB65> <#35EE75>{1}</#35EE75>
                        <gray>-</gray> <#25BB65>Location:</#25BB65> <#35EE75>{2}</#35EE75>
                        <gray>-</gray> <#25BB65>Type:</#25BB65> <#35EE75>{3}</#35EE75>
                        <gray>-</gray> <#25BB65>VPN:</#25BB65> {4}
                        <gray>-</gray> <#25BB65>Proxy:</#25BB65> {5}
                        <gray>-</gray> <#25BB65>Risk Score:</#25BB65> {6}""",
                        data.getIp(),
                        Objects.requireNonNullElse(data.getProvider(), data.getOrganisation()),
                        data.getLocation(),
                        data.getType(),
                        data.isVPN() ? "<green>Yes</green>" : "<red>No</red>",
                        data.isProxy() ? "<green>Yes</green>" : "<red>No</red>",
                        Utils.stringFormat(
                                data.getRiskName().equals("Very Risky") || data.getRiskName().equals("Risky") ?
                                        "<red>{0} ({1})</red>" :
                                        "<green>{0} ({1})</green>",
                                data.getRisk(),
                                data.getRiskName()
                        )
                );
            } else if (data.getStatus().equals("error")){
                Utils.sendMessage(sender, """
                        <#35EE75><b>IP Address Information</b></#35EE75>
                        <red>✕ There was an API error.</red>
                        <gray>-</gray> <#25BB65>Error:</#25BB65> <#35EE75>{0}</#35EE75>""",
                        data.getMessage()
                );
            }
        });

    }
}