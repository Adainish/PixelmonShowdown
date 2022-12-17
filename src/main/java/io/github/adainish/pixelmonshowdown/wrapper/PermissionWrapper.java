package io.github.adainish.pixelmonshowdown.wrapper;

import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Level;

public class PermissionWrapper {

    public String adminPermission = "pixelmonshowdown.admin";
    public String arenasPermission = "pixelmonshowdown.admin.command.arenas";

    public String showdownPermission = "pixelmonshowdown.user.command.pixelmonshowdown";

    public String displayPermission = "pixelmonshowdown.user.command.display";

    public String openLeaderBoardPermission = "pixelmonshowdown.user.action.openleaderboard";

    public String openStatsGUI = "pixelmonshowdown.user.action.openstats";

    public String openQueueGUI = "pixelmonshowdown.user.action.openqueue";

    public String openRulesPermission = "pixelmonshowdown.user.action.openrules";
    public PermissionWrapper() {
        registerPermissions();
    }
    public void registerPermissions() {
        registerCommandPermission(adminPermission, "The admin permission");
        registerCommandPermission(arenasPermission);
        registerCommandPermission(showdownPermission);
        registerCommandPermission(displayPermission);
        registerCommandPermission(openLeaderBoardPermission);
        registerCommandPermission(openStatsGUI);
        registerCommandPermission(openQueueGUI);
        registerCommandPermission(openRulesPermission);
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
