package use_case.modify_habit;

import entities.Habit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ModifyHabitOutputData {

    private final ArrayList<Habit> habitList;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ModifyHabitOutputData(ArrayList<Habit> habitList) {
        this.habitList = habitList;
    }

    public ArrayList<ArrayList<String>> getHabitList() {

        ArrayList<ArrayList<String>> formattedHabits = new ArrayList<>();

        for (Habit habit : habitList) {
            ArrayList<String> formattedHabit = new ArrayList<>();

            String habitName = habit.getName();
            LocalDateTime habitStartDateTime = habit.getStartTime();
            int habitFrequency = habit.getFrequency();
            String habitGroup = habit.getHabitGroup();
            int habitStreakCount = habit.getStreakCount();
            int priority = habit.getPriority();
            boolean status = habit.getStatus();
            String description = habit.getDescription();

            formattedHabit.add(habitName);

            String habitStartDateTimeToString = habitStartDateTime.format(ISO_FORMATTER);

            switch (habitStartDateTimeToString.substring(5, 7)) {
                case "01":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " January, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "02":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " February, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "03":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " March, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "04":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " April, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "05":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " May, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "06":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " June, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "07":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " July, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "08":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " August, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "09":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " September, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "10":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " October, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "11":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " November, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
                case "12":
                    formattedHabit.add(habitStartDateTimeToString.substring(8, 10) + " December, " +
                            habitStartDateTimeToString.substring(0, 4) + " " + habitStartDateTimeToString.substring(11, 16));
                    break;
            }

            formattedHabit.add(Integer.toString(habitFrequency));

            formattedHabit.add(habitGroup);

            formattedHabit.add(Integer.toString(habitStreakCount));

            formattedHabit.add(Integer.toString(priority));

            if (status == true) {
                formattedHabit.add("Complete");
            } else {
                formattedHabit.add("Incomplete");
            }

            formattedHabit.add(description);

            formattedHabits.add(formattedHabit);
        }
        return formattedHabits;
    }
}
