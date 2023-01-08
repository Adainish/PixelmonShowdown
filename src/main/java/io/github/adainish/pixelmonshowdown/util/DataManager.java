package io.github.adainish.pixelmonshowdown.util;

import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.config.*;
import io.github.adainish.pixelmonshowdown.wrapper.RentalWrapper;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataManager {

    private static Path dir;
    private static Configurable config, arenas, elos, formats, rentals;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoad, elosLoad, formatsLoad, arenasLoad, rentalsLoad;
    private static CommentedConfigurationNode configNode, elosNode, formatsNode, arenasNode, rentalsNode;
    private static boolean autoSaveEnabled;
    private static int interval;

    public static RentalWrapper rentalWrapper;

    public static void setup() {
        dir = new File(new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).toString()) + "/PixelmonShowdown/").toPath();
        load();
        update();
    }

    public static void load() {
        try {
            if(!Files.exists(dir))
                Files.createDirectory(dir);

            config = Configuration.getConfig();
            arenas = Arenas.getConfig();
            elos = Elos.getConfig();
            formats = Formats.getConfig();
            rentals = RentalConfig.getConfig();

            config.setup();
            arenas.setup();
            elos.setup();
            formats.setup();
            rentals.setup();

            config.load();
            arenas.load();
            formats.load();
            elos.load();
            rentals.load();


            configLoad = config.getConfigLoader();
            elosLoad = elos.getConfigLoader();
            formatsLoad = formats.getConfigLoader();
            arenasLoad = arenas.getConfigLoader();
            rentalsLoad = rentals.getConfigLoader();

            configNode = config.get();
            elosNode = elos.get();
            formatsNode = formats.get();
            arenasNode = arenas.get();
            rentalsNode = rentals.get();

            autoSaveEnabled = getConfigNode().node("Data-Management", "Automatic-Saving-Enabled").getBoolean();
            interval = getConfigNode().node("Data-Management", "Save-Interval").getInt();
            rentalWrapper = new RentalWrapper();
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
                    .url(config.path().toUri().toURL())
                    .build()
                    .load(ConfigurationOptions.defaults()));

            elosNode.mergeFrom(HoconConfigurationLoader.builder()
                    .url(elos.path().toUri().toURL())
                    .build()
                    .load(ConfigurationOptions.defaults()));

            formatsNode.mergeFrom(HoconConfigurationLoader.builder()
                    .url(formats.path().toUri().toURL())
                    .build()
                    .load(ConfigurationOptions.defaults()));

            arenasNode.mergeFrom(HoconConfigurationLoader.builder()
                    .url(arenas.path().toUri().toURL())
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

    public static CommentedConfigurationNode getRentalsNode(Object... node)
    {
        return rentalsNode.node(node);
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
    public static ConfigurationLoader<CommentedConfigurationNode> getRentalsLoad(){
        return rentalsLoad;
    }
}
