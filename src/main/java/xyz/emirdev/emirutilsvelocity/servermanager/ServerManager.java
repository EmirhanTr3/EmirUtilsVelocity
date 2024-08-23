package xyz.emirdev.emirutilsvelocity.servermanager;

import com.velocitypowered.api.proxy.server.ServerInfo;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return createServer(name, port, jar, ram, "");
    }

    public Server createServer(String name, int port, String jar, int ram, String flags) {
        if (getServer(name) != null) {
            throw new Error("Server named " + name + " already exists.");
        }

        Server server = new Server(name, port, jar, ram, flags);
        servers.put(name, server);

        String ip = EmirUtilsVelocity.config.getServerManagerConfig().getIp();
        EmirUtilsVelocity.proxy.registerServer(new ServerInfo(name, new InetSocketAddress(ip, port)));

        return server;
    }

    public Server loadServer(String name, int port, String jar, int ram) {
        return loadServer(name, port, jar, ram, "");
    }

    public Server loadServer(String name, int port, String jar, int ram, String flags) {
        if (getServer(name) != null) {
            throw new Error("Server named " + name + " already exists.");
        }

        Path serverFolder = Paths.get("plugins/emirutilsvelocity/servermanager/server/" + name);

        if (!Files.exists(serverFolder)) {
            throw new Error("Folder of server named " + name + " was not found.");
        }

        Server server = new Server(name, port, jar, ram, flags);
        servers.put(name, server);

        return server;
    }

    public void unloadServer(String name) {
        if (getServer(name) == null) {
            throw new Error("Server named " + name + " does not exists.");
        }

        Server server = getServer(name);

        server.stop();
        servers.remove(name);
    }

}

