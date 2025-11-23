package interface_adapter.leaderboard;

import use_case.view_leaderboard.ViewLeaderboardInputBoundary;
import use_case.view_leaderboard.ViewLeaderboardInputData;

/**
 * The Controller for the View Leaderboard Use Case.
 */
public class ViewLeaderboardController {
    private final ViewLeaderboardInputBoundary viewLeaderboardInteractor;

    public ViewLeaderboardController(ViewLeaderboardInputBoundary viewLeaderboardInteractor) {
        this.viewLeaderboardInteractor = viewLeaderboardInteractor;
    }

    /**
     * Executes the View Leaderboard Use Case.
     */
    public void execute() {
        ViewLeaderboardInputData inputData = new ViewLeaderboardInputData();
        viewLeaderboardInteractor.execute(inputData);
    }
}

