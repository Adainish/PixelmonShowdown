package io.github.adainish.pixelmonshowdown.arenas;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.util.WorldUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.UUID;

public class ArenaLocation {
    private UUID uuid = null;
    private String world = null;
    private double x;
    private double y;
    private double z;
    private double rX;
    private double rY;
    private double rZ;
    private Location returnLocation;
    private Vector3d returnHeadRotation;

    public ArenaLocation(String world, double x, double y, double z, double rX, double rY, double rZ){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rX = rX;
        this.rY = rY;
        this.rZ = rZ;
    }

    public ArenaLocation(){}

    public UUID getUUID(){
        return uuid;
    }

    public void setUUID(UUID uuid){
        this.uuid = uuid;
    }

    public boolean hasUUID(UUID compUUID){
        if(uuid == null){
            return false;
        }
        else if(uuid.equals(compUUID)){
            return true;
        }
        else{
            return false;
        }
    }

    public void setWorld(String world){
        this.world = world;
    }

    public String getWorld(){
        return world;
    }



    public Location getLocation(){
        World world = WorldUtil.getBasicWorld();
        if (WorldUtil.getWorld(this.world).isPresent())
            world = WorldUtil.getWorld(this.world).get();
        return new Location(world
                , x, y, z);
    }

    public void setLocation(BlockPos location){
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public void setReturnLocation(World world, BlockPos location){
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.returnLocation = new Location(world, location.getX(), location.getY(), location.getZ());
    }


    public Location getReturnLocation()
    {
        return this.returnLocation;
    }


    public void setHeadRotation(Vector3d headRotation){
        this.rX = headRotation.getX();
        this.rY = headRotation.getY();
        this.rZ = headRotation.getZ();
    }

    public void setReturnHeadRotation(Vector3d headRotation){
        this.returnHeadRotation = headRotation;
    }



    public Vector3d getHeadRotation(){
        return new Vector3d(rX, rY, rZ);
    }

    public Vector3d getReturnHeadRotation(){
        return returnHeadRotation;
    }
}
