package xyz.emirdev.emirutilsvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.velocity.annotation.CommandPermission;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.RedisBungeeUtils;
import xyz.emirdev.emirutilsvelocity.Utils;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StaffChatCommand {

    @Command({"staffchat", "sc"})
    @CommandPermission("emirutilsvelocity.staffchat")
    public void staffchat(CommandSource sender, String message) {
        if (sender instanceof Player player) {
            UserManager userManager = EmirUtilsVelocity.getLuckPerms().getUserManager();
            UUID uuid = player.getUniqueId();
            CompletableFuture<User> userFuture = userManager.loadUser(uuid);

            userFuture.thenAcceptAsync(user -> {
                String prefix = user.getCachedData().getMetaData().getPrefix();
                String suffix = user.getCachedData().getMetaData().getSuffix();
                String displayname = Utils.convertComponentToLegacyString(Utils.format(Objects.requireNonNullElse(prefix, "") + player.getUsername() + Objects.requireNonNullElse(suffix, "")));

                RedisBungeeUtils.broadcastWithPermission(
                    "emirutilsvelocity.staffchat",
                    "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>%s<dark_aqua>] <dark_aqua>[<aqua>%s<dark_aqua>] <aqua>%s<aqua>: %s",
                        EmirUtilsVelocity.getRedisBungee().getProxyId(),
                        EmirUtilsVelocity.getRedisBungee().getServerFor(uuid).getName(),
                        displayname,
                        message
                );
            });
        } else {
            RedisBungeeUtils.broadcastWithPermission(
                    "emirutilsvelocity.staffchat",
                    "<dark_aqua>[<aqua>SC<dark_aqua>] <dark_aqua>[<aqua>%s<dark_aqua>] <aqua>Console<aqua>: %s",
                    EmirUtilsVelocity.getRedisBungee().getProxyId(),
                    message
            );
        }
    }
}