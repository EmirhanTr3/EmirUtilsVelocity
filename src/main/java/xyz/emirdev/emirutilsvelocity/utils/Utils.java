package xyz.emirdev.emirutilsvelocity.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Utils {
    public static Component getPrefix() {
        return format("<gradient:#00eeaa:#00aaaa><bold>EmirUtilsVelocity<reset> <dark_gray>» ");
    }

    /**
     *
     * @param string string to format
     * @param args arguments that will be replaced in the string. argument format: {0}
     * @return formatted string
     */
    public static String stringFormat(String string, Object... args) {
        for (int i = 0; i < args.length; i++) {
            string = string.replaceFirst(
                    "\\{"+ i + "}",
                    Objects.requireNonNullElse(
                            args[i],
                            "null"
                    )
                            .toString()
                            .replaceAll("([$\\\\])", "\\$1")
            );
        }
        return string;
    }

    public static Component format(String string, Object... args) {
        return MiniMessage.miniMessage().deserialize(stringFormat(string, args));
    }

    public static void sendMessage(CommandSource sender, String string, Object... args) {
        sender.sendMessage(format(string, args));
    }

    public static void sendError(CommandSource sender, String string, Object... args) {
        sender.sendMessage(format("<#ee4444>" + string, args));
    }

    public static void broadcast(String string, Object... args) {
        for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            sendMessage(player, string, args);
        }
        sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), string, args);
    }

    public static void broadcastWithPermission(String perm, String string, Object... args) {
        for (Player player : EmirUtilsVelocity.getProxy().getAllPlayers()) {
            if (player.hasPermission(perm)) {
                sendMessage(player, string, args);
            }
        }
        sendMessage(EmirUtilsVelocity.getProxy().getConsoleCommandSource(), string, args);
    }

    public static String convertComponentToLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static void connectPlayer(Player player, RegisteredServer server) {
        connectPlayer(player, server, false);
    }

    public static void connectPlayer(Player player, RegisteredServer server, boolean silent) {
        if (!silent) {
            Utils.sendMessage(player,
                    "<#00eeee>Connecting to server <#00ccff>{0}<#00eeee>...",
                    server.getServerInfo().getName()
            );
        }

        CompletableFuture<ConnectionRequestBuilder.Result> request = player.createConnectionRequest(server).connect();

        request.thenAcceptAsync(action -> {
            if (!action.isSuccessful()) {
                if (action.getStatus() == ConnectionRequestBuilder.Status.ALREADY_CONNECTED) {
                    Utils.sendError(player, "You are already connected to this server.");
                    return;

                } else if (action.getStatus() == ConnectionRequestBuilder.Status.CONNECTION_IN_PROGRESS) {
                    Utils.sendError(player, "You are already connecting to this server.");
                    return;
                }

                Component message = Utils.format(
                        "<#ee4444>Unable to connect to {0}. ",
                        server.getServerInfo().getName()
                );
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
                    uri = new URI("https://proxycheck.io/v2/"+ip+"?vpn=3&asn=1&risk=1&short=1&key="+key);
                } else {
                    uri = new URI("https://proxycheck.io/v2/"+ip+"?vpn=3&asn=1&risk=1&short=1");
                }
            } catch (URISyntaxException ignored) {}

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            try {
                HttpResponse<String> cli = HttpClient.newBuilder()
                        .build()
                        .send(req, HttpResponse.BodyHandlers.ofString());

                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(cli.body(), new TypeToken<>(){});
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
