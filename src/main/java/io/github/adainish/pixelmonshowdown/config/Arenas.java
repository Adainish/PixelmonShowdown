package io.github.adainish.pixelmonshowdown.config;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;

public class Arenas extends Configurable
{
    private static Arenas config;



    public static Arenas getConfig() {
        if (config == null) {
            config = new Arenas();
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
        this.get().node("Arenas");
    }

    public String getConfigName() {
        return "Arenas.conf";
    }

    public Arenas() {
    }
}
