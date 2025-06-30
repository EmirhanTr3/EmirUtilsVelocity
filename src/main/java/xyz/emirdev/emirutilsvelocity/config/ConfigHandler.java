package xyz.emirdev.emirutilsvelocity.config;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.simpleyaml.configuration.file.YamlFile;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ConfigHandler {
    private static final File DATA_FILE = new File("plugins/emirutilsvelocity/config.yml");

    private YamlFile yamlFile;

    public ConfigHandler() {
        loadFile();

        this.yamlFile.setComment("database", "The configuration for the MySQL database.");
        this.yamlFile.addDefault("database.address", "0.0.0.0");
        this.yamlFile.addDefault("database.port", 3306);
        this.yamlFile.addDefault("database.username", "username");
        this.yamlFile.addDefault("database.password", "password");
        this.yamlFile.addDefault("database.name", "emirutilsvelocity");

        this.yamlFile.setComment("hub", "The hub server's name for /hub. Set to \"none\" to disable.");
        this.yamlFile.addDefault("hub", "none");

        this.yamlFile.setComment("discord", "The configuration of /discord command.");
        this.yamlFile.setComment("discord.invite", "The discord server invite. Set to \"none\" to disable.");
        this.yamlFile.addDefault("discord.invite", "none");
        this.yamlFile.setComment("discord.message",
                "The message to send when /discord is executed. <invite> will be replaced with the invite url.");
        this.yamlFile.addDefault("discord.message",
                "<click:open_url:'<invite>'><aqua>Join our discord server by clicking</aqua> <dark_aqua><u>here</u></dark_aqua><aqua>.</aqua></click>");

        this.yamlFile.setComment("ipcheck", """
                IP Check Configuration
                - We are using the proxycheck.io API for checking IP data.
                - The API is usable with its guest mode, but it's only limited to 100 requests per day.
                - By making a free account, you instead get 1000 free requests per day, and if you pay - even more!""");
        this.yamlFile.addDefault("ipcheck.enabled", true);
        this.yamlFile.addDefault("ipcheck.key", "none");

        this.yamlFile.setComment("announcement.format", """
                Format of the announcement command.
                <message> will be replaced with the announcement message.""");
        this.yamlFile.addDefault("announcement.format", List.of("", "<message>", ""));

        saveFile();
    }

    public void reload() throws IOException {
        this.yamlFile = new YamlFile(DATA_FILE);
        this.yamlFile.loadWithComments();
    }

    public void loadFile() {
        this.yamlFile = new YamlFile(DATA_FILE);
        try {
            this.yamlFile.createOrLoadWithComments();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFile() {
        try {
            this.yamlFile.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DatabaseConfig getDatabase() {
        return new DatabaseConfig(
                this.yamlFile.getString("database.address"),
                this.yamlFile.getInt("database.port"),
                this.yamlFile.getString("database.username"),
                this.yamlFile.getString("database.password"),
                this.yamlFile.getString("database.name"));
    }

    public RegisteredServer getHubServer() {
        String hub = this.yamlFile.getString("hub");
        if (hub.equals("none"))
            return null;
        Optional<RegisteredServer> server = EmirUtilsVelocity.getProxy().getServer(hub);
        return server.orElse(null);
    }

    public String getDiscordInvite() {
        String discord = this.yamlFile.getString("discord.invite");
        if (discord.equals("none"))
            return null;
        return discord;
    }

    public String getDiscordMessage() {
        return this.yamlFile.getString("discord.message");
    }

    public String getIPCheckKey() {
        String key = this.yamlFile.getString("ipcheck.key");
        if (key.equals("none"))
            return null;
        return key;
    }

    public boolean isIPCheckEnabled() {
        return this.yamlFile.getBoolean("ipcheck.enabled");
    }

    public String getAnnouncementFormat() {
        return String.join("\n", this.yamlFile.getStringList("announcement.format"));
    }
}
