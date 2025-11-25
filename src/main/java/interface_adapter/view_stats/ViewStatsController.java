package interface_adapter.view_stats;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.view_stats.ViewStatsInputBoundary;
import use_case.view_stats.ViewStatsInputData;

public class ViewStatsController {
    private final ViewStatsInputBoundary viewStatsInteractor;
    private final LoggedInViewModel loggedInViewModel;
    public ViewStatsController(ViewStatsInputBoundary viewStatsInteractor, LoggedInViewModel loggedInViewModel) {
        this.viewStatsInteractor = viewStatsInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    public void execute(){
        String userID = loggedInViewModel.getState().getUsername();
        ViewStatsInputData inputData = new ViewStatsInputData(userID);

        this.viewStatsInteractor.execute(inputData);
    }

    public void switchToTaskView(){
        viewStatsInteractor.switchToTaskView();
    }
}
