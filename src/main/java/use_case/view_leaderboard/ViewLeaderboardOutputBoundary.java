package use_case.view_leaderboard;

/**
 * Output Boundary for the View Leaderboard Use Case.
 */
public interface ViewLeaderboardOutputBoundary {
    /**
     * Prepares the success view for the View Leaderboard Use Case.
     * @param outputData the output data containing leaderboard entries
     */
    void prepareSuccessView(ViewLeaderboardOutputData outputData);

    /**
     * Prepares the failure view for the View Leaderboard Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}

