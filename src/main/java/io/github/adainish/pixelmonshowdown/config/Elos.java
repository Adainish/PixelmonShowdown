package io.github.adainish.pixelmonshowdown.config;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;
import java.util.Arrays;

public class Elos extends Configurable
{
    private static Elos config;

    public static Elos getConfig() {
        if (config == null) {
            config = new Elos();
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
        this.get().node("Player-Elos", "Ubers");
        this.get().node("Player-Elos", "OU");
        this.get().node("Player-Elos", "UU");
        this.get().node("Player-Elos", "NU");
        this.get().node("Player-Elos", "PU");
        this.get().node("Player-Elos", "Monotype");

    }

    public String getConfigName() {
        return "Elos.conf";
    }

    public Elos() {
    }
}
