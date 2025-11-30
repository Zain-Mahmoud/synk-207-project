package strategy;

import entities.Habit;
import use_case.gateways.HabitGateway;
import java.util.ArrayList;

/**
 * Concrete Strategy: Checks that a modified habit's name does not conflict
 * with any other existing habit for the same user.
 */
public class HabitValidationStrategy implements ValidationStrategy<Habit> {
    private final HabitGateway habitDataAccessObject;

    public HabitValidationStrategy(HabitGateway habitDataAccessObject) {
        this.habitDataAccessObject = habitDataAccessObject;
    }

    @Override
    public String validate(String userID, Habit oldHabit, Habit modifiedHabit) {
        if (oldHabit == null || modifiedHabit == null) {
            return "Cannot validate: Habit entities are null.";
        }

        ArrayList<Habit> habitList = habitDataAccessObject.fetchHabits(userID);

        for (Habit habit : habitList) {
            if (!habit.getName().equals(oldHabit.getName()) && habit.getName().equals(modifiedHabit.getName())) {
                return "Habit already exists";
            }
        }

        return null;
    }
}