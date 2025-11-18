package use_case.view_leaderboard;

import java.util.List;
import java.util.Map;

/**
 * Output Data for the View Leaderboard Use Case.
 * Contains the leaderboard entries sorted by maximum streak count.
 */
public class ViewLeaderboardOutputData {
    /**
     * List of leaderboard entries.
     * Each entry is a Map with keys: "username" (String) and "maxStreak" (Integer).
     * Entries are sorted in descending order by maxStreak.
     */
    private final List<Map<String, Object>> leaderboardEntries;

    public ViewLeaderboardOutputData(List<Map<String, Object>> leaderboardEntries) {
        this.leaderboardEntries = leaderboardEntries;
    }

    public List<Map<String, Object>> getLeaderboardEntries() {
        return leaderboardEntries;
    }
}

