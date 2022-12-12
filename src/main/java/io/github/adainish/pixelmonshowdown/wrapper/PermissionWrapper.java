package io.github.adainish.pixelmonshowdown.wrapper;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Level;

public class PermissionWrapper {

    public static String adminPermission = "pixelmonshowdown.admin";
    public PermissionWrapper() {
        registerPermissions();
    }
    public void registerPermissions() {
        registerCommandPermission(adminPermission, "The admin permission");
    }
    public static void registerCommandPermission(String s) {
        if (s == null || s.isEmpty()) {
            PixelmonShowdown.getInstance().log.log(Level.FATAL, "Trying to register a permission node failed, please check any configs for null/empty Configs");
            return;
        }
        PermissionAPI.registerNode(s, DefaultPermissionLevel.NONE, s);
    }

    public static void registerCommandPermission(String s, String description) {
        if (s == null || s.isEmpty()) {
            PixelmonShowdown.getInstance().log.log(Level.FATAL, "Trying to register a permission node failed, please check any configs for null/empty Configs");
            return;
        }
        PermissionAPI.registerNode(s, DefaultPermissionLevel.NONE, description);
    }
}
