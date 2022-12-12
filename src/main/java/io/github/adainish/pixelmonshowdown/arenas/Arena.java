package io.github.adainish.pixelmonshowdown.arenas;

import io.github.adainish.pixelmonshowdown.util.DataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.configurate.serialize.SerializationException;

public class Arena {
    private String name;
    private ArenaLocation locationA;
    private ArenaLocation locationB;

    public Arena(String name, ArenaLocation locationA, ArenaLocation locationB){
        this.name = name;
        this.locationA = locationA;
        this.locationB = locationB;
    }

    public Arena(String name){
        this.name = name;
        this.locationA = new ArenaLocation();
        this.locationB = new ArenaLocation();
    }

    //Loads arena from the Arenas.conf configuration
    public void load(){
        String locAWorld = DataManager.getArenasNode().node("Arenas", name, "LocationA", "World").getString();
        double locAX = DataManager.getArenasNode().node("Arenas", name, "LocationA", "X").getDouble();
        double locAY = DataManager.getArenasNode().node("Arenas", name, "LocationA", "Y").getDouble();
        double locAZ = DataManager.getArenasNode().node("Arenas", name, "LocationA", "Z").getDouble();
        double locARX = DataManager.getArenasNode().node("Arenas", name, "LocationA", "RX").getDouble();
        double locARY = DataManager.getArenasNode().node("Arenas", name, "LocationA", "RY").getDouble();
        double locARZ = DataManager.getArenasNode().node("Arenas", name, "LocationA", "RZ").getDouble();
        this.locationA = new ArenaLocation(locAWorld, locAX, locAY, locAZ, locARX, locARY, locARZ);

        String locBWorld = DataManager.getArenasNode().node("Arenas", name, "LocationB", "World").getString();
        double locBX = DataManager.getArenasNode().node("Arenas", name, "LocationB", "X").getDouble();
        double locBY = DataManager.getArenasNode().node("Arenas", name, "LocationB", "Y").getDouble();
        double locBZ = DataManager.getArenasNode().node("Arenas", name, "LocationB", "Z").getDouble();
        double locBRX = DataManager.getArenasNode().node("Arenas", name, "LocationB", "RX").getDouble();
        double locBRY = DataManager.getArenasNode().node("Arenas", name, "LocationB", "RY").getDouble();
        double locBRZ = DataManager.getArenasNode().node("Arenas", name, "LocationB", "RZ").getDouble();
        this.locationB = new ArenaLocation(locBWorld, locBX, locBY, locBZ, locBRX, locBRY, locBRZ);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setLocationA(ArenaLocation location){
        this.locationA = location;
    }

    public ArenaLocation getLocationA(){
        return locationA;
    }

    public void setLocationB(ArenaLocation location){
        this.locationB = location;
    }

    public ArenaLocation getLocationB(){
        return locationB;
    }

    public boolean isLocationsFilled(){
        return (locationA.getUUID() != null && locationB.getUUID() != null);
    }

    //Saves an arena
    public void saveArena(){

        if(locationA.getWorld() != null) {
            Location locALoc = locationA.getLocation();
            Vector3d locAVec = locationA.getHeadRotation();

            try {
                DataManager.getArenasNode().node("Arenas", name, "LocationA", "World").set(locationA.getWorld());
                DataManager.getArenasNode().node("Arenas", name, "LocationA", "X").set(locALoc.getX());
                DataManager.getArenasNode().node("Arenas", name, "LocationA", "Y").set(locALoc.getY());
                DataManager.getArenasNode().node("Arenas", name, "LocationA", "Z").set(locALoc.getZ());
                DataManager.getArenasNode().node("Arenas", name, "LocationA", "RX").set(locAVec.getX());
                DataManager.getArenasNode().node("Arenas", name, "LocationA", "RY").set(locAVec.getY());
                DataManager.getArenasNode().node("Arenas", name, "LocationA", "RZ").set(locAVec.getZ());
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }

        }

        if(locationB.getWorld() != null) {
            Location locBLoc = locationB.getLocation();
            Vector3d locBVec = locationB.getHeadRotation();
            try {
                DataManager.getArenasNode().node("Arenas", name, "LocationB", "World").set(locationB.getWorld());
                DataManager.getArenasNode().node("Arenas", name, "LocationB", "X").set(locBLoc.getX());
                DataManager.getArenasNode().node("Arenas", name, "LocationB", "Y").set(locBLoc.getY());
                DataManager.getArenasNode().node("Arenas", name, "LocationB", "Z").set(locBLoc.getZ());
                DataManager.getArenasNode().node("Arenas", name, "LocationB", "RX").set(locBVec.getX());
                DataManager.getArenasNode().node("Arenas", name, "LocationB", "RY").set(locBVec.getY());
                DataManager.getArenasNode().node("Arenas", name, "LocationB", "RZ").set(locBVec.getZ());
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
