package io.github.adainish.pixelmonshowdown.arenas;

import com.pixelmonmod.pixelmon.api.util.helpers.DimensionHelper;
import io.github.adainish.pixelmonshowdown.util.WorldUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

public class Location {

    private World world;
    private double x;
    private double y;
    private double z;

    public Location(World world, double x, double y, double z)
    {
        this.setWorld(world);
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void teleport(ServerPlayerEntity target)
    {
        DimensionHelper.teleport(target, WorldUtil.getWorld(world.getDimensionKey()).get().getDimensionKey().getRegistryName().toString(), x, y, z);
    }

    public void teleport(ServerPlayerEntity target, String worldID, double x, double y, double z) {
        DimensionHelper.teleport(target, worldID, x, y, z);
    }
}
