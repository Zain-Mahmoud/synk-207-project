package use_case.view_stats;

public interface ViewStatsOutputBoundary {

    /***
     * Prepares the success view upon interactor completion.
     * @param outputData output data to present
     */
    void prepareSuccessView(ViewStatsOutputData outputData);

    /***
     * Switches to task list view.
     */
    void switchToTaskList();
}
