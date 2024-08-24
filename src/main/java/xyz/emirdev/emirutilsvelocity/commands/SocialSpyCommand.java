package xyz.emirdev.emirutilsvelocity.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.PluginData;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.*;

public final class SocialSpyCommand extends xyz.emirdev.emirutilsvelocity.Command {
    public static String name = "socialspy";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("socialspy")
            .requires(source -> source.hasPermission("emirutilsvelocity.socialspy"))
            .executes(context -> {
                if (context.getSource() instanceof Player player) {
                    PluginData data = EmirUtilsVelocity.data;
                    UUID uuid = player.getUniqueId();

                    if (!data.getSocialSpyEnabled(uuid)) {
                        data.enableSocialSpy(uuid);
                        context.getSource().sendMessage(Utils.deserialize("<green>You can <bold>now</bold> see other's messages.</green>"));
                    } else {
                        data.disableSocialSpy(uuid);
                        context.getSource().sendMessage(Utils.deserialize("<red>You can <bold>no longer</bold> see other's messages.</red>"));
                    }
                } else {
                    context.getSource().sendMessage(Utils.deserialize("<red>Console already sort of has socialspy...</red>"));
                }

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(node);
    }

    public static void sendProxyMessage(CommandSource source, String target, String message) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
        UUID targetUUID = redisbungee.getUuidFromName(target);

        if ((source instanceof Player player)) {
            Gson gson = new Gson();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("proxyId", redisbungee.getProxyId());
            map.put("name", player.getUsername());
            map.put("uuid", player.getUniqueId());
            map.put("targetuuid", targetUUID);
            map.put("message", message);

            String json = gson.toJson(map);

            redisbungee.sendChannelMessage("emirutilsvelocity:socialspy", json);
        }
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (event.getChannel().equals("emirutilsvelocity:socialspy")) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            sendMessage(
                UUID.fromString((String) map.get("uuid")),
                UUID.fromString((String) map.get("targetuuid")),
                (String) map.get("message")
            );
        }
    }

    public static void sendMessage(UUID playerUUID, UUID targetUUID, String message) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();
        String playerName = redisbungee.getNameFromUuid(playerUUID);
        String targetName = redisbungee.getNameFromUuid(targetUUID);

        Component comp = Utils.deserialize("<#41BBFF>[<#2595CC>SocialSpy<#41BBFF>] <#2595CC>" + playerName + " <#41BBFF>→ <#2595CC>" + targetName + "<#41BBFF>: <#60CCFF>" + message);

        EmirUtilsVelocity.proxy.getConsoleCommandSource().sendMessage(comp);
        for (Player p : EmirUtilsVelocity.proxy.getAllPlayers()) {
            if (EmirUtilsVelocity.data.getSocialSpyEnabled(p.getUniqueId())) {
                if (p.hasPermission("emirutilsvelocity.socialspy")) {
                    if (!p.getUniqueId().equals(playerUUID) && !p.getUniqueId().equals(targetUUID)) {
                        p.sendMessage(comp);
                    }
                } else {
                    EmirUtilsVelocity.data.disableSocialSpy(p.getUniqueId());
                }
            }
        }
    }
}