package io.github.adainish.pixelmonshowdown.config;

import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RentalConfig extends Configurable {
    private static RentalConfig config;

    public static RentalConfig getConfig() {
        if (config == null) {
            config = new RentalConfig();
        }
        return config;
    }

    public void setup() {
        super.setup();
    }

    public void load() {
        super.load();
    }

    public void populate() {
        try {
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "PokemonName").set("Vulpix");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "HeldItem").set("pixelmon:leftovers");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Level").set(10).comment("The Pokemons level if raise to cap was not enabled");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Form").set("").comment("Decide the form for this pokemon, leave blank if none are to be set");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "NickName").set("").comment("Set the Pokemons Nick Name in Battle!");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Shiny").set(false).comment("Is this Pokemon Shiny?");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Texture").set("").comment("Apply a pokemon Texture if these are installed");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "Nature").set("Timid").comment("What Nature should be applied to this Pokemon");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "Ability").set("Drought").comment("What Ability should this Pokemon have?");
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Dynamax").set(1).comment("Set the dynamax level for this Pokemon");
            //EVS
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "EVS", "HP").set(0);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "EVS", "ATK").set(0);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "EVS", "SPA").set(252);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "EVS", "DEF").set(0);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "EVS", "SPDEF").set(0);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "EVS", "SPD").set(252);
            //IVS
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "IVS", "HP").set(31);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "IVS", "ATK").set(31);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "IVS", "SPA").set(31);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "IVS", "DEF").set(31);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "IVS", "SPDEF").set(31);
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "Stats", "IVS", "SPD").set(31);

            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "MoveSet").set(Arrays.asList("Quick Attack", "Hidden Power", "Shadow Ball", "Incinerate"));
            this.get().node("Rentals", "AvailablePokemon", "ExampleEntry", "SpecFlags").set(Arrays.asList("unbreedable", "untradeable"));
        } catch (SerializationException e) {
            e.printStackTrace();
        }

    }

    public String getConfigName() {
        return "rentals.hocon";
    }

    public RentalConfig() {}
}
