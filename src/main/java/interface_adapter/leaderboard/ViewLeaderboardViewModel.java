package interface_adapter.leaderboard;

import interface_adapter.ViewModel;

/**
 * The View Model for the Leaderboard View.
 */
public class ViewLeaderboardViewModel extends ViewModel<ViewLeaderboardState> {

    public static final String TITLE_LABEL = "Habit Streak Leaderboard";
    public static final String REFRESH_BUTTON_LABEL = "Refresh";

    public ViewLeaderboardViewModel() {
        super("leaderboard");
        setState(new ViewLeaderboardState());
    }
}

