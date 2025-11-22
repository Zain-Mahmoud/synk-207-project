package use_case.modify_task;

public interface ModifyTaskOutputBoundary {
    /**
     * Prepares the success view for the Login Use Case.
     */
    void prepareSuccessView();

    /**
     * Prepares the failure view for the modify task use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

    void switchToTaskListView();
}
