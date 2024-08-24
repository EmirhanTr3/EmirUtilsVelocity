package xyz.emirdev.emirutilsvelocity.servermanager;

import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {
    private final String name;
    private final int port;
    private final String jar;
    private final int ram;
    private final String flags;
    private Process process;

    public Server(String name, int port, String jar, int ram, String flags) {
        this.name = name;
        this.port = port;
        this.jar = jar;
        this.ram = ram;
        this.flags = flags;

        Path serverPath = Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name);

        if (Files.exists(serverPath)) return;

        try {
            if (!Files.exists(serverPath)) Files.createDirectory(serverPath);
            Files.copy(
                    Paths.get("plugins/emirutilsvelocity/servermanager/jars/" + jar),
                    Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name + "/" + jar)
            );
        } catch (FileAlreadyExistsException e) {
            System.out.println("Jar already exists: " + e.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String main = "plugins/emirutilsvelocity/servermanager/servers/" + name;

            // server.properties
            String serverProp = main + "/server.properties";
            File serverPropFile = new File(serverProp);
            if (!serverPropFile.exists()) {
                serverPropFile.createNewFile();
                FileWriter spWriter = new FileWriter(serverPropFile);
                spWriter.write(
                    String.format("""
                        online-mode=false
                        server-ip=0.0.0.0
                        server-port=%s
                        query.port=%s
                        """,
                        port, port
                    )
                );
                spWriter.close();
            }

            // config/paper-global.yml
            String paperConfig = main + "/config/paper-global.yml";
            Path paperConfigFolder = Paths.get(main + "/config");
            if (!Files.exists(paperConfigFolder)) Files.createDirectory(paperConfigFolder);
            File paperConfigFile = new File(paperConfig);
            if (!paperConfigFile.exists()) {
                paperConfigFile.createNewFile();
                FileWriter pcWriter = new FileWriter(paperConfigFile);
                pcWriter.write(
                    String.format("""
                        proxies:
                          bungee-cord:
                            online-mode: true
                          proxy-protocol: false
                          velocity:
                            enabled: true
                            online-mode: true
                            secret: %s
                        """,
                        EmirUtilsVelocity.getVelocitySecret()
                    )
                );
                pcWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Process start() {
        Process process = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder()
                .command(
                    "bash", "-c",
                    "cd plugins/emirutilsvelocity/servermanager/servers/" + name + " ; " +
                    "java " +
                    "-Xms128M " +
                    "-Xmx" + ram + "M " +
                    "-Dcom.mojang.eula.agree=true " +
                    "-jar " + jar + " " +
                    flags
                        .replace("%port%", String.valueOf(port))
                        .replace("%secret%", EmirUtilsVelocity.getVelocitySecret())
                );
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.process = process;

        Future<Void> future = Executors.newSingleThreadExecutor().submit(() -> {
            Process process1 = EmirUtilsVelocity.getServerManager().getServer(name).getProcess();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process1.getInputStream()))) {
                String line;
                String logPrefix = "["+ process1.pid() +"] " + "["+ name +":"+ port +"] ";
                while ((line = reader.readLine()) != null) {
                    System.out.println(logPrefix + line);
                }
            } catch (IOException ignored) {}
            return null;
        });

        return process;
    }

    public void stop() {
        process.destroy();
        process = null;
    }

    public void kill() {
        process.destroyForcibly();
        process = null;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getJar() {
        return jar;
    }

    public int getRam() {
        return ram;
    }

    public String getFlags() {
        return flags;
    }

    public Process getProcess() {
        return process;
    }

    public ServerStatus getStatus() {
        if (process != null && process.isAlive()) {
            return ServerStatus.valueOf("Online");

        } else if (Files.exists(Paths.get("plugins/emirutilsvelocity/servermanager/servers/" + name))) {
            return ServerStatus.valueOf("Offline");

        } else {
            return ServerStatus.valueOf("Invalid");
        }
    }
}
