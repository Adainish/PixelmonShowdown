package io.github.adainish.pixelmonshowdown.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.queues.CompetitiveQueue;
import io.github.adainish.pixelmonshowdown.queues.EloLadder;
import io.github.adainish.pixelmonshowdown.queues.EloProfile;
import io.github.adainish.pixelmonshowdown.queues.QueueManager;
import io.github.adainish.pixelmonshowdown.util.PermissionUtil;
import io.github.adainish.pixelmonshowdown.util.StringUtil;
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
                        return 1;
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("profile")
                        .executes(cc ->
                        {
                            cc.getSource().sendFeedback(new StringTextComponent(StringUtil.formattedString("&cUnknown format. Usage: /display <type> <format..>")), false);
                            return 1;
                        })
                        .then(Commands.argument("format", StringArgumentType.string())
                                .executes(cc -> {
                                    QueueManager queueManager = PixelmonShowdown.getInstance().queueManager;
                                    if (queueManager.findQueue(StringArgumentType.getString(cc, "format")) != null) {
                                        CompetitiveQueue queue = queueManager.findQueue(StringArgumentType.getString(cc, "format"));
                                        EloLadder ladder = queue.getLadder();

                                        if (ladder.getProfile(cc.getSource().asPlayer().getUniqueID()) != null) {
                                            EloProfile profile = ladder.getProfile(cc.getSource().asPlayer().getUniqueID());

                                            StringTextComponent text = new StringTextComponent
                                                    (
                                                            StringUtil.formattedString("&fPlayer: %playername% \n &6Elo: %elo% \n&aWins: %wins% \n &cLosses: %losses%\n &bWinrate: %winrate%"
                                                                    .replace("%playername%", profile.getPlayerName())
                                                                    .replace("%elo%", String.valueOf(profile.getElo()))
                                                                    .replace("wins%", String.valueOf(profile.getWins()))
                                                                    .replace("%losses%", String.valueOf(profile.getLosses()))
                                                                    .replace("%winrate%", String.valueOf(profile.getWinRate()))
                                                            )
                                                    );
                                            cc.getSource().sendFeedback(text, false);
                                        } else {
                                            cc.getSource().sendFeedback(new StringTextComponent(StringUtil.formattedString("&cNo Player Stats Recorded!")), false);
                                        }
                                        return 1;
                                    }
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("leaderboard")
                        .executes(cc -> {
                            cc.getSource().sendFeedback(new StringTextComponent(StringUtil.formattedString("&cUnknown format. Usage: /display <type> <format..>")), false);
                            return 1;
                        })
                        .then(Commands.argument("format", StringArgumentType.string())
                                .executes(cc -> {
                                    QueueManager queueManager = PixelmonShowdown.getInstance().queueManager;
                                    if (queueManager.findQueue(StringArgumentType.getString(cc, "format")) != null) {
                                        CompetitiveQueue queue = queueManager.findQueue(StringArgumentType.getString(cc, "format"));
                                        EloLadder ladder = queue.getLadder();

                                        if (ladder.getLadderSize() > 0) {
                                            String format = "&fFormat: " + queue.getFormat().getFormatName();
                                            StringBuilder stringBuilder = new StringBuilder(format);

                                            if (ladder.getLadderSize() >= 5) {
                                                for (int i = 0; i < 5; i++) {
                                                    EloProfile profile = ladder.getProfile(i);
                                                    String string = "\n &6" + (i + 1) + ". " + profile.getPlayerName() + " - &a(" + profile.getElo() + ")";
                                                    stringBuilder.append(string);
                                                }
                                            } else {
                                                for (int i = 0; i < ladder.getLadderSize(); i++) {
                                                    EloProfile profile = ladder.getProfile(i);
                                                    String string = "\n &6" + (i + 1) + ". " + profile.getPlayerName() + " - &a(" + profile.getElo() + ")";
                                                    stringBuilder.append(string);
                                                }
                                            }
                                            cc.getSource().sendFeedback(new StringTextComponent(StringUtil.formattedString(stringBuilder.toString())), false);
                                            return 1;
                                        }
                                    } else {
                                        cc.getSource().sendFeedback(new StringTextComponent(StringUtil.formattedString("&cNo Stats Recorded.")), false);
                                    }
                                    return 1;
                                })
                        )
                )
                ;
    }
}
