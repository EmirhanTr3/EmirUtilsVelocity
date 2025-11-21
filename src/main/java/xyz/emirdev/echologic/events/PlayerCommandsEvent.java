package xyz.emirdev.echologic.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import org.spongepowered.configurate.serialize.SerializationException;
import xyz.emirdev.echologic.EchoLogic;

import java.util.ArrayList;
import java.util.List;

public class PlayerCommandsEvent {
    private final List<String> commands;

    public PlayerCommandsEvent() throws SerializationException {
        this.commands = EchoLogic.getConfig().getRoot().node("hidecommands", "commands").getList(String.class);
    }

    @Subscribe
    public void playerCommandsEvent(PlayerAvailableCommandsEvent event) {
        if (!EchoLogic.getConfig().getRoot().node("hidecommands", "enabled").getBoolean()) return;

        List<String> commandsToRemove = new ArrayList<>();

        event.getRootNode().getChildren().forEach(commandNode -> {
            for (String command : commands) {
                if (commandNode.getName().matches(command)) {
                    commandsToRemove.add(commandNode.getName());
                }
            }
        });

        for (String command : commandsToRemove) {
            event.getRootNode().removeChildByName(command);
        }
    }
}
