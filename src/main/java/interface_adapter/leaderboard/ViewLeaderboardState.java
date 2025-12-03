package interface_adapter.leaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The State information for the Leaderboard View.
 */
public class ViewLeaderboardState {
    private List<Map<String, Object>> leaderboardEntries = new ArrayList<>();
    private String errorMessage = null;

    public ViewLeaderboardState() {
    }

    public ViewLeaderboardState(ViewLeaderboardState copy) {
        // Create a new ArrayList to avoid shared reference
        this.leaderboardEntries = new ArrayList<>(copy.leaderboardEntries);
        this.errorMessage = copy.errorMessage; // String is immutable, so direct assignment is safe
    }

    public List<Map<String, Object>> getLeaderboardEntries() {
        return leaderboardEntries;
    }

    public void setLeaderboardEntries(List<Map<String, Object>> leaderboardEntries) {
        this.leaderboardEntries = leaderboardEntries;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

