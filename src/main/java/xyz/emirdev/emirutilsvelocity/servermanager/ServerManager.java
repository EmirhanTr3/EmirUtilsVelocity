package xyz.emirdev.emirutilsvelocity.servermanager;

import com.velocitypowered.api.proxy.server.ServerInfo;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
        if (getServer(name) != null || Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name))) {
            throw new Error("Server named " + name + " already exists.");
        }

        if (EmirUtilsVelocity.proxy.getServer(name).isPresent()) {
            throw new Error("Server named " + name + " already exists in the proxy.");
        }

        if (!Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/jars/" + jar))) {
            throw new Error("Server jar not found: " + jar);
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

        if (EmirUtilsVelocity.proxy.getServer(name).isPresent()) {
            throw new Error("Server named " + name + " already exists in the proxy.");
        }

        Path serverFolder = Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name);

        if (!Files.exists(serverFolder)) {
            throw new Error("Folder of server named " + name + " was not found.");
        }

        if (!Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name + "/" + jar))) {
            throw new Error("Server jar not found: " + jar);
        }


        Server server = new Server(name, port, jar, ram, flags);
        servers.put(name, server);

        String ip = EmirUtilsVelocity.config.getServerManagerConfig().getIp();
        EmirUtilsVelocity.proxy.registerServer(new ServerInfo(name, new InetSocketAddress(ip, port)));

        return server;
    }

    public void unloadServer(String name) {
        if (getServer(name) == null) {
            throw new Error("Server named " + name + " does not exist.");
        }

        Server server = getServer(name);

        if (server.getProcess() != null) server.stop();
        servers.remove(name);

        String ip = EmirUtilsVelocity.config.getServerManagerConfig().getIp();
        EmirUtilsVelocity.proxy.unregisterServer(new ServerInfo(name, new InetSocketAddress(ip, server.getPort())));
    }

    public void deleteServer(String name) {
        Path serverPath = Path.of("plugins/emirutilsvelocity/servermanager/servers/" + name);
        if (!Files.exists(serverPath)) {
            throw new Error("Server named " + name + " does not exist");
        }

        Server server = getServer(name);

        if (server != null) {
            if (server.getProcess() != null) server.kill();
            servers.remove(name);

            String ip = EmirUtilsVelocity.config.getServerManagerConfig().getIp();
            EmirUtilsVelocity.proxy.unregisterServer(new ServerInfo(name, new InetSocketAddress(ip, server.getPort())));
        }

        try {
            Files.walkFileTree(serverPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {}
    }

}

