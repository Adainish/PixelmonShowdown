package io.github.adainish.pixelmonshowdown.arenas;

import io.github.adainish.pixelmonshowdown.util.DataManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.UUID;

public class ArenaManager {
    private ArrayList<Arena> arenas = new ArrayList<>();

    public ArrayList<Arena> getArenas(){
        return arenas;
    }

    //Load arenas from Arenas.conf configuration
    public void loadArenas(){
        arenas.clear();
        if(DataManager.getConfigNode().node("Arena-Management", "Arenas-Enabled").getBoolean()){
            DataManager.getArenasNode().node("Arenas").childrenMap().forEach((k,v) -> {
                Arena arena = new Arena(k.toString());
                arena.load();
                arenas.add(arena);
            });
        }
    }

    //Check if all arenas are full
    public boolean isArenasFull(){
        for(int i = 0; i < arenas.size(); i++){
            if(!arenas.get(i).isLocationsFilled()){
                return false;
            }
        }
        return true;
    }

    //Add players to arena
    public Arena addPlayers(ServerPlayerEntity player1, ServerPlayerEntity player2){
        for(int i = 0; i < arenas.size(); i++){
            if(!arenas.get(i).isLocationsFilled()){
                ArenaLocation locA = arenas.get(i).getLocationA();
                ArenaLocation locB = arenas.get(i).getLocationB();
                locA.setUUID(player1.getUniqueID());
                locA.setReturnLocation(player1.getServerWorld(), player1.getPosition());
//                locA.setReturnHeadRotation(new V);

                locB.setUUID(player2.getUniqueID());
                locB.setReturnLocation(player2.getServerWorld(), player2.getPosition());
//                locB.setReturnHeadRotation(player2.getHeadRotation());
                return arenas.get(i);
            }
        }
        return null;
    }

    //Get arena from arena name
    public Arena getArena(String arenaName){
        for(int i = 0; i < arenas.size(); i++){
            if(arenas.get(i).getName().equals(arenaName)){
                return arenas.get(i);
            }
        }
        return null;
    }

    //Remove players from arena
    public void remPlayers(UUID player1, UUID player2){
        for(int i = 0; i < arenas.size(); i++){
            //Check if arena is filled
            if(arenas.get(i).isLocationsFilled()){
                ArenaLocation locA = arenas.get(i).getLocationA();
                //Clear location if location has player uuid
                if(locA.getUUID().equals(player1) || locA.getUUID().equals(player2)){
                    locA.setUUID(null);
                }

                //Clear location if location has player uuid
                ArenaLocation locB = arenas.get(i).getLocationB();
                if(locB.getUUID().equals(player1) || locB.getUUID().equals(player2)){
                    locB.setUUID(null);
                }
            }
        }
    }

    //Get Arena players are in
    public Arena getArena(UUID player1, UUID player2){
        for(int i = 0; i < arenas.size(); i++){
            boolean matchA = false;
            boolean matchB = false;
            if(arenas.get(i).isLocationsFilled()){
                ArenaLocation locA = arenas.get(i).getLocationA();
                if(locA.getUUID().equals(player1) || locA.getUUID().equals(player2)){
                    matchA = true;
                }

                ArenaLocation locB = arenas.get(i).getLocationB();
                if(locB.getUUID().equals(player1) || locB.getUUID().equals(player2)){
                    matchB = true;
                }
                if(matchA && matchB){
                    return arenas.get(i);
                }
            }
        }
        return null;
    }

    //Add arena from Arenas.conf configuration
    public void addArena(){
        String arenaName = "Arena " + (arenas.size() + 1);
        Arena newArena = new Arena(arenaName);
        try {
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationA", "X").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationA", "Y").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationA", "Z").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationA", "RX").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationA", "RY").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationA", "RZ").set(0);

            DataManager.getArenasNode().node("Arenas", arenaName, "LocationB", "X").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationB", "Y").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationB", "Z").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationB", "RX").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationB", "RY").set(0);
            DataManager.getArenasNode().node("Arenas", arenaName, "LocationB", "RZ").set(0);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        arenas.add(newArena);
    }

    //Sort arenas by arena number
    public void sortArenas(){
        ArrayList<Arena> newArenas = new ArrayList<>();
        ArrayList<Arena> ignoreArenas = new ArrayList<>();
        for(int i = 0; i < arenas.size(); i++){
            Arena lowestArena = null;
            int lowestNum = -1;
            for(int k = 0; k < arenas.size(); k++){
                String arenaName = arenas.get(k).getName();
                String[] splitName = arenaName.split("Arena ");
                if((Integer.parseInt(splitName[1]) < lowestNum || lowestNum == -1)
                        && !ignoreArenas.contains(arenas.get(k))){
                    lowestNum = Integer.parseInt(splitName[1]);
                    lowestArena = arenas.get(k);
                }
            }
            if(lowestArena != null){
                newArenas.add(lowestArena);
                ignoreArenas.add(lowestArena);
            }
        }
        arenas = newArenas;
    }
}
