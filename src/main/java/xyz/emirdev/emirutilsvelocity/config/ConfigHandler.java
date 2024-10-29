package xyz.emirdev.emirutilsvelocity.config;

import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;

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

        saveFile();
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
                this.yamlFile.getString("database.name")
        );
    }
}