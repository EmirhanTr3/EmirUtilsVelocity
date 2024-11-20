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
    public static CompletableFuture<User> getUser(UUID uuid) {
        return EmirUtilsVelocity.getLuckPerms().getUserManager().loadUser(uuid);
    }

    public static CompletableFuture<Boolean> hasPermission(UUID uuid, String permission) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        getUser(uuid).thenAcceptAsync(user ->
            future.completeAsync(() -> user.getCachedData().getPermissionData().checkPermission(permission).asBoolean())
        );

        return future;
    }

    public static CompletableFuture<String> getDisplayName(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();

        getUser(player.getUniqueId()).thenAcceptAsync(user -> {
            String prefix = Objects.requireNonNullElse(user.getCachedData().getMetaData().getPrefix(), "");
            String suffix = Objects.requireNonNullElse(user.getCachedData().getMetaData().getSuffix(), "");
            String displayname = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + player.getUsername() + suffix));

            future.completeAsync(() -> displayname);
        });

        return future;
    }
}
