package use_case.create_habit;

public interface CreateHabitOutputBoundary {
    void prepareSuccessView(CreateHabitOutputData outputData);
    void prepareFailView(String errorMessage);
}
