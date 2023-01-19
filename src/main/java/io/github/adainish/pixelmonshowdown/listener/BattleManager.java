package io.github.adainish.pixelmonshowdown.listener;

import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import com.pixelmonmod.pixelmon.api.battles.BattleEndCause;
import com.pixelmonmod.pixelmon.api.battles.BattleResults;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.arenas.Arena;
import io.github.adainish.pixelmonshowdown.arenas.ArenaLocation;
import io.github.adainish.pixelmonshowdown.arenas.ArenaManager;
import io.github.adainish.pixelmonshowdown.arenas.Location;
import io.github.adainish.pixelmonshowdown.queues.*;
import io.github.adainish.pixelmonshowdown.util.DataManager;
import io.github.adainish.pixelmonshowdown.util.StringUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;


public class BattleManager {
    private static final boolean ARENAS_ENABLED = DataManager.getConfigNode().node("Arena-Management", "Arenas-Enabled").getBoolean();

    @SubscribeEvent
    public void onBattleEnd(BattleEndEvent event) {
        if (event.getPlayers().size() == 0)
            return;
        if (event.getResults().keySet().toArray()[0] instanceof WildPixelmonParticipant || event.getResults().keySet().toArray()[1] instanceof WildPixelmonParticipant)
            return;

        if (event.getResults().keySet().toArray()[0] instanceof TrainerParticipant || event.getResults().keySet().toArray()[1] instanceof TrainerParticipant)
            return;
        //Get both participants in battle

        BattleParticipant bParticipant1 = (BattleParticipant) event.getResults().keySet().toArray()[0];
        BattleParticipant bParticipant2 = (BattleParticipant) event.getResults().keySet().toArray()[1];

        PlayerParticipant participant1 = (PlayerParticipant) bParticipant1;
        PlayerParticipant participant2 = (PlayerParticipant) bParticipant2;
        UUID player1UUID = participant1.getEntity().getUniqueID();
        UUID player2UUID = participant2.getEntity().getUniqueID();

        ServerPlayerEntity player1 = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID);
        ServerPlayerEntity player2 = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID);

        //Check if both players are online
        if (player1 != null || player2 != null) {
            QueueManager queueManager = PixelmonShowdown.getInstance().queueManager;

            //Check if both players are in a match
            if (queueManager.isPlayerInMatch(player1UUID) && queueManager.isPlayerInMatch(player2UUID)) {
                if (queueManager.findPlayerInMatch(player1UUID) != null) {
                    CompetitiveQueue queue = queueManager.findPlayerInMatch(player1UUID);

                    CompetitiveFormat format = queue.getFormat();

                    if (format.isRandomBattle())
                    {
                        //wipe rental pokemon for both players
                        PlayerPartyStorage ppsOne = StorageProxy.getParty(player1UUID);
                        PlayerPartyStorage ppsTwo = StorageProxy.getParty(player2UUID);
                        for (int i = 0; i < 6; i++) {
                            if (ppsOne.get(i) != null)
                            {
                                ppsOne.set(i, null);
                            }
                        }
                        for (int i = 0; i < 6; i++) {
                            if (ppsTwo.get(i) != null)
                            {
                                ppsTwo.set(i, null);
                            }
                        }
                        //add first mon from pc to party in first slot
                        PCStorage pcOne = StorageProxy.getPCForPlayer(player1UUID);
                        for (Pokemon p:pcOne.getAll()) {
                            ppsOne.add(p);
                            pcOne.set(p.getPosition(), null);
                            break;
                        }
                        PCStorage pcTwo = StorageProxy.getPCForPlayer(player2UUID);
                        for (Pokemon p:pcTwo.getAll()) {
                            ppsTwo.add(p);
                            pcTwo.set(p.getPosition(), null);
                            break;
                        }

                    }
                    EloLadder ladder = queue.getLadder();
                    //Check if both players are in match in the same format
                    if (queue.hasPlayerInMatch(player2UUID)) {
                        //Check if battle end was normal or not
                        if (!event.isAbnormal() && event.getCause() != BattleEndCause.FORCE) {
                            //Add players as active for auto saving
                            ladder.addAsActive(player1UUID);
                            ladder.addAsActive(player2UUID);

                            EloProfile eloWinner;
                            EloProfile eloLoser;
                            ServerPlayerEntity winner;
                            ServerPlayerEntity loser;

                            //Determine winner and loser
                            if (event.getResults().get(bParticipant1) == BattleResults.VICTORY) {
                                winner = player1;
                                eloWinner = ladder.getProfile(player1UUID);
                                loser = player2;
                                eloLoser = ladder.getProfile(player2UUID);
                            } else if (event.getResults().get(bParticipant2) == BattleResults.VICTORY) {
                                winner = player2;
                                eloWinner = ladder.getProfile(player2UUID);
                                loser = player1;
                                eloLoser = ladder.getProfile(player1UUID);
                            } else {
                                player1.sendMessage(new StringTextComponent(StringUtil.formattedString("&f[&cPixelmon Showdown&f] &6Tie! Elo remains unchanged")), player1UUID);
                                player2.sendMessage(new StringTextComponent(StringUtil.formattedString("&f[&cPixelmon Showdown&f] &6Tie! Elo remains unchanged")), player2UUID);
                                //Remove players from match
                                queue.remPlayerInMatch(player1UUID);
                                queue.remPlayerInMatch(player2UUID);
                                //Update ladders for leaderboard
                                ladder.updatePlayer(player1UUID);
                                ladder.updatePlayer(player2UUID);
                                return;
                            }

                            int winnerElo = eloWinner.getElo();
                            int loserElo = eloLoser.getElo();

                            //Adjust elos for each player
                            eloWinner.addWin(loserElo);
                            eloLoser.addLoss(winnerElo);

                            int newWinnerElo = eloWinner.getElo();
                            int newLoserElo = eloLoser.getElo();

                            //Send player message for with their adjusted elo

                            StringTextComponent textWin = new StringTextComponent(StringUtil.formattedString("&f[&cPixelmon Showdown&f] &6Victory! [Elo: %winnerelo% > %newElo%]".replace("%winnerelo%", String.valueOf(winnerElo))
                                    .replace("%newElo%", String.valueOf(newWinnerElo))));

                            StringTextComponent textLoss = new StringTextComponent(StringUtil.formattedString("&f[&cPixelmon Showdown&f] &6Defeat! [Elo: %loserelo% > %newElo%]".replace("%loserelo%", String.valueOf(loserElo))
                                    .replace("%newElo%", String.valueOf(newLoserElo))));

                            winner.sendMessage(textWin, winner.getUniqueID());
                            loser.sendMessage(textLoss, loser.getUniqueID());

                            //Remove players from match
                            queue.remPlayerInMatch(player1UUID);
                            queue.remPlayerInMatch(player2UUID);
                            //Update ladders for leaderboard
                            ladder.updatePlayer(player1UUID);
                            ladder.updatePlayer(player2UUID);

                            if (ARENAS_ENABLED) {
                                remFromArena(player1, player1UUID, player2, player2UUID);
                            }
                        } else {
                            //Remove player from arena before task to reduce clunkiness
                            if (ARENAS_ENABLED) {
                                remFromArena(player1, player1UUID, player2, player2UUID);
                            }
                            //Create task due to some odd battle crashes seemingly not triggering battle end or disconnect event
                            Task.builder().execute(task -> {
                                //Check if both players are present
                                if (PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID) != null ||
                                        PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID) != null) {
                                    //If both players aren't present, it will give win to whoever is still connected
                                    //This is to deter players trying to intentionally caused forced battle ends to prevent loss
                                    //Add players as active for auto saving
                                    ladder.addAsActive(player1UUID);
                                    ladder.addAsActive(player2UUID);

                                    EloProfile player1Profile = ladder.getProfile(player1UUID);
                                    EloProfile player2Profile = ladder.getProfile(player2UUID);

                                    //Give win to player 2 if player 1 disconnected, vice versa if player 2 diconnected
                                    if (PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID) == null) {
                                        int player2Elo = player2Profile.getElo();
                                        player2Profile.addWin(player1Profile.getElo());
                                        player1Profile.addLoss(player2Elo);

                                        StringTextComponent textWin = new StringTextComponent(StringUtil.formattedString("&f[&cPixelmon Showdown&f] &6Victory! &a[Elo: %playerelo% > %newelo%]"
                                                .replace("%playerelo%", String.valueOf(player2Elo))
                                                .replace("%newelo%", String.valueOf(player2Profile.getElo()))
                                        ));
                                        player2.sendMessage(textWin, player2UUID);
                                        ladder.updatePlayer(player1UUID);
                                        ladder.updatePlayer(player2UUID);

                                    } else if (PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID) == null) {
                                        int player1Elo = player1Profile.getElo();
                                        player1Profile.addWin(player2Profile.getElo());
                                        player2Profile.addLoss(player1Elo);

                                        StringTextComponent textWin = new StringTextComponent(StringUtil.formattedString("&f[&cPixelmon Showdown&f] &6Victory! &a[Elo: %playerelo% > %newelo%]"
                                                .replace("%playerelo%", String.valueOf(player1Elo))
                                                .replace("%newelo%", String.valueOf(player1Profile.getElo()))
                                        ));
                                        player1.sendMessage(textWin, player1UUID);
                                        ladder.updatePlayer(player1UUID);
                                        ladder.updatePlayer(player2UUID);
                                    }
                                } else {
                                    //If both players are still connected, simply do not assign elo points.

                                    StringTextComponent textUnexpected = new StringTextComponent(StringUtil.formattedString("&f[&cPixelmon Showdown&f] &6Unexpected battle end! No points awarded."));

                                    player1.sendMessage(textUnexpected, player1UUID);
                                    player2.sendMessage(textUnexpected, player2UUID);

                                }
                                queue.remPlayerInMatch(player1UUID);
                                queue.remPlayerInMatch(player2UUID);
                                if (BattleRegistry.getBattle(player1) != null) {
                                    BattleRegistry.getBattle(player1).endBattle();
                                }
                                if (BattleRegistry.getBattle(player2) != null) {
                                    BattleRegistry.getBattle(player2).endBattle();
                                }
                            }).iterations(1).delay(1).build();
                        }
                    }
                }
            }
        }
    }

    //If player quits, remove them from any queue they were in
    @SubscribeEvent
    public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        QueueManager queueManager = PixelmonShowdown.getInstance().queueManager;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        UUID playerUUID = player.getUniqueID();

        if(queueManager.isPlayerInAny(playerUUID)){
            queueManager.findPlayerInAny(playerUUID).remPlayerInAny(playerUUID);
        }
    }

    //Removes players from an arena
    private void remFromArena(ServerPlayerEntity player1, UUID player1UUID, ServerPlayerEntity player2, UUID player2UUID){
        if(ARENAS_ENABLED){
            ArenaManager arenaManager = PixelmonShowdown.getInstance().arenaManager;
            Arena arena = arenaManager.getArena(player1UUID, player2UUID);

            //Check if arena doesn't exist (arena wasnt found)
            if(arena != null){
                ArenaLocation locA = arena.getLocationA();
                ArenaLocation locB = arena.getLocationB();

                //Return players to their original location
                if(locA.hasUUID(player1UUID)){
                    Location player1ReturnLocation = locA.getReturnLocation();
                    player1ReturnLocation.teleport(player1);

                }
                else if(locA.hasUUID(player2UUID)){
                    Location player2ReturnLocation = locA.getReturnLocation();
                    player2ReturnLocation.teleport(player2);
                }

                if(locB.hasUUID(player1UUID)){
                    Location player1ReturnLocation = locB.getReturnLocation();
                    player1ReturnLocation.teleport(player1);
                }
                else if(locB.hasUUID(player2UUID)){
                    Location player2ReturnLocation = locB.getReturnLocation();
                    player2ReturnLocation.teleport(player2);
                }

                //Clear arena
                arenaManager.remPlayers(player1UUID, player2UUID);
            }
        }
    }
}
