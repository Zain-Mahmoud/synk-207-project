package use_case.modify_habit;

public interface ModifyHabitOutputBoundary {
    /**
     * Prepares the success view for the Login Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(ModifyHabitOutputData outputData);

    /**
     * Prepares the failure view for the modify task use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

    void switchToHabitListView();
}
