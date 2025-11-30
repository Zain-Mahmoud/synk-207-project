package use_case.view_leaderboard;

import java.util.ArrayList;

/**
 * Input Boundary for the View Leaderboard Use Case.
 */
public interface ViewLeaderboardInputBoundary {
    /**
     * Executes the view leaderboard use case.
     * @param inputData the input data (may be empty if no input needed)
     */
    void execute(ViewLeaderboardInputData inputData);

}

