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
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class StaffChatCommand extends xyz.emirdev.emirutilsvelocity.Command {
    public static String name = "staffchat";
    public static List<String> aliases = List.of("sc");

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("staffchat")
            .requires(source -> source.hasPermission("emirutilsvelocity.staffchat"))
            .executes(context -> {
                if (context.getSource() instanceof Player player) {
                    if (EmirUtilsVelocity.staffChatToggledPlayers.contains(player.getUniqueId())) {
                        EmirUtilsVelocity.staffChatToggledPlayers.remove(player.getUniqueId());
                        player.sendMessage(Utils.deserialize("<green>You are <bold>no longer</bold> chatting in staff chat.</green>"));
                    } else {
                        EmirUtilsVelocity.staffChatToggledPlayers.add(player.getUniqueId());
                        player.sendMessage(Utils.deserialize("<green>You are <bold>now</bold> chatting in staff chat.</green>"));
                    }
                } else {
                    context.getSource().sendMessage(Utils.deserialize("<red>You cannot toggle staff chat as console."));
                }
                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                .executes(context -> {
                    CommandSource source = context.getSource();
                    String message = context.getArgument("message", String.class);

                    sendMessage(source, message);

                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();

        return new BrigadierCommand(node);
    }

    private static void sendMessage(CommandSource source, String message) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("proxyId", redisbungee.getProxyId());
        map.put("isPlayer", (source instanceof Player));
        map.put("name", ((source instanceof Player player) ? player.getUsername() : "Console"));
        map.put("uuid", ((source instanceof Player player) ? player.getUniqueId() : null));
        map.put("server", ((source instanceof Player player) ? player.getCurrentServer().get().getServerInfo().getName() : null));
        map.put("message", message);

        String json = gson.toJson(map);

        redisbungee.sendChannelMessage("emirutilsvelocity:staffchat", json);
    }

    @Subscribe
    public void onChatMessage(PlayerChatEvent event) {
        if (EmirUtilsVelocity.staffChatToggledPlayers.contains(event.getPlayer().getUniqueId())) {
            if (event.getPlayer().hasPermission("emirutilsvelocity.staffchat")) {
                event.setResult(PlayerChatEvent.ChatResult.denied());
                sendMessage(event.getPlayer(), event.getMessage());
            } else {
                EmirUtilsVelocity.staffChatToggledPlayers.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (event.getChannel().equals("emirutilsvelocity:staffchat")) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            if ((Boolean) map.get("isPlayer")) {
                LuckPerms luckperms = LuckPermsProvider.get();
                UserManager userManager = luckperms.getUserManager();
                UUID uuid = UUID.fromString((String) map.get("uuid"));
                CompletableFuture<User> userFuture = userManager.loadUser(uuid);

                userFuture.thenAcceptAsync(user -> {
                    String prefix = user.getCachedData().getMetaData().getPrefix();
                    String suffix = user.getCachedData().getMetaData().getSuffix();
                    String displayname = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(Objects.requireNonNullElse(prefix, "") + map.get("name") + Objects.requireNonNullElse(suffix, "")));
                    Component comp = Utils.deserialize("<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>" + map.get("proxyId") + "<dark_aqua>] <dark_aqua>[<aqua>" + map.get("server") + "<dark_aqua>] <aqua>" + displayname + "<aqua>: " + map.get("message"));

                    Utils.sendToAllWithPermissions("emirutilsvelocity.staffchat", comp);
                });
            } else {
                Component comp = Utils.deserialize("<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>" + map.get("proxyId") + "<dark_aqua>] <aqua>Console<aqua>: " + map.get("message"));
                Utils.sendToAllWithPermissions("emirutilsvelocity.staffchat", comp);
            }
        }
    }
}