package io.github.adainish.pixelmonshowdown.randoms;

import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.Nature;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbilityRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.config.RentalConfig;
import io.leangen.geantyref.TypeToken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public class RentalPokemon
{

    public Pokemon pokemon;
    public String node = "";

    public RentalPokemon(String identifier)
    {
        this.node = identifier;
        this.loadPokemon();
    }

    public void loadPokemon()
    {
        this.pokemon = generatePokemonFromFile(this.node);
    }

    public Pokemon getPokemon()
    {
        if (pokemon == null)
        return generatePokemonFromFile(node);
        else return pokemon;
    }

    public Pokemon generatePokemonFromFile(String node) {

        String pokemonname = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "PokemonName").getString();
        String form = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Form").getString();
        int level = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Level").getInt();
        String nickname = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "NickName").getString();
        boolean shiny = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Shiny").getBoolean();
        String texture = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Texture").getString();
        String nature = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "Nature").getString();
        String ability = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "Ability").getString();
        int dynamaxLevel = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Dynamax").getInt();
        String heldItem = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "HeldItem").getString();
        List <String> moves = new ArrayList <>();
        try {
            moves = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "MoveSet").getList(TypeToken.get(String.class));
        } catch (SerializationException e) {
            PixelmonShowdown.getInstance().log.error(e);
        }

        List <String> specFlags = new ArrayList <>();
        try {
            specFlags = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "SpecFlags").getList(TypeToken.get(String.class));
        } catch (SerializationException e) {
            PixelmonShowdown.getInstance().log.error(e);
        }
        //Evs

        int evsHP = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "EVS", "HP").getInt();
        int evsATK = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "EVS", "ATK").getInt();
        int evsSPA = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "EVS", "SPA").getInt();
        int evsDEF = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "EVS", "DEF").getInt();
        int evsSPDEF = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "EVS", "SPDEF").getInt();
        int evsSPD = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "EVS", "SPD").getInt();
        //IVS
        int ivsHP = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "IVS", "HP").getInt();
        int ivsATK = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "IVS", "ATK").getInt();
        int ivsSPA = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "IVS", "SPA").getInt();
        int ivsDEF = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "IVS", "DEF").getInt();
        int ivsSPDEF = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "IVS", "SPDEF").getInt();
        int ivsSPD = RentalConfig.getConfig().get().node("Rentals", "AvailablePokemon", node, "Stats", "IVS", "SPD").getInt();

        if (pokemonname == null) {
            PixelmonShowdown.getInstance().log.info("The PokemonName doesn't exist, please check your config!");
            return null;
        }

        if (!PixelmonSpecies.get(pokemonname).isPresent()) {
            PixelmonShowdown.getInstance().log.info("The Pokemon Species %mon% doesn't exist!".replaceAll("%mon%", pokemonname));
            return null;
        }

        Pokemon pokemon = PokemonFactory.create(PixelmonSpecies.get(pokemonname).get().orElse(PixelmonSpecies.MAGIKARP)).toPokemon();
        pokemon.setLevel(level);

        pokemon.getIVs().setStat(BattleStatsType.HP, ivsHP);
        pokemon.getIVs().setStat(BattleStatsType.ATTACK, ivsATK);
        pokemon.getIVs().setStat(BattleStatsType.SPECIAL_ATTACK, ivsSPA);
        pokemon.getIVs().setStat(BattleStatsType.DEFENSE, ivsDEF);
        pokemon.getIVs().setStat(BattleStatsType.SPECIAL_DEFENSE, ivsSPDEF);
        pokemon.getIVs().setStat(BattleStatsType.SPEED, ivsSPD);


        pokemon.getEVs().setStat(BattleStatsType.HP, evsHP);
        pokemon.getEVs().setStat(BattleStatsType.ATTACK, evsATK);
        pokemon.getEVs().setStat(BattleStatsType.SPECIAL_ATTACK, evsSPA);
        pokemon.getEVs().setStat(BattleStatsType.DEFENSE, evsDEF);
        pokemon.getEVs().setStat(BattleStatsType.SPECIAL_DEFENSE, evsSPDEF);
        pokemon.getEVs().setStat(BattleStatsType.SPEED, evsSPD);

        pokemon.setGrowth(EnumGrowth.Ordinary);
        if (nickname != null) {
            if (!nickname.isEmpty())
                pokemon.setNickname(nickname);
        }

        if (heldItem != null)
            if (!heldItem.isEmpty()) {
                ResourceLocation location = new ResourceLocation(heldItem);
                Item op = ForgeRegistries.ITEMS.getValue(location);
                if (op == null) {
                    PixelmonShowdown.getInstance().log.error("The Item for the pokemon held item could not be created, thus the pokemon was given an oran berry");
                    op = PixelmonItems.oran_berry;
                }
                ItemStack itemStack = new ItemStack(op);
                if (itemStack.isEmpty()) //Ignore, this can still be null due to the String being editable by administrators
                    pokemon.setHeldItem(itemStack);
                else
                    PixelmonShowdown.getInstance().log.error("The ItemStack couldn't be created for npc pokemon %pokemon%".replaceAll("%pokemon%", pokemonname));
            }

        if (form != null && !form.isEmpty())
            if (pokemon.getSpecies().hasForm(form))
                pokemon.setForm(form);
            else pokemon.setForm(pokemon.getSpecies().getDefaultForm());

        pokemon.setShiny(shiny);

        if (texture != null) {
            if (!texture.isEmpty()) {
                // TODO: 17/06/2022
                //  add texture support
            }
        }

        if (Nature.natureFromString(nature) != null)
            pokemon.setNature(Nature.natureFromString(nature));
        else {
            pokemon.setNature(Nature.getRandomNature());
            PixelmonShowdown.getInstance().log.info("There was an issue generating the nature for %pokemon%, please check your config for any errors".replace("%pokemon%", pokemonname));
        }
        pokemon.getMoveset().clear();
        if (moves != null) {
            for (String s : moves) {
                Attack atk = new Attack(s);
                if (AttackRegistry.getAttackBase(s).isPresent()) {
                    pokemon.getMoveset().add(atk);
                } else {
                    PixelmonShowdown.getInstance().log.info("The %move% for %pokemon% doesn't exist! skipping move!".replace("%pokemon%", pokemonname).replace("%move%", s));
                }
            }
        } else {
            pokemon.rerollMoveset();
            PixelmonShowdown.getInstance().log.info("The moves for %pokemon% returned null, generating random movelist".replace("%pokemon%", pokemonname));
        }
        if (ability != null) {
            if (AbilityRegistry.getAbility(ability).isPresent()) {
                pokemon.setAbility(AbilityRegistry.getAbility(ability));
            } else {
                PixelmonShowdown.getInstance().log.info("There was an issue generating the ability for %pokemon%, %ability% doesn't exist according to pixelmon. Please check your config for any errors".replace("%pokemon%", pokemonname).replace("%ability%", ability));
                pokemon.setAbility(pokemon.getForm().getAbilities().getRandomAbility());
            }
        } else {
            pokemon.setAbility(pokemon.getForm().getAbilities().getRandomAbility());
            PixelmonShowdown.getInstance().log.info("There was an issue generating the ability for %pokemon% due to a nullpointer being detected in the config, please check your config for any errors".replace("%pokemon%", pokemonname));
        }
        pokemon.setDynamaxLevel(dynamaxLevel);
        pokemon.setDoesLevel(false);
        pokemon.addFlag("rentalPokemon");
        pokemon.addFlag("unbreedable");
        if (specFlags != null && !specFlags.isEmpty()) {
            for (String s : specFlags) {
                pokemon.addFlag(s);
            }
        }
        return pokemon;
    }

}
