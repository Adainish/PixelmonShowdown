package io.github.adainish.pixelmonshowdown.queues;

import com.pixelmonmod.pixelmon.api.battles.BattleType;
import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbilityRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRuleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.type.*;
import com.pixelmonmod.pixelmon.enums.heldItems.EnumHeldItems;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.clauses.MinLevelClause;
import io.github.adainish.pixelmonshowdown.clauses.MonoTypeClause;
import io.github.adainish.pixelmonshowdown.randoms.RentalPokemon;
import io.github.adainish.pixelmonshowdown.util.DataManager;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompetitiveFormat {
    private String formatName;
    private int positionNum;

    private BattleRules battleRules = new BattleRules();
    private List<PokemonBanClause> pokemonClauses = new ArrayList<>();
    private List<ItemPreventClause> itemClauses = new ArrayList<>();
    private List<AbilityClause> abilityClauses = new ArrayList<>();
    private List<MoveClause> moveClauses = new ArrayList<>();
    private List<LogicalOrBattleClauseSingle> complexClauses = new ArrayList<>();
    private List<String> strBattleRules = new ArrayList<>();
    private List<String> strPokemonClauses = new ArrayList<>();
    private List<String> strItemClauses = new ArrayList<>();
    private List<String> strAbilityClauses = new ArrayList<>();
    private List<String> strMoveClauses = new ArrayList<>();
    private boolean isRandomBattle = false;
    private int complexNum = 0;
    private int cooldown = 0;

    private boolean teamPreview = false;

    public CompetitiveFormat(String formatName){
        this.formatName = formatName;
    }

    public List<Pokemon> generateRentalTeam()
    {
        List<RentalPokemon> rentalPokemons = new ArrayList <>();
        List<Pokemon> pokemonList = new ArrayList <>();

        for (int i = 0; i < 6; i++) {
            if (i >= DataManager.rentalWrapper.rentalPokemonCache.size())
                break;
            RentalPokemon rentalPokemon = RandomHelper.getRandomElementFromCollection(DataManager.rentalWrapper.rentalPokemonCache.values());
            if (rentalPokemons.contains(rentalPokemon))
                continue;
            rentalPokemons.add(rentalPokemon);
        }
        for (RentalPokemon p:rentalPokemons) {
            pokemonList.add(p.getPokemon());
        }

        return pokemonList;
    }

    //Get Pokemon clause
    public PokemonBanClause getPokemonClause(String pokemonClause){
        if(pokemonClause.contains("_")) {
            //Check if form exists
            String[] splitString = pokemonClause.split("_");
            //Get pokemon name & form from string
            String pokemon = splitString[0];
            String suffix = splitString[1].toLowerCase();
            if (PixelmonSpecies.get(pokemon).isPresent()) {
                Species form = PixelmonSpecies.get(pokemon).get().getValueUnsafe();
                if (splitString.length == 2) {
                    for (Stats st:form.getForms()) {
                        if (st.getName().equalsIgnoreCase(suffix))
                        {
                            PokemonBase pokemonBase = new PokemonBase(form, st);
                            return new PokemonBanClause(pokemonClause, pokemonBase);
                        }
                    }
                }
            }
        }
        //If form isn't found or no suffix, try to find species & return it
        if (PixelmonSpecies.get(pokemonClause).isPresent()) {
            return new PokemonBanClause(pokemonClause, PixelmonSpecies.get(pokemonClause).get().getValueUnsafe());
        }
        //Throw error if pokemon not found
        PixelmonShowdown.getInstance().log.error("Error Getting Pokemon Clause: " + pokemonClause + ". Please check format config for errors.");
        return null;
    }

    //Adds Pokemon clause to rules
    public void addPokemonClause(String pokemonClause){
        String[] split = new String[]{};
        if (pokemonClause.contains("_")) {
            split = pokemonClause.split("_");
        }
        if(pokemonClause.equals("Legendaries")) { //Ban all legends if Legendaries is in list
            strPokemonClauses.add("Legendaries");
            for (int i :PixelmonSpecies.getLegendaries()) {
                Species sp = PixelmonSpecies.fromDex(i).get();
                PokemonBanClause clause = getPokemonClause(sp.getName());
                pokemonClauses.add(clause);
            }
        }
        else if(pokemonClause.equals("Ultrabeasts")){ //Ban all ultrabeasts if Ultrabeasts is in list
            strPokemonClauses.add("Ultrabeasts");
            for (int i :PixelmonSpecies.getUltraBeasts()) {
                Species sp = PixelmonSpecies.fromDex(i).get();
                PokemonBanClause clause = getPokemonClause(sp.getName());
                pokemonClauses.add(clause);
            }
        }
        else if(pokemonClause.contains("_") && PixelmonSpecies.get(split[0]).isPresent()) {
            PokemonBanClause clause = getPokemonClause(pokemonClause);
            if(clause != null){
                strPokemonClauses.add(pokemonClause);
                pokemonClauses.add(clause);
            }
        } else if (PixelmonSpecies.get(pokemonClause).isPresent())
        {
            PokemonBanClause clause = getPokemonClause(pokemonClause);
            if(clause != null){
                strPokemonClauses.add(pokemonClause);
                pokemonClauses.add(clause);
            }
        }
        else{
            PixelmonShowdown.getInstance().log.error("Error Adding Pokemon Clause: " + pokemonClause + ". Please check format config for errors.");
        }
    }

    public EnumHeldItems getHeldItem(String s) {

        for (EnumHeldItems h:EnumHeldItems.values()) {
            if (h.name().toLowerCase().equalsIgnoreCase(s))
                return h;
        }

        return null;
    }
    //Gets item clause from String
    public ItemPreventClause getItemClause(String itemClause){
        //Pixelmon's item enums dont follow consistent convention, so have to check which one the item is...

        //Converts string to lowercase notation
        String itemLowerCase = itemClause.replace(" ", "").toLowerCase();

        //Converts string to camelCase notation
        String itemCamelCase = itemClause.trim();
        if(!itemCamelCase.substring(0,1).equals(itemCamelCase.substring(0,1).toLowerCase())){
            itemCamelCase = itemCamelCase.substring(0,1).toLowerCase() + itemCamelCase.substring(1);
        }
        while(itemCamelCase.contains(" ")){
            int spaceIndex = itemCamelCase.indexOf(" ");
            String upperCase = String.valueOf(itemCamelCase.charAt(spaceIndex + 1)).toUpperCase();
            itemCamelCase = itemCamelCase.substring(0, spaceIndex) + upperCase + itemCamelCase.substring(spaceIndex + 2);
        }

        //Check if item exists
        try{
            if(itemLowerCase.equals("megastones")){
                return new ItemPreventClause(itemClause, EnumHeldItems.megaStone);
            }
            else if(itemLowerCase.equals("z crystals") || itemLowerCase.equals("z-crystals")){
                return new ItemPreventClause(itemClause, EnumHeldItems.zCrystal);
            }
            else {
                return new ItemPreventClause(itemClause, getHeldItem(itemLowerCase));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        try{
            return new ItemPreventClause(itemClause, getHeldItem(itemLowerCase));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        PixelmonShowdown.getInstance().log.error("Error Adding Ability Clause: " + itemClause + ". Please check format config for errors.");
        return null;
    }

    //Adds item clause to rules
    public void addItemClause(String itemClause){
        ItemPreventClause clause = getItemClause(itemClause);
        if(clause != null){
            strItemClauses.add(itemClause);
            itemClauses.add(clause);
        }
    }

    //Gets ability clause from String
    public AbilityClause getAbilityClause(String abilityClause){
        if(AbilityRegistry.getAbility(abilityClause).isPresent()) {
            AbstractAbility retrievedAbility = (AbstractAbility) AbilityRegistry.getAbility(abilityClause).get();
            return new AbilityClause(abilityClause, retrievedAbility.getClass());
        }
        else{
            PixelmonShowdown.getInstance().log.error("Error Adding Ability Clause: " + abilityClause + ". Please check format config for errors.");
        }
        return null;
    }

    //Adds ability clause to rules
    public void addAbilityClause(String abilityClause){
        AbilityClause clause = getAbilityClause(abilityClause);
        if(clause != null){
            strAbilityClauses.add(abilityClause);
            abilityClauses.add(clause);
        }
    }

    //Gets move clause from String
    public MoveClause getMoveClause(String moveClause){
        try{
            if(moveClause.equals("OHKO Moves")){
                return new MoveClause("OHKO", true , AttackRegistry.FISSURE, AttackRegistry.GUILLOTINE, AttackRegistry.HORN_DRILL, AttackRegistry.SHEER_COLD);
            }
            else {
                return new MoveClause("OHKO", true, AttackRegistry.getAttackBase(moveClause));
            }
        }
        catch (Exception e){
            PixelmonShowdown.getInstance().log.error("Error Adding Move Clause: " + moveClause + ". Please check format config for errors.");
        }
        return null;
    }

    //Adds move clause to rules
    public void addMoveClause(String moveClause){
        MoveClause clause = getMoveClause(moveClause);
        if(clause != null){
            strMoveClauses.add(moveClause);
            moveClauses.add(clause);
        }
    }

    //Adds a complex clause (when all rules are present, pokemon is not allowed)
    public void addComplexClause(List<String> clauses){
        ArrayList<BattleClause> builtClauses = new ArrayList<>();
        clauses.forEach(strClause -> {
            if (strClause.startsWith("P:")) {
                String pokemonClause = strClause.substring(2);
                PokemonBanClause clause = getPokemonClause(pokemonClause);
                if (clause != null) {
                    builtClauses.add(clause);
                }
            } else if (strClause.startsWith("A:")) {
                String abilityClause = strClause.substring(2);
                AbilityClause clause = getAbilityClause(abilityClause);
                if (clause != null) {
                    builtClauses.add(clause);
                }
            } else if (strClause.startsWith("I:")) {
                String itemClause = strClause.substring(2);
                ItemPreventClause clause = getItemClause(itemClause);
                if (clause != null) {
                    builtClauses.add(clause);
                }
            } else if (strClause.startsWith("M:")) {
                String moveClause = strClause.substring(2);
                MoveClause clause = getMoveClause(moveClause);
                if (clause != null) {
                    builtClauses.add(clause);
                }
            }
        });

        BattleClause[] arrClauses = new BattleClause[builtClauses.size()];
        arrClauses = builtClauses.toArray(arrClauses);


        LogicalOrBattleClauseSingle comboClause = new LogicalOrBattleClauseSingle("ComplexClause" + complexNum, arrClauses);

        complexNum++;
        complexClauses.add(comboClause);
    }

    //Build format from config
    public void buildFormat(){
        BattleRules newRules = new BattleRules();
        ArrayList<BattleClause> allClauses = new ArrayList<>();
        allClauses.addAll(pokemonClauses);
        allClauses.addAll(itemClauses);
        allClauses.addAll(abilityClauses);
        allClauses.addAll(moveClauses);
        allClauses.addAll(complexClauses);

        if(DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Sleep-Clause").getBoolean()){
            strBattleRules.add("Sleep Clause");
            allClauses.add(new BattleClause("sleep"));
        }
        if(DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Bag-Clause").getBoolean()){
            strBattleRules.add("Bag Clause");
            allClauses.add(new BattleClause("bag"));
        }
        if(DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Inverse-Clause").getBoolean()){
            strBattleRules.add("Inverse Battle Clause");
            allClauses.add(new BattleClause("inverse"));
        }
        if(DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Species-Clause").getBoolean()){
            strBattleRules.add("Species Clause");
            BattleClause clause = BattleClauseRegistry.getClause("pokemon");
            allClauses.add(clause);
        }
        if(DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Team-Preview").getBoolean()){
            this.teamPreview = true;
            strBattleRules.add("Team Preview");
        }

        setRandomBattle(DataManager.getFormatsNode("Formats", formatName, "RandomBattles").getBoolean());

        String monotype = DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Monotype").getString();
        if(monotype != null) {
            if (!monotype.equals("None")){
                if (monotype.equals("Any")) {
                    BattleClause clause = new MonoTypeClause("Monotype", null);
                    allClauses.add(clause);
                    strBattleRules.add("Monotype");
                }
                else {
                    Element type = Element.parseType(monotype);
                    if (type != null) {
                        BattleClause clause = new MonoTypeClause("Monotype", type);
                        allClauses.add(clause);
                        strBattleRules.add("Monotype " + type);
                    } else {
                        PixelmonShowdown.getInstance().log.error("Error Adding Monotype Clause: " + monotype + ". Please check format config for errors.");
                    }
                }
            }
        }

        int minLevel = DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Level-Floor").getInt();
        if(minLevel >= 0 && minLevel <= 100) {
            BattleClause clause = new MinLevelClause("Level Floor", minLevel);
            allClauses.add(clause);
            strBattleRules.add("Level Floor: " + minLevel);
        }
        else{
            PixelmonShowdown.getInstance().log.error("Error Adding Minimum Level Clause: " + minLevel + ". Please check format config for errors.");
        }

        newRules = new BattleRules(BattleType.SINGLE);

        newRules.setNewClauses(allClauses);
        newRules.set(BattleRuleRegistry.TEAM_PREVIEW, true);
        newRules.set(BattleRuleRegistry.LEVEL_CAP, DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Level-Cap").getInt());
        newRules.set(BattleRuleRegistry.FULL_HEAL, DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Full-Heal").getBoolean());
        newRules.set(BattleRuleRegistry.NUM_POKEMON, DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Num-Pokemon").getInt());
        newRules.set(BattleRuleRegistry.RAISE_TO_CAP, DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Raise-To-Cap").getBoolean());
        newRules.set(BattleRuleRegistry.TURN_TIME, DataManager.getFormatsNode().node("Formats", formatName, "Battle-Rules", "Turn-Time").getInt());
        this.battleRules = newRules;
    }

    public String getFormatName(){
        return formatName;
    }

    public BattleRules getBattleRules(){
        return battleRules;
    }

    //Get str lists for adding to rules UI
    public List<String> getStrBattleRules(){
        return strBattleRules;
    }

    public List<String> getStrItemClauses(){
        return strItemClauses;
    }

    public List<String> getStrPokemonClauses(){
        return strPokemonClauses;
    }

    public List<String> getStrAbilityClauses(){
        return strAbilityClauses;
    }

    public List<String> getStrMoveClauses(){
        return strMoveClauses;
    }

    public int getPositionNum(){
        return positionNum;
    }

    public boolean isTeamPreview(){
        return teamPreview;
    }

    public void loadFormat(){
        try {
            this.positionNum = DataManager.getFormatsNode().node("Formats", formatName, "Listing-Number").getInt() - 1;
            setCooldown(DataManager.getFormatsNode().node("Formats", formatName, "Cooldown", "Timer").getInt());
            List<String> strPokemonClauses = DataManager.getFormatsNode().node("Formats", formatName, "Pokemon-Clauses").getList(TypeToken.get(String.class));
            List<String> strItemClauses = DataManager.getFormatsNode().node("Formats", formatName, "Item-Clauses").getList(TypeToken.get(String.class));
            List<String> strAbilityClauses = DataManager.getFormatsNode().node("Formats", formatName, "Ability-Clauses").getList(TypeToken.get(String.class));
            List<String> strMoveClauses = DataManager.getFormatsNode().node("Formats", formatName, "Move-Clauses").getList(TypeToken.get(String.class));


            if (strPokemonClauses != null) {
                strPokemonClauses.forEach(this::addPokemonClause);
            }

            if (strItemClauses != null) {
                strItemClauses.forEach(this::addItemClause);
            }

            if (strAbilityClauses != null) {
                strAbilityClauses.forEach(this::addAbilityClause);
            }

            if (strMoveClauses != null) {
                strMoveClauses.forEach(this::addMoveClause);
            }

            Iterator<CommentedConfigurationNode> itr = DataManager.getFormatsNode().node("Formats", formatName, "Complex-Clauses").childrenList().iterator();

            while(itr.hasNext()){
                try {
                    CommentedConfigurationNode node = itr.next();
                    Iterator<CommentedConfigurationNode> strItr = node.childrenList().iterator();
                    ArrayList<String> strList = new ArrayList<>();
                    while(strItr.hasNext()){
                        try{
                            String clause = strItr.next().getString();
                            strList.add(clause);
                        }
                        catch(Exception e){
                            PixelmonShowdown.getInstance().log.error("PixelmonShowdown has encountered an error loading complex causes! Check configuration for errors!");
                            e.printStackTrace();
                        }
                    }

                    addComplexClause(strList);
                }
                catch(Exception e){
                    PixelmonShowdown.getInstance().log.error("PixelmonShowdown has encountered an error loading complex causes! Check configuration for errors!");
                    PixelmonShowdown.getInstance().log.error(e.getMessage());
                    e.printStackTrace();
                }

            }

            buildFormat();

        }
        catch(SerializationException e){
            PixelmonShowdown.getInstance().log.error("PixelmonShowdown has encountered an error loading format! Check configuration for errors!");
        }
    }

    public List<BattleClause> validateTeamList(List<Pokemon> team){
        List<BattleClause> clauseList = battleRules.getClauseList();
        List<BattleClause> caughtClauses = new ArrayList<>();
        for(BattleClause clause: clauseList){
            if(clause.validateTeam(team)){
                caughtClauses.add(clause);
            }
        }
        return caughtClauses;
    }

    public boolean isRandomBattle() {
        return isRandomBattle;
    }

    public void setRandomBattle(boolean randomBattle) {
        isRandomBattle = randomBattle;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
