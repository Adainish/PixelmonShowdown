package io.github.adainish.pixelmonshowdown.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.util.PermissionUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class DisplayCommand {
    public static LiteralArgumentBuilder<CommandSource> getCommand() {
        return Commands.literal("display")
                .requires(cs -> PermissionUtil.checkPermAsPlayer(cs, PixelmonShowdown.getInstance().permissionWrapper.displayPermission))
                .executes(cc -> {
                    try {
                        cc.getSource().sendFeedback(new StringTextComponent("Unknown format. Usage: /display <type> <format..>"), true);
                    } catch (Exception e) {
                        cc.getSource().sendFeedback(new StringTextComponent(""), true);
                    }

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("type", StringArgumentType.string())

                )
                ;
    }
}
