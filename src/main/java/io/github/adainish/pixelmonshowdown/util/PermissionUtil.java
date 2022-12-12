package io.github.adainish.pixelmonshowdown.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.server.permission.PermissionAPI;

public class PermissionUtil {
    /**
     * Checks if the player has the permission
     * If the supplied CommandSource is not a player it checks if it is the server
     *
     * @param cs the CommandSource whose permission should be checked
     * @param perm the permission that should be checked
     * @return if the CommandSource has that permission or is the server
     */
    public static boolean checkPermAsPlayer(CommandSource cs, String perm) {
        try {
            return checkPerm(cs.asPlayer(), perm);
        } catch (CommandSyntaxException e) {
            return isServer(cs);
        }
    }

    /**
     * Checks if the player has the permission
     *
     * @param player who should be checked
     * @param perm the permission that should be checked
     * @return if the player has the permission
     */
    public static boolean checkPerm(ServerPlayerEntity player, String perm) {
        if (player == null)
            return false;
        if (perm == null)
            return false;
        if (perm.isEmpty())
            return true;
        return PermissionAPI.hasPermission(player, perm) || player.hasPermissionLevel(4);
    }

    /**
     * Checks if the supplied CommandSource is the server
     *
     * @param cs CommandSource that should be checked
     * @return if the CommandSource is the server or not
     */
    public static boolean isServer(CommandSource cs) {
        return cs.source == LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

}
