package xyz.emirdev.emirutilsvelocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils {
    public static Component getPrefix() {
        return format("<gradient:#00eeaa:#00aaaa><bold>EmirUtilsVelocity<reset> <dark_gray>» ");
    }

    public static Component format(String string, Object... args) {
        return MiniMessage.miniMessage().deserialize(String.format(string, args));
    }

    public static void sendMessage(CommandSource sender, String string, Object... args) {
        sender.sendMessage(getPrefix().append(format(string, args)));
    }

    public static void sendError(CommandSource sender, String string, Object... args) {
        sender.sendMessage(getPrefix().append(format("<#ee4444>" + string, args)));
    }

    public static void broadcast(String string, Object... args) {
        for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            sendMessage(player, string, args);
        }
        sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), string, args);
    }

    public static void broadcastWithPermission(String perm, String string, Object... args) {
        for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            if (player.hasPermission(perm)) {
                sendMessage(player, string, args);
            }
        }
        sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), string, args);
    }

    public static String convertComponentToLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
