package use_case.modify_task;

public interface ModifyTaskOutputBoundary {
    /**
     * Prepares the success view for the Login Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(ModifyTaskOutputData outputData);

    /**
     * Prepares the failure view for the modify task use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

    /**
     * Switches to the Task List View.
     */
    void switchToTaskListView();
}
