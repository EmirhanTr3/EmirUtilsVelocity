package xyz.emirdev.emirutilsvelocity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Utils {
    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static void sendToAllWithPermissions(String perm, Component comp) {
        EmirUtilsVelocity.proxy.getConsoleCommandSource().sendMessage(comp);
        for (Player p : EmirUtilsVelocity.proxy.getAllPlayers()) {
            if (p.hasPermission(perm)) {
                p.sendMessage(comp);
            }
        }
    }

    public static void sendToAllWithPermissions(String perm, String text) {
        sendToAllWithPermissions(perm, deserialize(text));
    }

    public static IPData getIPData(String ip) {
        URI uri = null;
        boolean isUsingKey = false;
        try {
            String key = EmirUtilsVelocity.config.getIpCheckConfig().getToken();
            if (key != null && !key.equals("none")) {
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
            Map<String, Object> map = gson.fromJson(cli.body(), new TypeToken<Map<String, Object>>(){});
            map.put("isUsingKey", isUsingKey);

            return new IPData(map);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}