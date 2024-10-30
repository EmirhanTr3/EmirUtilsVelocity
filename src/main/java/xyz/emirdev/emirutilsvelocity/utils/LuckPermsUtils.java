package xyz.emirdev.emirutilsvelocity.utils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsUtils {
    public static CompletableFuture<String> getDisplayName(Player player) {
        UserManager userManager = EmirUtilsVelocity.getLuckPerms().getUserManager();
        UUID uuid = player.getUniqueId();
        CompletableFuture<User> userFuture = userManager.loadUser(uuid);
        CompletableFuture<String> future = new CompletableFuture<>();

        userFuture.thenAcceptAsync(user -> {
            String prefix = Objects.requireNonNullElse(user.getCachedData().getMetaData().getPrefix(), "");
            String suffix = Objects.requireNonNullElse(user.getCachedData().getMetaData().getSuffix(), "");
            String displayname = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + player.getUsername() + suffix));

            future.completeAsync(() -> displayname);
        });

        return future;
    }
}
