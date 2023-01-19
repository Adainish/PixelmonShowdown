package io.github.adainish.pixelmonshowdown.queues;

import io.github.adainish.pixelmonshowdown.util.DataManager;
import io.github.adainish.pixelmonshowdown.util.Util;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EloProfile {
    private final static boolean IS_PERSISTENT = DataManager.getConfigNode().node("Elo-Management", "K-Factor", "K-Factor-Persistent").getBoolean();
    private final static double K_FACTOR_LOW_ELO = DataManager.getConfigNode().node("Elo-Management", "K-Factor", "K-Factor-Low-Elo").getDouble();
    private final static double K_FACTOR_MID_ELO = DataManager.getConfigNode().node("Elo-Management", "K-Factor", "K-Factor-Mid-Elo").getDouble();
    private final static double K_FACTOR_HIGH_ELO = DataManager.getConfigNode().node("Elo-Management", "K-Factor", "K-Factor-High-Elo").getDouble();
    private final static double K_FACTOR_PERSISTENT = DataManager.getConfigNode().node("Elo-Management", "K-Factor", "K-Factor-Persistent-Value").getDouble();
    public final static int ELO_FLOOR = DataManager.getConfigNode().node("Elo-Management", "Elo-Range", "Elo-Floor").getInt();
    private final static int LOW_ELO_RANGE = DataManager.getConfigNode().node("Elo-Management", "Elo-Range", "Low-Elo-Range").getInt();
    private final static int HIGH_ELO_RANGE = DataManager.getConfigNode().node("Elo-Management", "Elo-Range", "High-Elo-Range").getInt();

    private UUID uuid;
    private String formatName;
    private int elo;
    private int wins;
    private int losses;
    private double winRate;
    private long lastQueue;
    private int timeVar;
    private String playerName;

    public EloProfile(UUID uuid, String formatName) {
        this.uuid = uuid;
        this.playerName = " ";
        this.formatName = formatName;
        this.elo = ELO_FLOOR;
        this.lastQueue = 0;
        this.wins = 0;
        this.losses = 0;
        this.winRate = 0.0;
        this.timeVar = 0;
    }

    /**
     * Loads Elo Profile from Elo Config
     */
    public void loadProfile() {
        String loadedPlayerName = DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Name").getString();
        int loadedElo = DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Elo").getInt();
        int loadedWins = DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Wins").getInt();
        int loadedLosses = DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Losses").getInt();
        long loadedLastQueue = DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "LastQueue").getLong();
        this.playerName = loadedPlayerName;
        this.elo = loadedElo;
        this.wins = loadedWins;
        this.losses = loadedLosses;
        this.lastQueue = loadedLastQueue;
        this.winRate = loadedWins + loadedLosses == 0 ? 0.0 : Math.round(wins * 100.0 / (wins + losses));
    }

    public long getCooldownLong(CompetitiveFormat format)
    {
        long currentTime = System.currentTimeMillis();
        return (lastQueue + TimeUnit.MINUTES.toMillis(format.getCooldown())) - currentTime;
    }

    public String getCooldownString(CompetitiveFormat format)
    {
        if (format.getCooldown() == 0)
            return "No active cooldown";
        long currentTime = System.currentTimeMillis();
        long cd = getCooldownLong(format) - currentTime;
        long hours = cd / Util.HOUR_IN_MILLIS;
        cd = cd - (hours * Util.HOUR_IN_MILLIS);
        long minutes = cd / Util.MINUTE_IN_MILLIS;
        cd = cd - (minutes * Util.MINUTE_IN_MILLIS);
        long seconds = cd / Util.SECOND_IN_MILLIS;
        return hours + " Hours " + minutes + " Minutes " + seconds + " Seconds";
    }

    public boolean onCooldown(CompetitiveFormat format)
    {
        if (format.getCooldown() == 0)
            return false;
        if (lastQueue <= 0)
            return false;
        long cooldownUntil = lastQueue + TimeUnit.MINUTES.toMillis(format.getCooldown());
        return cooldownUntil > System.currentTimeMillis();
    }

    public void setPlayerName(String newPlayerName) {
        this.playerName = newPlayerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    //Saves Elo Profile in Elo Config Object
    public void saveProfile(){
        try {
            DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Name").set(playerName);
            DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Elo").set(elo);
            DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Wins").set(wins);
            DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "LastQueue").set(lastQueue);
            DataManager.getElosNode().node("Player-Elos", formatName, uuid.toString(), "Losses").set(losses);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
;
    }

    //Returns UUID
    public UUID getUUID() {
        return uuid;
    }

    //Returns Elo
    public int getElo() {
        return elo;
    }

    //Returns Wins
    public int getWins() {
        return wins;
    }

    //Returns Losses
    public int getLosses() {
        return losses;
    }

    //Returns Win Rate
    public double getWinRate() {
        return winRate;
    }

    //Returns time variable used in matchmaking
    public int getTimeVar() {
        return timeVar;
    }

    //Sets time variable used in matchmaking
    public void setTimeVar(int newTimeVar) {
        this.timeVar = newTimeVar;
    }

    //Sets wins, losses, and winrate back to zero but retains elo
    public void resetWL() {
        this.wins = 0;
        this.losses = 0;
        this.winRate = 0.0;
    }

    //Adds win to player and adjusts elo according to their opponent
    public void addWin(int oppElo) {
        double kFactor = getKFactor();

        int newElo = (int) (Math.round((elo + kFactor * (1.0 - getExpectedOutcome(oppElo)))));
        //Set elo back to floor if it's below
        this.elo = Math.max(newElo, ELO_FLOOR);
        this.wins++;

        this.winRate = Math.round(wins * 100.0 / (wins + losses));
    }

    //Adds loss to player and adjusts elo according to their opponent
    public void addLoss(int oppElo) {
        double kFactor = getKFactor();

        int newElo = (int) (Math.round((elo + kFactor * (0.0 - getExpectedOutcome(oppElo)))));
        //Set elo back to floor if it's below
        this.elo = Math.max(newElo, ELO_FLOOR);

        this.losses++;

        this.winRate = Math.round(wins * 100.0 / (wins + losses));
    }

    //Gets KFactor used to adjust elos after a win or loss
    private double getKFactor() {
        double kFactor;

        if(IS_PERSISTENT){
            kFactor = K_FACTOR_PERSISTENT;
        }
        else {
            if (elo < LOW_ELO_RANGE) {
                kFactor = K_FACTOR_LOW_ELO;
            } else if (elo < HIGH_ELO_RANGE) {
                kFactor = K_FACTOR_MID_ELO;
            } else {
                kFactor = K_FACTOR_HIGH_ELO;
            }
        }

        return kFactor;
    }

    //Gets the expected outcome of a pokemon battle
    private double getExpectedOutcome(int oppElo) {
        return 1 / (1 + Math.pow(10, (oppElo - elo)/400.0));
    }

    public void setLastQueue(long lastQueue) {
        this.lastQueue = lastQueue;
    }
}
