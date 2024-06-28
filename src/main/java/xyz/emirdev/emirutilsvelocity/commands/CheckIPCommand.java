package xyz.emirdev.emirutilsvelocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.emirdev.emirutilsvelocity.IPData;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.Objects;

public final class CheckIPCommand {
    public static String name = "checkip";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("checkip")
            .requires(source -> source.hasPermission("emirutilsvelocity.checkip"))
            .executes(context -> {
                context.getSource().sendMessage(Utils.deserialize("<red>You need to specify an ip address."));
                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("ip", StringArgumentType.greedyString())
                .suggests((ctx, builder) -> {
                    RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

                    redisbungee.getAllProxies().forEach(rproxy ->
                        redisbungee.getPlayersOnProxy(rproxy).forEach(uuid ->
                            builder.suggest(
                                redisbungee.getPlayerIp(uuid).getHostAddress(),
                                VelocityBrigadierMessage.tooltip(
                                    Utils.deserialize(redisbungee.getNameFromUuid(uuid))
                                )
                            )
                        )
                    );

                    return builder.buildFuture();
                })
                .executes(context -> {
                    String ip = context.getArgument("ip", String.class);
                    IPData data = Utils.getIPData(ip);

                    if (data.getStatus().equals("ok") || data.getStatus().equals("warning")) {
                        String riskText = data.getRiskName().equals("Very Risky") || data.getRiskName().equals("Risky") ?
                            "<red>" + data.getRisk() + " (" + data.getRiskName() + ")</red>" :
                            "<green>" + data.getRisk() + " (" + data.getRiskName() + ")</green>";

                        context.getSource().sendMessage(Utils.deserialize(
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
                            ) +
                            "<gray>-</gray> <#25BB65>IP:</#25BB65> <#35EE75>" + data.getIp() + "</#35EE75>\n" +
                            "<gray>-</gray> <#25BB65>Provider:</#25BB65> <#35EE75>" + (Objects.requireNonNullElse(data.getProvider(), data.getOrganisation())) + "\n" +
                            "<gray>-</gray> <#25BB65>Location:</#25BB65> <#35EE75>" + data.getLocation() + "\n" +
                            "<gray>-</gray> <#25BB65>Type:</#25BB65> <#35EE75>" + data.getType() + "\n" +
                            "<gray>-</gray> <#25BB65>VPN:</#25BB65> <red>" + (data.isVPN() ? "<green>Yes</green>" : "<red>No</red>") + "\n" +
                            "<gray>-</gray> <#25BB65>Proxy:</#25BB65> <green>" + (data.isProxy() ? "<green>Yes</green>" : "<red>No</red>") + "\n" +
                            "<gray>-</gray> <#25BB65>Risk Score:</#25BB65> " + riskText + "\n"
                        ));
                    } else if (data.getStatus().equals("error")){
                        context.getSource().sendMessage(Utils.deserialize(
                            "<#35EE75><b>IP Address Information</b></#35EE75>\n" +
                            "<red>✕ There was an API error.</red>\n" +
                            "<gray>-</gray> <#25BB65>Error:</#25BB65> <#35EE75>" + data.getMessage() + "</#35EE75>\n"
                        ));
                    }

                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();

        return new BrigadierCommand(node);
    }
}