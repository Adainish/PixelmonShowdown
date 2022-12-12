package io.github.adainish.pixelmonshowdown.battles;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import io.github.adainish.pixelmonshowdown.PixelmonShowdown;
import io.github.adainish.pixelmonshowdown.arenas.Arena;
import io.github.adainish.pixelmonshowdown.arenas.ArenaLocation;
import io.github.adainish.pixelmonshowdown.arenas.ArenaManager;
import io.github.adainish.pixelmonshowdown.arenas.Location;
import io.github.adainish.pixelmonshowdown.queues.*;
import io.github.adainish.pixelmonshowdown.util.DataManager;
import io.github.adainish.pixelmonshowdown.util.StringUtil;
import io.github.adainish.pixelmonshowdown.util.UIHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.UUID;

public class MatchMakingManager {
    private static QueueManager queueManager = PixelmonShowdown.getInstance().queueManager;
    private static boolean isRunning = false;
    private static Task matchMake;
    private static final int INTERVAL = DataManager.getConfigNode().node("Queue-Management", "Match-Maker-Timer").getInt();
    private static final int WARM_UP = DataManager.getConfigNode().node("Queue-Management", "Battle-Preparation-Time").getInt();
    private static final int PREVIEW_TIME = DataManager.getConfigNode().node("Queue-Management", "Team-Preview-Time").getInt();
    private static final int BIAS_VALUE = DataManager.getConfigNode().node("Queue-Management", "Match-Maker-Bias-Value").getInt();
    private static final int MATCH_THRESHOLD = DataManager.getConfigNode().node("Queue-Management", "Match-Threshold-Value").getInt();
    private static final boolean ARENAS_ENABLED = DataManager.getConfigNode().node("Arena-Management", "Arenas-Enabled").getBoolean();

    public static void runTask() {
        if (!isRunning) {
            isRunning = true;
            matchMake = Task.builder().execute(MatchMakingManager::matchMake).interval(
                    20L * 60 * INTERVAL ).infinite().build();
        }
    }

    private static void matchMake() {
        Boolean[] continueMatching = {false};
        queueManager.getAllQueues().forEach((k, v) -> {
            if (v.getQueueSize() >= 2) {
                findMatch(v);
                continueMatching[0] = true;
            }
        });
        if(!continueMatching[0]){
            stopTask();
        }
    }

    public static void stopTask() {
        if (isRunning) {
            isRunning = false;
            matchMake.setExpired();
        }
    }

