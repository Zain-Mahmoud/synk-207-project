package interface_adapter.delete_habit;

import use_case.delete_habit.DeleteHabitInputBoundary;
import use_case.delete_habit.DeleteHabitInputData;

public class DeleteHabitController {
    private final DeleteHabitInputBoundary interactor;

    public DeleteHabitController(DeleteHabitInputBoundary interactor) {
        this.interactor = interactor;
    }
    public void excute(String username, String habitName) {
        final DeleteHabitInputData inputData = new DeleteHabitInputData(username, habitName);
        interactor.excute(inputData);
    }
}
