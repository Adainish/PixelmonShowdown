package io.github.adainish.pixelmonshowdown.commands;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.util.PermissionUtil;
import io.github.adainish.pixelmonshowdown.util.UIHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ArenaCommand {
    public static LiteralArgumentBuilder<CommandSource> getCommand() {
        return Commands.literal("arenas")
                .requires(cs -> PermissionUtil.checkPermAsPlayer(cs, PixelmonShowdown.getInstance().permissionWrapper.arenasPermission))
                .executes(cc -> {
                    try {
                        UIHandler uiHandler = new UIHandler(cc.getSource().asPlayer());
                        UIManager.openUIForcefully(cc.getSource().asPlayer(), uiHandler.ViewArenasGUI());
                    } catch (Exception e) {
                        cc.getSource().sendFeedback(new StringTextComponent(e.getMessage()), true);
                    }

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                });
    }
}
