package xyz.emirdev.echologic.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.emirdev.echologic.EchoLogic;

import java.time.Duration;
import java.util.*;

public class BackendIntegrationManager {
    private final Map<UUID, RegisteredServer> lastServer = new HashMap<>();

    public BackendIntegrationManager() {
        EchoLogic.getProxy().getScheduler()
                .buildTask(EchoLogic.get(), this::updateServerStatuses)
                .repeat(Duration.ofSeconds(10))
                .schedule();

        EchoLogic.getProxy().getScheduler()
                .buildTask(EchoLogic.get(), () -> {
                    EchoLogic.getProxy().getAllServers().forEach(this::updateServerPlayerCount);
                })
                .repeat(Duration.ofSeconds(10))
                .schedule();
    }

    public void updateServerStatuses() {
        EchoLogic.getProxy().getAllServers().forEach(server -> {
            server.ping()
                    .thenAcceptAsync(ping -> {
                        updateServerStatus(server, true);
                    })
                    .exceptionallyAsync(err -> {
                        updateServerStatus(server,  false);
                        return null;
                    });
        });
    }

    public void updateServerStatus(RegisteredServer server, boolean online) {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();
        data.writeUTF(server.getServerInfo().getName());
        data.writeBoolean(online);

        EchoLogic.getProxy().getAllServers().forEach(s -> s.sendPluginMessage(
                MinecraftChannelIdentifier.create("echologic", "serverstatus"),
                data.toByteArray()
        ));
    }

    @Subscribe
    public void onChangeServer(ServerConnectedEvent event) {
        if (event.getPreviousServer().isPresent()) {
            updateServerPlayerCount(event.getPreviousServer().get());
        }
        updateServerPlayerCount(event.getServer());
        lastServer.put(event.getPlayer().getUniqueId(), event.getServer());
    }

    @Subscribe
    public void onNetworkLeave(DisconnectEvent event) {
        if (lastServer.containsKey(event.getPlayer().getUniqueId())) {
            updateServerPlayerCount(lastServer.get(event.getPlayer().getUniqueId()));
            lastServer.remove(event.getPlayer().getUniqueId());
        } else {
            EchoLogic.getProxy().getAllServers().forEach(this::updateServerPlayerCount);
        }
    }

    public void updateServerPlayerCount(RegisteredServer server) {
        updateServerPlayerCount(
                server,
                EchoLogic.hasRedisBungee() ?
                        RedisBungeeAPI.getRedisBungeeApi().getPlayersOnServer(server.getServerInfo().getName()).size() :
                        EchoLogic.getProxy().getPlayerCount()
        );
    }

    public void updateServerPlayerCount(RegisteredServer server, int count) {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();
        data.writeUTF(server.getServerInfo().getName());
        data.writeInt(count);

        EchoLogic.getProxy().getAllServers().forEach(s -> s.sendPluginMessage(
                MinecraftChannelIdentifier.create("echologic", "serverplayercount"),
                data.toByteArray()
        ));
    }
}
