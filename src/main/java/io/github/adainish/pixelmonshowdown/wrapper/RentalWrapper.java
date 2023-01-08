package io.github.adainish.pixelmonshowdown.wrapper;

import io.github.adainish.pixelmonshowdown.randoms.RentalPokemon;
import io.github.adainish.pixelmonshowdown.util.DataManager;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.HashMap;
import java.util.Map;

public class RentalWrapper
{
    public HashMap<String, RentalPokemon> rentalPokemonCache = new HashMap <>();

    public RentalWrapper()
    {
        loadRentals();
    }

    public void loadRentals()
    {
        CommentedConfigurationNode node = DataManager.getRentalsNode("Rentals", "AvailablePokemon");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj:nodeMap.keySet()) {
            if (obj == null)
                continue;
            String nodeString = obj.toString();
            RentalPokemon rentalPokemon = new RentalPokemon(nodeString);
            if (rentalPokemon.pokemon == null)
                continue;
            rentalPokemonCache.put(nodeString, rentalPokemon);
        }
    }
}
