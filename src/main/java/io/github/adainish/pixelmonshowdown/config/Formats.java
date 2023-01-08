package io.github.adainish.pixelmonshowdown.config;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Formats extends Configurable {
    public List <String> defaultFormats()
    {
        List <String> formatList = new ArrayList <>(Arrays.asList("Ubers", "OU", "UU", "RU", "PU", "NU", "Monotype"));
        return formatList;
    }
    private static Formats config;

    public static Formats getConfig() {
        if (config == null) {
            config = new Formats();
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
            for (String s:defaultFormats()) {
                List<String> abilityClauses = new ArrayList <>();
                List<ArrayList<String>> complexClauses = new ArrayList <>();
                List<String> itemClauses = new ArrayList <>();
                int listingNumber = 0;
                List<String> moveClauses = new ArrayList <>();
                List<String> pokemonClauses = new ArrayList <>();


                for (int i = 0; i < defaultFormats().size(); i++) {
                    if (defaultFormats().get(i).equalsIgnoreCase(s)) {
                        abilityClauses.add("BattleBond");
                        abilityClauses.add("ShadowTag");
                        abilityClauses.add("Drizzle");
                        abilityClauses.add("Drought");

                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Blaziken",
                                "A:SpeedBoost")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Blaziken",
                                "I:Megastones")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Gengar",
                                "I:Megastones")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Kangaskhan",
                                "I:Megastones")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Lucario",
                                "I:Megastones")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Metagross",
                                "I:Megastones")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Salamence",
                                "I:Megastones")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Mawile",
                                "I:Megastones")));
                        complexClauses.add(new ArrayList <>(Arrays.asList("P:Medicham",
                                "I:Megastones")));

                        itemClauses.add("DampRock");
                        itemClauses.add("SmoothRock");

                        listingNumber = i;
                        moveClauses.add("Baton Pass");
                        moveClauses.add("OHKO Moves");
                        moveClauses.add("Double Team");
                        moveClauses.add("Minimize");
                        moveClauses.add("Swagger");
                        pokemonClauses.addAll(Arrays.asList("Aegislash",
                                "Arceus",
                                "Darkrai",
                                "Deoxys",
                                "Deoxys_Attack",
                                "Dialga",
                                "Genesect",
                                "Giratina",
                                "Groudon",
                                "Ho-Oh",
                                "Hoopa_Unbound",
                                "Kartana",
                                "Kyogre",
                                "Kyurem_White",
                                "Lugia",
                                "Lunala",
                                "Marshadow",
                                "Mewtwo",
                                "Naganadel",
                                "Necrozma_Dawn",
                                "Necrozma_Dusk",
                                "Palkia",
                                "Pheromosa",
                                "Rayquaza",
                                "Reshiram",
                                "Shaymin_Sky",
                                "Solgaleo",
                                "Tapu Lele",
                                "Xerneas",
                                "Yveltal",
                                "Zekrom",
                                "Zygarde"));
                        break;
                    }
                }

                this.get().node("Formats", s, "Ability-Clauses").set(abilityClauses);

                this.get().node("Formats", s, "RandomBattles").set(false);

                this.get().node("Formats", s, "Battle-Rules", "Bag-Clause").set(true);
                this.get().node("Formats", s, "Battle-Rules", "Full-Heal").set(true);
                this.get().node("Formats", s, "Battle-Rules", "Inverse-Battle").set(false);
                this.get().node("Formats", s, "Battle-Rules", "Level-Cap").set(100);
                this.get().node("Formats", s, "Battle-Rules", "Monotype").set(s.equalsIgnoreCase("Monotype") ? "Any" : "None");
                this.get().node("Formats", s, "Battle-Rules", "Num-Pokemon").set(6);
                this.get().node("Formats", s, "Battle-Rules", "Raise-To-Cap").set(true);
                this.get().node("Formats", s, "Battle-Rules", "Sleep-Clause").set(true);
                this.get().node("Formats", s, "Battle-Rules", "Species-Clause").set(true);
                this.get().node("Formats", s, "Battle-Rules", "Team-Preview").set(true);
                this.get().node("Formats", s, "Battle-Rules", "Turn-Time").set(60);

                this.get().node("Formats", s, "Complex-Clauses").set(complexClauses);
                this.get().node("Formats", s, "Item-Clauses").set(itemClauses);
                this.get().node("Formats", s, "Listing-Number").set(listingNumber);
                this.get().node("Formats", s, "Move-Clauses").set(moveClauses);
                this.get().node("Formats", s, "Pokemon-Clauses").set(pokemonClauses);
            }

        } catch (SerializationException e) {
            PixelmonShowdown.getInstance().log.error(e);
        }


    }

    public String getConfigName() {
        return "Formats.conf";
    }

    public Formats() {
    }
}
