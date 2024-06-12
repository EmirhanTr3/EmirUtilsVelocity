package xyz.emirdev.emirutilsvelocity;

import de.exlll.configlib.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
public class PluginData {
    private Map<UUID, List<UUID>> ignoredPlayers = new HashMap<>();

    public Map<UUID, List<UUID>> getIgnoredPlayers() {
        return this.ignoredPlayers;
    }

    public List<UUID> getIgnoredPlayers(UUID uuid) {
        return this.ignoredPlayers.get(uuid);
    }
}
