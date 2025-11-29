package use_case.delete_habit;

public interface DeleteHabitOutputBoundary {
    void prepareSuccessView(DeleteHabitOutputData outputData);
    void prepareFailView(String errorMessage);
}
