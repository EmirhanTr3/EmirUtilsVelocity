package xyz.emirdev.echologic.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import com.velocitypowered.api.command.VelocityBrigadierMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.arguments.InetAddressArgumentType;
import xyz.emirdev.echologic.utils.Utils;

import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CheckIPCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("checkip")
                .requires(sender -> sender.hasPermission("echologic.checkip")
                        && EchoLogic.getConfig().getRoot().node("ipcheck", "enabled").getBoolean())
                .then(requiredCustomArgumentBuilder("ip", new InetAddressArgumentType())
                        .executes(this::execute)
                        .suggests(this::suggest))
                .build();
    }

    public CompletableFuture<Suggestions> suggest(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        if (EchoLogic.hasRedisBungee()) {
            RedisBungeeAPI redisBungee = RedisBungeeAPI.getRedisBungeeApi();
            redisBungee.getPlayersOnline()
                    .forEach(uuid -> builder.suggest(
                            redisBungee.getPlayerIp(uuid).getHostAddress(),
                            VelocityBrigadierMessage.tooltip(Component.text(redisBungee.getNameFromUuid(uuid)))
                    ));
        } else {
            EchoLogic.getProxy().getAllPlayers()
                    .forEach(player -> builder.suggest(
                            player.getRemoteAddress().getHostName(),
                            VelocityBrigadierMessage.tooltip(Component.text(player.getUsername()))
                    ));
        }

        return builder.buildFuture();
    }

    public int execute(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        InetAddress ip = getCustomArgument(ctx, "ip", InetAddressArgumentType.class);

        CompletableFuture<Utils.IPData> dataFuture = Utils.getIPData(ip.getHostName());

        dataFuture.thenAcceptAsync(data -> {
            if (data.getStatus().equals("ok") || data.getStatus().equals("warning")) {
                Utils.sendMessage(ctx.getSource(),
                        "<#35EE75><b>IP Address Information</b></#35EE75>\n" +
                                (!data.isUsingKey()
                                        ? "<yellow>WARNING: You have no proxycheck.io token defined in config. Please change it.</yellow>\n"
                                        : "")
                                +
                                (data.getStatus().equals("warning")
                                        ? "<gold>⚠ There was an API warning.</gold>\n"
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
                                Objects.requireNonNullElse(data.getProvider(),
                                        data.getOrganisation())),
                        Placeholder.unparsed("location", data.getLocation()),
                        Placeholder.unparsed("type", data.getType()),
                        Placeholder.parsed("vpn",
                                data.isVPN() ? "<green>Yes</green>" : "<red>No</red>"),
                        Placeholder.parsed("proxy",
                                data.isProxy() ? "<green>Yes</green>"
                                        : "<red>No</red>"),
                        Placeholder.component("risk_score", Utils.formatMessage(
                                data.getRiskName().equals("Very Risky")
                                        || data.getRiskName().equals("Risky")
                                                ? "<red><risk> (<name>)</red>"
                                                : "<green><risk> (<name>)</green>",
                                Placeholder.unparsed("risk",
                                        String.valueOf(data.getRisk())),
                                Placeholder.unparsed("name", data.getRiskName()))),
                        Placeholder.unparsed("message", data.getMessage()));
            } else if (data.getStatus().equals("error")) {
                Utils.sendMessage(ctx.getSource(),
                        """
                                <#35EE75><b>IP Address Information</b></#35EE75>
                                <red>✕ There was an API error.</red>
                                <gray>-</gray> <#25BB65>Error:</#25BB65> <#35EE75><message></#35EE75>""",
                        Placeholder.unparsed("message", data.getMessage()));
            }
        });

        return 1;
    }
}
