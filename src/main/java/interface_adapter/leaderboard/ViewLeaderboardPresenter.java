package interface_adapter.leaderboard;

import java.util.List;
import java.util.Map;

import use_case.view_leaderboard.ViewLeaderboardOutputBoundary;
import use_case.view_leaderboard.ViewLeaderboardOutputData;

/**
 * The Presenter for the View Leaderboard Use Case.
 */
public class ViewLeaderboardPresenter implements ViewLeaderboardOutputBoundary {
    private final ViewLeaderboardViewModel viewLeaderboardViewModel;

    public ViewLeaderboardPresenter(ViewLeaderboardViewModel viewLeaderboardViewModel) {
        this.viewLeaderboardViewModel = viewLeaderboardViewModel;
    }

    @Override
    public void prepareSuccessView(ViewLeaderboardOutputData outputData) {
        ViewLeaderboardState state = viewLeaderboardViewModel.getState();
        List<Map<String, Object>> leaderboardEntries = outputData.getLeaderboardEntries();
        state.setLeaderboardEntries(leaderboardEntries);
        state.setErrorMessage(null);
        viewLeaderboardViewModel.setState(state);
        viewLeaderboardViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ViewLeaderboardState state = viewLeaderboardViewModel.getState();
        state.setErrorMessage(errorMessage);
        viewLeaderboardViewModel.setState(state);
        viewLeaderboardViewModel.firePropertyChanged();
    }
}

