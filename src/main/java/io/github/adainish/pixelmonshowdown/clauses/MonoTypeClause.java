package io.github.adainish.pixelmonshowdown.clauses;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;

import java.util.List;

public class MonoTypeClause extends BattleClause {
    Element type;

    public MonoTypeClause(String id, Element type) {
        super(id);
        this.type = type;
    }

    //Check if team follows the clause type
    public boolean validateTeam(List<Pokemon> team){

        if(type == null) {
            Element type1;
            Element type2;
            type1 = team.get(0).getForm().getTypes().get(0);
            type2 = team.get(0).getForm().getTypes().get(1);
            PixelmonShowdown.getInstance().log.info(type1.getName());

            for (Pokemon pokemon : team) {
                boolean type1Different = false;
                boolean type2Different = false;
                PixelmonShowdown.getInstance().log.info(pokemon.getSpecies().getName() + pokemon.getForm().getName());
                PixelmonShowdown.getInstance().log.info(pokemon.getForm().getTypes().get(0).getName());

                if (pokemon.getForm().getTypes().get(0) != type1 &&
                        pokemon.getForm().getTypes().get(1) != type2) {
                    type1Different = true;

                }
                if (pokemon.getForm().getTypes().get(1) != type1 &&
                        pokemon.getForm().getTypes().get(1) != type2) {
                    type2Different = true;

                }

                if(type1Different && type2Different){
                    return false;
                }
            }
        }
        else{
            for (Pokemon pokemon: team){
                if(pokemon.getForm().getTypes().get(0) != type &&
                        (pokemon.getForm().getTypes().get(1) != type ||
                                pokemon.getForm().getTypes().get(1) == null)){
                    return false;
                }
            }
        }
        return true;
    }
}
