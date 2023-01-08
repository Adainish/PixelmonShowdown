package io.github.adainish.pixelmonshowdown.config;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;
import java.util.Arrays;

public class Configuration extends Configurable
{
    private static Configuration config;

    public static Configuration getConfig() {
        if (config == null) {
            config = new Configuration();
        }
        return config;
    }

    public Path path() {
        return super.path();
    }

    public void setup() {
        super.setup();
    }

    public void load() {
        super.load();
    }

    public void populate() {
        try {
            this.get().node("Elo-Management", "K-Factor-Persistent").set(false);
            this.get().node("Elo-Management", "K-Factor-Persistent-Value").set(30.0);
            this.get().node("Elo-Management", "K-Factor", "K-Factor-High-Elo").set(30.0);
            this.get().node("Elo-Management", "K-Factor", "K-Factor-Mid-Elo").set(40.0);
            this.get().node("Elo-Management", "K-Factor", "K-Factor-Low-Elo").set(50.0);
            this.get().node("Elo-Management", "Elo-Range", "Low-Elo-Range").set(1300);
            this.get().node("Elo-Management", "Elo-Range", "High-Elo-Range").set(1600);
            this.get().node("Elo-Management", "Elo-Range", "Elo-Floor").set(1000);


            this.get().node("Queue-Management", "Match-Maker-Timer").set(10);
            this.get().node("Queue-Management", "Match-Maker-Bias-Value").set(25);
            this.get().node("Queue-Management", "Battle-Preparation-Time").set(30);
            this.get().node("Queue-Management", "Team-Preview-Time").set(15);
            this.get().node("Queue-Management", "Match-Threshold-Value").set(150);

            this.get().node("Arena-Management", "Arenas-Enabled").set(false);


            this.get().node("Data-Management", "Automatic-Saving-Enabled").set(true);
            this.get().node("Data-Management", "Save-Interval").set(15);

            this.get().node("GUI-Management", "Custom-Listing-Enabled").set(false);

        } catch (SerializationException e) {
            PixelmonShowdown.getInstance().log.error(e);
        }


    }

    public String getConfigName() {
        return "Configuration.conf";
    }

    public Configuration() {
    }
}
