package io.github.adainish.pixelmonshowdown.util;

import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataManager {
    private static Path dir, config, elos, formats, arenas;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoad, elosLoad, formatsLoad, arenasLoad;
    private static CommentedConfigurationNode configNode, elosNode, formatsNode, arenasNode;
    private static final String[] FILES = {"Configuration.conf", "Elos.conf", "Formats.conf", "Arenas.conf"};
    private static boolean autoSaveEnabled;
    private static int interval;

    public static void setup(Path folder) {
        dir = folder;
        config = dir.resolve(FILES[0]);
        elos = dir.resolve(FILES[1]);
        formats = dir.resolve(FILES[2]);
        arenas = dir.resolve(FILES[3]);
        load();
        update();
    }

    public static void load() {
        try {
            if(!Files.exists(dir))
                Files.createDirectory(dir);

            PixelmonShowdown.getContainer().getAsset(FILES[0]).get().copyToFile(config, false, true);
            PixelmonShowdown.getContainer().getAsset(FILES[1]).get().copyToFile(elos, false, true);
            PixelmonShowdown.getContainer().getAsset(FILES[2]).get().copyToFile(formats, false, true);
            PixelmonShowdown.getContainer().getAsset(FILES[3]).get().copyToFile(arenas, false, true);

            configLoad = HoconConfigurationLoader.builder().path(config).build();
            elosLoad = HoconConfigurationLoader.builder().path(elos).build();
            formatsLoad = HoconConfigurationLoader.builder().path(formats).build();
            arenasLoad = HoconConfigurationLoader.builder().path(arenas).build();

            configNode = configLoad.load();
            elosNode = elosLoad.load();
            formatsNode = formatsLoad.load();
            arenasNode = arenasLoad.load();

            autoSaveEnabled = getConfigNode().node("Data-Management", "Automatic-Saving-Enabled").getBoolean();
            interval = getConfigNode().node("Data-Management", "Save-Interval").getInt();

        } catch(IOException e) {
            PixelmonShowdown.getInstance().log.error("Error loading PixelmonShowdown Configurations");
            e.printStackTrace();
        }

        saveAll();
    }

    public static void saveAll() {
        try {
            configLoad.save(configNode);
            elosLoad.save(elosNode);
            formatsLoad.save(formatsNode);
            arenasLoad.save(arenasNode);
        } catch (IOException e) {
            PixelmonShowdown.getInstance().log.error("Error saving PixelmonShowdown Configuration");
            e.printStackTrace();
        }
    }

    public static void saveElos() {
        try {
            PixelmonShowdown.getInstance().queueManager.saveAllQueueProfiles();
            elosLoad.save(elosNode);
            PixelmonShowdown.getInstance().log.info("Elos saved.");
        } catch (IOException e) {
            PixelmonShowdown.getInstance().log.error("Error saving PixelmonShowdown Elos Configuration");
            e.printStackTrace();
        }
    }

    public static void saveArenas() {
        try {
            arenasLoad.save(arenasNode);
        } catch (IOException e) {
            PixelmonShowdown.getInstance().log.error("Error saving PixelmonShowdown Arenas Configuration");
            e.printStackTrace();
        }
    }

    public static void startAutoSave(){
        if (autoSaveEnabled) {
            Task.builder().execute(DataManager::saveElos).interval(
                    (20 * 60 ) * interval).infinite().build();
        }
    }

    public static void update() {
        try {
            configNode.mergeFrom(HoconConfigurationLoader.builder()
                    .url(PixelmonShowdown.getContainer().getAsset(FILES[0]).get().getUrl())
                    .build()
                    .load(ConfigurationOptions.defaults()));

            elosNode.mergeFrom(HoconConfigurationLoader.builder()
                    .url(PixelmonShowdown.getContainer().getAsset(FILES[1]).get().getUrl())
                    .build()
                    .load(ConfigurationOptions.defaults()));

            formatsNode.mergeFrom(HoconConfigurationLoader.builder()
                    .url(PixelmonShowdown.getContainer().getAsset(FILES[2]).get().getUrl())
                    .build()
                    .load(ConfigurationOptions.defaults()));

            arenasNode.mergeFrom(HoconConfigurationLoader.builder()
                    .url(PixelmonShowdown.getInstance().getContainer().getAsset(FILES[3]).get().getUrl())
                    .build()
                    .load(ConfigurationOptions.defaults()));

            saveAll();

        } catch (IOException e) {
            PixelmonShowdown.getInstance().log.error("Error updating PixelmonShowdown Configuration");
            e.printStackTrace();
        }
    }



    public static CommentedConfigurationNode getConfigNode(Object... node) {
        return configNode.node(node);
    }

    public static CommentedConfigurationNode getElosNode(Object... node) {
        return elosNode.node(node);
    }

    public static CommentedConfigurationNode getFormatsNode(Object... node) {
        return formatsNode.node(node);
    }

    public static CommentedConfigurationNode getArenasNode(Object... node) {
        return arenasNode.node(node);
    }

    public static ConfigurationLoader<CommentedConfigurationNode> getConfigLoad(){
        return configLoad;
    }

    public static ConfigurationLoader<CommentedConfigurationNode> getElosLoad(){
        return elosLoad;
    }

    public static ConfigurationLoader<CommentedConfigurationNode> getFormatsLoad(){
        return formatsLoad;
    }

    public static ConfigurationLoader<CommentedConfigurationNode> getArenasLoad(){
        return arenasLoad;
    }
}
