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

    public void excute(String username,
                        String habitName,
                        String startDateTimeText,
                        int frequencyCount,
                        String frequencyUnit,
                        String habitGroup,
                        int streakCount,
                        int priority) {

        LocalDateTime startDateTime;

        try {
            startDateTime = LocalDateTime.parse(startDateTimeText);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid start date & time format. Use yyyy-MM-dd'T'HH:mm");
        }

        // No more frequencyText parsing — now frequency = (count + unit)
        // We pass them directly into CreateHabitInputData.

        CreateHabitInputData inputData = new CreateHabitInputData(
                username,
                habitName,
                startDateTime,
                frequencyCount,
                frequencyUnit,      // update：Per Day / Per Week / Per Month / Per Year
                habitGroup,
                streakCount,
                priority
        );

        interactor.excute(inputData);
    }
}
