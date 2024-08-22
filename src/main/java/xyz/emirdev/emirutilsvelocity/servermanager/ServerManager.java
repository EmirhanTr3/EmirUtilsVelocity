package xyz.emirdev.emirutilsvelocity.servermanager;

import com.velocitypowered.api.proxy.server.ServerInfo;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServerManager {
    Map<String, Server> servers = new HashMap<>();

    public Map<String, Server> getServers() {
        return servers;
    }

    public Server getServer(String name) {
        return servers.get(name);
    }

    public Server createServer(String name, int port, String jar, int ram) {
        return this.createServer(name, port, jar, ram, "");
    }

    public Server createServer(String name, int port, String jar, int ram, String flags) {
        Server server = new Server(name, port, jar, ram, flags);
        servers.put(name, server);

        String ip = EmirUtilsVelocity.config.getServerManagerConfig().getIp();
        EmirUtilsVelocity.proxy.registerServer(new ServerInfo(name, new InetSocketAddress(ip, port)));

        return server;
    }

}

