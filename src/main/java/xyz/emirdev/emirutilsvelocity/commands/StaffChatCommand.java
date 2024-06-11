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
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class StaffChatCommand {

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("staffchat")
                .requires(source -> source.hasPermission("emirutilsvelocity.staffchat"))
                .executes(context -> {
                    context.getSource().sendMessage(Utils.deserialize("<red>You need to specify a message."));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                    .executes(context -> {
                        CommandSource source = context.getSource();
                        String message = context.getArgument("message", String.class);

                        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

                        Gson gson = new Gson();
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("proxyId", redisbungee.getProxyId());
                        map.put("isPlayer", (source instanceof Player));
                        map.put("name", ((source instanceof Player player) ? player.getUsername() : "Console"));
                        map.put("server", ((source instanceof Player player) ? player.getCurrentServer().get().getServerInfo().getName() : null));
                        map.put("message", message);

                        String json = gson.toJson(map);

                        redisbungee.sendChannelMessage("emirutilsvelocity:staffchat", json);

                        return Command.SINGLE_SUCCESS;
                    })
                )
                .build();

        return new BrigadierCommand(node);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (event.getChannel().equals("emirutilsvelocity:staffchat")) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            Component comp;
            if ((Boolean) map.get("isPlayer")) {
                comp = Utils.deserialize("<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>"+ map.get("proxyId") +"<dark_aqua>] <dark_aqua>[<aqua>"+ map.get("server") +"<dark_aqua>] <aqua>"+ map.get("name") +"<aqua>: " + map.get("message"));
            } else {
                comp = Utils.deserialize("<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>" + map.get("proxyId") + "<dark_aqua>] <aqua>Console<aqua>: " + map.get("message"));
            }

            EmirUtilsVelocity.proxy.getConsoleCommandSource().sendMessage(comp);
            for (Player p : EmirUtilsVelocity.proxy.getAllPlayers()) {
                if (p.hasPermission("emirutilsvelocity.staffchat")) {
                    p.sendMessage(comp);
                }
            }
        }
    }
}