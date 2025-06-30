package xyz.emirdev.emirutilsvelocity.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Utils {
    public static Component formatMessage(String string, TagResolver... resolvers) {
        return MiniMessage.miniMessage().deserialize(string, resolvers);
    }

    public static String unformatMessage(Component component, TagResolver... resolvers) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static void sendMessage(CommandSource sender, String string, TagResolver... resolvers) {
        sender.sendMessage(Utils.formatMessage(string, resolvers));
    }

    public static void sendError(CommandSource sender, String string, TagResolver... resolvers) {
        sender.sendMessage(Utils.formatMessage("<#ee4444>" + string, resolvers));
    }

    public static void broadcast(String string, TagResolver... resolvers) {
        for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            Utils.sendMessage(player, string, resolvers);
        }
        Utils.sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), string, resolvers);
    }

    public static void broadcastWithPermission(String perm, String string, TagResolver... resolvers) {
        for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            if (!player.hasPermission(perm))
                continue;
            Utils.sendMessage(player, string, resolvers);
        }
        Utils.sendMessage((CommandSource) EmirUtilsVelocity.getProxy().getConsoleCommandSource(), string, resolvers);
    }

    public static String convertComponentToLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static void connectPlayer(Player player, RegisteredServer server) {
        Utils.connectPlayer(player, server, false);
    }

    public static void connectPlayer(Player player, RegisteredServer server, boolean silent) {
        if (!silent) {
            Utils.sendMessage(player,
                    "<#00eeee>Connecting to server <#00ccff><server><#00eeee>...",
                    Placeholder.unparsed("server", server.getServerInfo().getName()));
        }

        player.createConnectionRequest(server).connect().thenAcceptAsync(action -> {
            if (!action.isSuccessful()) {
                if (action.getStatus() == ConnectionRequestBuilder.Status.ALREADY_CONNECTED) {
                    Utils.sendError(player, "You are already connected to this server.");
                    return;
                }

                if (action.getStatus() == ConnectionRequestBuilder.Status.CONNECTION_IN_PROGRESS) {
                    Utils.sendError(player, "You are already connecting to this server.");
                    return;
                }

                Component message = Utils.formatMessage(
                        "<#ee4444>Unable to connect to <server>. ",
                        Placeholder.unparsed("server", server.getServerInfo().getName()));

                Optional<Component> reason = action.getReasonComponent();
                if (reason.isPresent()) {
                    message = message.append(reason.get());
                }

                player.sendMessage(message);
            }
        });
    }

    public static CompletableFuture<IPData> getIPData(String ip) {
        return CompletableFuture.supplyAsync(() -> {
            URI uri = null;
            boolean isUsingKey = false;

            try {
                String key = EmirUtilsVelocity.getConfig().getIPCheckKey();
                if (key != null) {
                    isUsingKey = true;
                    uri = new URI("https://proxycheck.io/v2/" + ip + "?vpn=3&asn=1&risk=1&short=1&key=" + key);
                } else {
                    uri = new URI("https://proxycheck.io/v2/" + ip + "?vpn=3&asn=1&risk=1&short=1");
                }
            } catch (URISyntaxException ignored) {
            }

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            try {
                HttpResponse<String> cli = HttpClient.newBuilder()
                        .build()
                        .send(req, HttpResponse.BodyHandlers.ofString());

                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(cli.body(), new TypeToken<>() {
                });
                map.put("isUsingKey", isUsingKey);

                return new IPData(map);

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static class IPData {
        private final boolean isUsingKey;
        private final String status;
        private final String message;
        private final String ip;
        private final String provider;
        private final String organisation;
        private final String city;
        private final String region;
        private final String country;
        private final double latitude;
        private final double longitude;
        private final String type;
        private final boolean isVPN;
        private final boolean isProxy;
        private final double risk;
        private final String riskName;

        public IPData(Map<String, Object> map) {
            this.isUsingKey = (boolean) map.get("isUsingKey");
            this.status = (String) map.get("status");
            this.message = (String) map.get("message");
            this.ip = (String) map.get("ip");
            this.provider = (String) map.get("provider");
            this.organisation = (String) map.get("organisation");
            this.city = (String) map.get("city");
            this.region = (String) map.get("region");
            this.country = (String) map.get("country");
            this.latitude = (double) map.getOrDefault("latitude", 0D);
            this.longitude = (double) map.getOrDefault("longitude", 0D);
            this.type = (String) map.get("type");
            this.isVPN = map.getOrDefault("vpn", "no").equals("yes");
            this.isProxy = map.getOrDefault("proxy", "no").equals("yes");
            this.risk = (double) map.getOrDefault("risk", -1D);
            this.riskName = this.risk >= 66 ? "Very Risky" : this.risk >= 33 ? "Risky" : "Safe";
        }

        public boolean isUsingKey() {
            return this.isUsingKey;
        }

        public String getStatus() {
            return this.status;
        }

        public String getMessage() {
            return this.message;
        }

        public String getIp() {
            return this.ip;
        }

        public String getProvider() {
            return this.provider;
        }

        public String getOrganisation() {
            return this.organisation;
        }

        public String getLocation() {
            return String.format("%s, %s, %s (%s, %s)", this.city, this.region, this.country, latitude, longitude);
        }

        public String getType() {
            return this.type;
        }

        public boolean isVPN() {
            return this.isVPN;
        }

        public boolean isProxy() {
            return this.isProxy;
        }

        public double getRisk() {
            return this.risk;
        }

        public String getRiskName() {
            return this.riskName;
        }
    }
}
