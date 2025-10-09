package xyz.emirdev.echologic;

import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class PluginConfig {
    private final Path path;
    private final Logger logger;

    @Getter
    private YamlConfigurationLoader loader;
    @Getter private CommentedConfigurationNode root;

    public PluginConfig(Path path) {
        this.path = path;
        this.logger = EchoLogic.get().getLogger();
    }

    public void load() {
        logger.info("Loading config...");

        // Check if config exists
        Path configPath = path.resolve("config.yml");
        if (!Files.exists(configPath)) {
            logger.info("Config does not exist. Creating config...");
            try {
                if (!Files.exists(path))
                    Files.createDirectories(path);
                Files.createFile(configPath);
            } catch (IOException e) {
                logger.error("Couldn't create file for config:", e);
                return;
            }

            logger.info("Copying config from plugin...");

            try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(Objects.requireNonNull(is), configPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException | NullPointerException e) {
                logger.error("Couldn't copy file from plugin's classloader:", e);
                return;
            }
        }

        this.loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .build();

        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            logger.error("Couldn't load configuration to memory:", e);
        }
    }

    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            logger.error("Couldn't save configuration to disk:", e);
        }
    }
}