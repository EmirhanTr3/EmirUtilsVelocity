package xyz.emirdev.echologic.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import xyz.emirdev.echologic.commands.OwnerChatCommand;
import xyz.emirdev.echologic.commands.StaffChatCommand;

public class ChatEvent {

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if (OwnerChatCommand.toggledPlayers.contains(player.getUniqueId())) {
            if (!player.hasPermission("echologic.ownerchat")) {
                OwnerChatCommand.toggledPlayers.remove(player.getUniqueId());
            } else {
                OwnerChatCommand.sendOwnerChatMessage(player, event.getMessage());
                event.setResult(PlayerChatEvent.ChatResult.denied());
            }
        }
        else if (StaffChatCommand.toggledPlayers.contains(player.getUniqueId())) {
            if (!player.hasPermission("echologic.staffchat")) {
                StaffChatCommand.toggledPlayers.remove(player.getUniqueId());
            } else {
                StaffChatCommand.sendStaffChatMessage(player, event.getMessage());
                event.setResult(PlayerChatEvent.ChatResult.denied());
            }
        }
    }
}
