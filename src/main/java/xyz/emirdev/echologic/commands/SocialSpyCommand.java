package xyz.emirdev.echologic.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.PluginCommand;
import xyz.emirdev.echologic.Database;
import xyz.emirdev.echologic.utils.Utils;

public class SocialSpyCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSource> getCommand() {
        return BrigadierCommand.literalArgumentBuilder("socialspy")
                .requires(hasPermission("echologic.socialspy"))
                .executes(this::execute)
                .build();
    }

    public int execute(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        Database.PlayerData playerData = EchoLogic.getDatabase().getPlayerData(player.getUniqueId());

        if (!playerData.hasSocialSpy()) {
            EchoLogic.getDatabase().updateSocialSpy(playerData.getUniqueId(), true);
            Utils.sendMessage(player, "<green>You can <bold>now</bold> see other's messages.</green>");
        } else {
            EchoLogic.getDatabase().updateSocialSpy(playerData.getUniqueId(), false);
            Utils.sendMessage(player, "<red>You can <bold>no longer</bold> see other's messages.</red>");
        }

        return 1;
    }
}
