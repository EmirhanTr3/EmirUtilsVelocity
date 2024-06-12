package xyz.emirdev.emirutilsvelocity;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {
    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static void sendToAllWithPermissions(String perm, Component comp) {
        EmirUtilsVelocity.proxy.getConsoleCommandSource().sendMessage(comp);
        for (Player p : EmirUtilsVelocity.proxy.getAllPlayers()) {
            if (p.hasPermission(perm)) {
                p.sendMessage(comp);
            }
        }
    }

    public static void sendToAllWithPermissions(String perm, String text) {
        sendToAllWithPermissions(perm, deserialize(text));
    }
}