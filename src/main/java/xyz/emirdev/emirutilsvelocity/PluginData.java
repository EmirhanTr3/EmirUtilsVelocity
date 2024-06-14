package xyz.emirdev.emirutilsvelocity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Configuration
public class PluginData {
    private Map<UUID, List<UUID>> ignoredPlayers = new HashMap<>();

    public void save() {
        Path dataPath = Paths.get("plugins/emirutilsvelocity/data.yml");
        YamlConfigurations.save(
                dataPath,
                PluginData.class,
                this
        );

        EmirUtilsVelocity.data = YamlConfigurations.load(dataPath, PluginData.class);
    }

    public Map<UUID, List<UUID>> getIgnoredPlayers() {
        return this.ignoredPlayers;
    }

    public List<UUID> getIgnoredPlayers(UUID uuid) {
        List<UUID> list = this.ignoredPlayers.get(uuid);
        if (list != null) {
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    public void addIgnoredPlayer(UUID playerUUID, UUID targetUUID) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("playerUUID", playerUUID);
        map.put("targetUUID", targetUUID);

        String json = gson.toJson(map);

        redisbungee.sendChannelMessage("emirutilsvelocity:addignored", json);
    }

    public void removeIgnoredPlayer(UUID playerUUID, UUID targetUUID) {
        RedisBungeeAPI redisbungee = RedisBungeeAPI.getRedisBungeeApi();

        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("playerUUID", playerUUID);
        map.put("targetUUID", targetUUID);

        String json = gson.toJson(map);

        redisbungee.sendChannelMessage("emirutilsvelocity:removeignored", json);
    }

    @Subscribe
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        if (
            event.getChannel().equals("emirutilsvelocity:addignored") ||
            event.getChannel().equals("emirutilsvelocity:removeignored")
        ) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(event.getMessage(), new TypeToken<Map<String, Object>>(){});

            UUID playerUUID = UUID.fromString((String) map.get("playerUUID"));
            UUID targetUUID = UUID.fromString((String) map.get("targetUUID"));

            List<UUID> list = this.getIgnoredPlayers(playerUUID);
            if (event.getChannel().equals("emirutilsvelocity:addignored")) {
                if (!list.contains(targetUUID))
                    list.add(targetUUID);
            } else if (event.getChannel().equals("emirutilsvelocity:removeignored")) {
                list.remove(targetUUID);
            }
            this.ignoredPlayers.put(playerUUID, list);
            this.save();
        }
    }
}