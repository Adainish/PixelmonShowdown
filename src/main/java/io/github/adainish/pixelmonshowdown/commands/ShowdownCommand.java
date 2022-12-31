package io.github.adainish.pixelmonshowdown.commands;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.util.PermissionUtil;
import io.github.adainish.pixelmonshowdown.util.UIHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ShowdownCommand {
    public static LiteralArgumentBuilder<CommandSource> getCommand() {
        return Commands.literal("showdown")
                .requires(cs -> PermissionUtil.checkPermAsPlayer(cs, PixelmonShowdown.getInstance().permissionWrapper.showdownPermission))
                .executes(cc -> {
                    try {
                        UIHandler uiHandler = new UIHandler(cc.getSource().asPlayer());
                        UIManager.openUIForcefully(cc.getSource().asPlayer(), uiHandler.MainPage(cc.getSource().asPlayer().getUniqueID()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        cc.getSource().sendFeedback(new StringTextComponent("Something went wrong using this command!"), true);
                    }

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("reload")
                        .requires(cs -> PermissionUtil.checkPermAsPlayer(cs, PixelmonShowdown.getInstance().permissionWrapper.adminPermission))
                        .executes(cc -> {
                            try {
                                PixelmonShowdown.getInstance().reload();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            return 1;
                        })
                )
                ;
    }
}