    private static void startPreBattle(UUID player1UUID, UUID player2UUID, CompetitiveFormat format){
        if(PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID) != null && PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID) != null){
            ServerPlayerEntity player1 = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID);
            ServerPlayerEntity player2 = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID);

            StringTextComponent textBattleStarting = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &aBattle starting in " + WARM_UP + "  seconds. Get ready!"));
            player1.sendMessage(textBattleStarting, player1UUID);
            player2.sendMessage(textBattleStarting, player2UUID);

            Pokemon[] player1Party = StorageProxy.getParty(player1).getAll();
            Pokemon[] player2Party = StorageProxy.getParty(player2).getAll();
            ArrayList<Pokemon> player1PokemonList = new ArrayList<>();
            ArrayList<Pokemon> player2PokemonList = new ArrayList<>();

            boolean player1PartyFainted = true;
            for (Pokemon value : player1Party) {
                if (value == null) {
                    continue;
                }
                player1PokemonList.add(value);
            }

            boolean player2PartyFainted = true;
            for (Pokemon pokemon : player2Party) {
                if (pokemon == null) {
                    continue;
                }
                player2PokemonList.add(pokemon);
            }

            if(format.isTeamPreview()){
                UIHandler player1UI = new UIHandler(player1);
                UIHandler player2UI = new UIHandler(player2);

                Task.builder().execute(() -> {
                    player1UI.openTeamPreview(player2UUID);
                    player2UI.openTeamPreview(player1UUID);
                }).delay(20L * 60 * (WARM_UP - PREVIEW_TIME)).build();

                Task.builder().execute(() -> {
                    UIManager.closeUI(player1);
                    UIManager.closeUI(player2);
                    Pokemon player1Starter = player1UI.getStartingPokemon();
                    Pokemon player2Starter = player2UI.getStartingPokemon();

                    MatchMakingManager.startBattle(player1UUID, player1PokemonList, player1Starter, player2UUID, player2PokemonList, player2Starter, format);
                }).delay(20L * 60 * WARM_UP).build();
            }
            else{
                Task.builder().execute(() -> {
                    UIManager.closeUI(player1);
                    UIManager.closeUI(player2);
                    MatchMakingManager.startBattle(player1UUID, player1PokemonList, null, player2UUID, player2PokemonList, null, format);
                }).delay(20L * 60 * WARM_UP).build();
            }
        }
    }

    //Start Battle between two players
    private static void startBattle(UUID player1UUID, ArrayList<Pokemon> player1Pokemon, Pokemon player1Starter,
                                    UUID player2UUID, ArrayList<Pokemon> player2Pokemon, Pokemon player2Starter, CompetitiveFormat format){

        CompetitiveQueue queue = queueManager.findQueue(format.getFormatName());
        queue.addPlayerInMatch(player1UUID);
        queue.addPlayerInMatch(player2UUID);

        if(PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID) != null && PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID) != null){
            ServerPlayerEntity player1 = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID);
            ServerPlayerEntity player2 = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID);

            //Check players are already in a battle
            if(BattleRegistry.getBattle(player1) != null || BattleRegistry.getBattle(player2) != null){
                StringTextComponent text = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6A participant is already in battle! Battle cancelled."));
                player1.sendMessage(text, player1UUID);
                player2.sendMessage(text, player2UUID);

                queue.remPlayerInMatch(player1UUID);
                queue.remPlayerInMatch(player2UUID);
                return;
            }

            Pokemon[] player1Party = StorageProxy.getParty(player1).getAll();
            Pokemon[] player2Party = StorageProxy.getParty(player2).getAll();
            ArrayList<Pokemon> player1PokemonList = new ArrayList<>();
            ArrayList<Pokemon> player2PokemonList = new ArrayList<>();

            //Check if either player's full party is fainted
            boolean player1PartyFainted = true;
            for (int i = 0; i < player1Party.length; i++) {
                if (player1Party[i] == null) {
                    continue;
                }
                if(player1Party[i].getHealth() != 0){
                    player1PartyFainted = false;
                }
                player1PokemonList.add(player1Party[i]);
            }

            boolean player2PartyFainted = true;
            for (Pokemon pokemon : player2Party) {
                if (pokemon == null) {
                    continue;
                }
                if (pokemon.getHealth() != 0) {
                    player2PartyFainted = false;
                }
                player2PokemonList.add(pokemon);
            }

            //Check if either player's party does not follow formats rules
            boolean player1Validates = format.getBattleRules().validateTeam(player1PokemonList) == null;

            boolean player2Validates = format.getBattleRules().validateTeam(player2PokemonList) == null;

            //Check that either player's party is the same as what they began match with
            boolean player1PartySame = isPartySame(player1Pokemon, player1PokemonList);
            boolean player2PartySame = isPartySame(player2Pokemon, player2PokemonList);

            //Check that either player is not already in battle
            boolean player1InBattle = BattleRegistry.getBattle(player1) != null;
            boolean player2InBattle = BattleRegistry.getBattle(player2) != null;

            //Check parties are same they matchmade with
            if(!player1PartySame || !player2PartySame) {
                StringTextComponent textDifferentParty = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6Your party is not the same as what you queue'd with!"));
                if(!player1PartySame){
                    player1.sendMessage(textDifferentParty, player1UUID);
                }
                if(!player2PartySame){
                    player2.sendMessage(textDifferentParty, player2UUID);
                }
                StringTextComponent textBattleCancelled = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6A participant's team was found ineligible! Battle cancelled."));
                player1.sendMessage(textBattleCancelled, player1UUID);
                player2.sendMessage(textBattleCancelled, player2UUID);

                queue.remPlayerInMatch(player1UUID);
                queue.remPlayerInMatch(player2UUID);
                return;
            }

            //Check that either player doesn't have fully fainted party
            if(player1PartyFainted || player2PartyFainted){

                StringTextComponent textPartyFainted = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6Your party is all fainted!"));

                if(player1PartyFainted){
                    player1.sendMessage(textPartyFainted, player1UUID);
                }
                if(player2PartyFainted){
                    player2.sendMessage(textPartyFainted, player2UUID);
                }


                StringTextComponent stringTextComponent = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6A participant's team was all fainted! Battle cancelled."));

                player1.sendMessage(stringTextComponent, player1UUID);
                player2.sendMessage(stringTextComponent, player2UUID);

                queue.remPlayerInMatch(player1UUID);
                queue.remPlayerInMatch(player2UUID);
                return;
            }

            //Check that either player's party doesn't break formats rules
            if(!player1Validates || !player2Validates){
                StringTextComponent textDoesNotValidate = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6Your party does not follow the formats rules!"));
                if(!player1Validates){
                    player1.sendMessage(textDoesNotValidate, player1UUID);
                }
                if(!player2Validates){
                    player2.sendMessage(textDoesNotValidate, player2UUID);
                }

                StringTextComponent textBattleCancelled = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6A participant's team did not follow the format's rules! Battle cancelled."));
                player1.sendMessage(textBattleCancelled, player1UUID);
                player2.sendMessage(textBattleCancelled, player2UUID);

                queue.remPlayerInMatch(player1UUID);
                queue.remPlayerInMatch(player2UUID);
                return;
            }

            //Check that either player isnt in battle
            if(player1InBattle || player2InBattle) {
                StringTextComponent textDoesNotValidate = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6You are already in battle!"));

                if(player1InBattle){
                    player1.sendMessage(textDoesNotValidate, player1UUID);
                }

                if(player2InBattle){
                    player2.sendMessage(textDoesNotValidate, player2UUID);
                }
                StringTextComponent textBattleCancelled = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6A participant is already in battle! Battle cancelled."));

                player1.sendMessage(textBattleCancelled, player1UUID);
                player2.sendMessage(textBattleCancelled, player2UUID);

                queue.remPlayerInMatch(player1UUID);
                queue.remPlayerInMatch(player2UUID);
                return;
            }

            //Get starting pokemon for battle
            PixelmonEntity participant1Starter;
            PixelmonEntity participant2Starter;
            if(player1Starter == null) {
                participant1Starter = StorageProxy.getParty(player1).getAndSendOutFirstAblePokemon(player1);
            }
            else{
                participant1Starter = player1Starter.getOrSpawnPixelmon(player1);
            }

            if(player2Starter == null){
                participant2Starter = StorageProxy.getParty(player2).getAndSendOutFirstAblePokemon(player2);
            }
            else{
                participant2Starter = player2Starter.getOrSpawnPixelmon(player2);
            }

            PlayerParticipant[] pp1 = {new PlayerParticipant(player1, participant1Starter)};
            PlayerParticipant[] pp2 = {new PlayerParticipant(player2, participant2Starter)};

            //Send player to Arena if enabled
            ArenaManager arenaManager = PixelmonShowdown.getInstance().arenaManager;
            if(ARENAS_ENABLED) {
                if (!arenaManager.isArenasFull()) {
                    Arena arena = arenaManager.addPlayers(player1, player2);
                    ArenaLocation locationA = arena.getLocationA();
                    ArenaLocation locationB = arena.getLocationB();

                    try {
                        Location locA = locationA.getLocation();
                        locA.teleport(player1);

                        Location locB = locationB.getLocation();
                        locB.teleport(player2);
                    }
                    catch(Exception e) {
                        PixelmonShowdown.getInstance().log.error("Error Teleporting to Arena");
                        e.printStackTrace();
                    }

                    BattleRules rules = format.getBattleRules();
                    BattleRegistry.startBattle(pp1, pp2, rules);
                }
                else{
                    player1.sendMessage(new StringTextComponent(StringUtil.formattedString("&4Arenas all full, battle commencing at distance.")), player1UUID);
                    player2.sendMessage(new StringTextComponent(StringUtil.formattedString("&4Arenas all full, battle commencing at distance.")), player2UUID);
                    BattleRules rules = format.getBattleRules();
                    BattleRegistry.startBattle(pp1, pp2, rules);
                }
            }
            else {
                BattleRules rules = format.getBattleRules();
                BattleRegistry.startBattle(pp1, pp2, rules);
            }
        }
        else{
            StringTextComponent playerNotFound = new StringTextComponent(StringUtil.formattedString("&f[&4Pixelmon Showdown&f] &6"));
//            Text playerNotFound = Text.of(TextColors.WHITE, "[", TextColors.RED, "Pixelmon Showdown", TextColors.WHITE,
//                    "]", TextColors.GOLD, " A player disconnected! Battle cancelled.");
            if(PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID) != null){
                PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player1UUID).sendMessage(playerNotFound, player1UUID);
            }
            if(PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID) != null){
                PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(player2UUID).sendMessage(playerNotFound, player2UUID);
            }
            queue.remPlayerInMatch(player1UUID);
            queue.remPlayerInMatch(player2UUID);
        }
    }

    public static void findMatch(CompetitiveQueue queue){
        //Create arraylist of players to remove from the queue to avoid iteration issues
        ArrayList<UUID> toRemove = new ArrayList<>();
        //Loop through all players in queue
        ArrayList<UUID> playersInQueue = queue.getPlayersInQueue();

        EloLadder ladder = queue.getLadder();

        for(UUID key: playersInQueue){
            //Make sure loop doesnt match players already set for removal
            if(toRemove.contains(key)){
                continue;
            }
            //Create variables for evaluating match quality
            EloProfile playerProfile = ladder.getProfile(key);
            //TimeVar that increases the longer player is in queue
            int timeVar = playerProfile.getTimeVar();
            int lowestMatchValue = -1;
            UUID bestOpponent = null;

            //Loop through players in queue again
            for(UUID secondKey: playersInQueue){
                //Avoid matching players already set for removal
                if(toRemove.contains(secondKey)){
                    continue;
                }

                //Keep player from matching with themselves (lol)
                if(!key.equals(secondKey)){
                    //Calculate quality of potential match
                    EloProfile oppProfile = ladder.getProfile(secondKey);
                    int matchValue = Math.abs(playerProfile.getElo() - oppProfile.getElo()) - timeVar;
                    //Check match quality is within threshold
                    if(matchValue <= MATCH_THRESHOLD){
                        //Check if match is best value
                        if(matchValue <= lowestMatchValue || lowestMatchValue == -1){
                            lowestMatchValue = matchValue;
                            bestOpponent = secondKey;
                        }
                    }
                }
            }
            //Check if there's an appropriate match
            if(bestOpponent != null){
                toRemove.add(bestOpponent);
                toRemove.add(key);
                EloProfile profile1 = ladder.getProfile(key);
                String player1Name = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(key).getName().getUnformattedComponentText();
                if(!profile1.getPlayerName().equals(player1Name)){
                    profile1.setPlayerName(player1Name);
                }
                player1Name = profile1.getPlayerName();

                EloProfile profile2 = ladder.getProfile(bestOpponent);
                String player2Name = PixelmonShowdown.getInstance().server.getPlayerList().getPlayerByUUID(bestOpponent).getName().getUnformattedComponentText();
                if(!profile2.getPlayerName().equals(player2Name)){
                    profile2.setPlayerName(player2Name);
                }
                player2Name = profile2.getPlayerName();

                MatchMakingManager.startPreBattle(key, bestOpponent, queue.getFormat());
            }
            else{
                //If no good match, increase TimeVar value
                playerProfile.setTimeVar(timeVar + BIAS_VALUE);
            }
        }
        for(UUID uuid: toRemove){
            queue.addPlayerInPreMatch(uuid);
        }
    }

    private static boolean isPartySame(ArrayList<Pokemon> party1, ArrayList<Pokemon> party2){
        if(party1.size() != party2.size()){
            return false;
        }
        else{
            for (Pokemon pokemon : party1) {
                if (!party2.contains(pokemon)) {
                    return false;
                }
            }
        }
        return true;
    }
}
