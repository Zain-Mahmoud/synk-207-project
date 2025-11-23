package interface_adapter.create_habit;

import use_case.create_habit.CreateHabitInputBoundary;
import use_case.create_habit.CreateHabitInputData;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class CreateHabitController {

    private final CreateHabitInputBoundary interactor;

    public CreateHabitController(CreateHabitInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String username,
                        String habitName,
                        String startDateTimeText,
                        String frequencyDateTimeText,
                        String habitGroup,
                        int streakCount,
                        int priority) {

        LocalDateTime startDateTime;
        LocalDateTime frequency;

        try {
            startDateTime = LocalDateTime.parse(startDateTimeText);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid start date & time format. Use yyyy-MM-dd'T'HH:mm"
            );
        }

        try {
            frequency = LocalDateTime.parse(frequencyDateTimeText);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid frequency date & time format. Use yyyy-MM-dd'T'HH:mm"
            );
        }

        CreateHabitInputData inputData = new CreateHabitInputData(
                username,
                habitName,
                startDateTime,
                frequency,
                habitGroup,
                streakCount,
                priority
        );

        interactor.execute(inputData);
    }
}
