package de.streblow.paperracing;

/**
 * Created by streblow on 08.12.17.
 */

public class RaceStatsEntry {

    public String playerName;
    public int races;
    public int won;

    public RaceStatsEntry(String playerName, int races, int won) {
        this.playerName = playerName;
        this.races = races;
        this.won = won;
    }

}
