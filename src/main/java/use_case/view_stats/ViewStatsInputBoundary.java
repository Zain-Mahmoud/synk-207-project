package use_case.view_stats;

public interface ViewStatsInputBoundary {

    /**
     * Executes the use case interactor.
     * @param inputData input data to pass to interactor
     */
    void execute(ViewStatsInputData inputData);

    /**
     * Switches to task list view.
     */
    void switchToTaskView();
}
