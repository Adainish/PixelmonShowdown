package io.github.adainish.pixelmonshowdown.clauses;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;

import java.util.List;

public class MinLevelClause extends BattleClause {
    int level;

    public MinLevelClause(String id, int level) {
        super(id);
        this.level = level;
    }

    //Check if team follows the clause type
    public boolean validateTeam(List<Pokemon> team){
        for(Pokemon pokemon: team){
            if(pokemon.getPokemonLevel() < level){
                return false;
            }
        }
        return true;
    }
}
