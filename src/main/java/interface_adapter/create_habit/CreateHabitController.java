package interface_adapter.create_habit;

import use_case.create_habit.CreateHabitInputBoundary;
import use_case.create_habit.CreateHabitInputData;

import java.time.LocalDateTime;

public class CreateHabitController {

    private final CreateHabitInputBoundary interactor;

    public CreateHabitController(CreateHabitInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String username,
            String habitName,
            LocalDateTime startDateTime,
            int frequency,
            String habitGroup,
            int streakCount,
            int priority) {

        CreateHabitInputData inputData = new CreateHabitInputData(
                username,
                habitName,
                startDateTime,
                frequency,
                habitGroup,
                streakCount,
                priority);

        interactor.execute(inputData);
    }
}
