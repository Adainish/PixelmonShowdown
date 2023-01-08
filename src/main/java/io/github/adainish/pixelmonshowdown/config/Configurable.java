package io.github.adainish.pixelmonshowdown.config;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Configurable {
    protected CommentedConfigurationNode configNode;
    private Path configFile = Paths.get(PixelmonShowdown.getInstance().defaultConfigFolder + "//PixelmonShowdown//" + this.getConfigName());
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    public Configurable() {
        this.configLoader = HoconConfigurationLoader.builder().path(this.configFile).build();
    }

    public abstract void populate();

    public abstract String getConfigName();

    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        return this.configLoader;
    }

    public Path path()
    {
        return this.configFile;
    }

    public void setup() {
        File configDirectory = new File(PixelmonShowdown.getInstance().defaultConfigFolder + "//PixelmonShowdown//");
        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        if (!Files.exists(this.configFile)) {
            try {
                Files.createFile(this.configFile);
                this.load();
                this.populate();
                this.save();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        } else {
            this.load();
        }

    }

    public void load() {
        try {
            this.configNode = (CommentedConfigurationNode) this.configLoader.load();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void setConfigNode(CommentedConfigurationNode configNode) {
        this.configNode = configNode;
    }

    public void save() {
        try {
            this.configLoader.save(this.configNode);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public CommentedConfigurationNode get() {
        return this.configNode;
    }
}
