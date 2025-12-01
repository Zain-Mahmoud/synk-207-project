package use_case.view_tasks_and_habits;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import entities.Habit;
import entities.Task;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.gateways.HabitGateway;
import use_case.gateways.TaskGateway;

public class ViewTasksAndHabitsInteractor implements ViewTasksAndHabitsInputBoundary {
    private final TaskGateway taskDataAccess;
    private final HabitGateway habitDataAccess;
    private final ViewTasksAndHabitsOutputBoundary presenter;

    public ViewTasksAndHabitsInteractor(TaskGateway taskDataAccess, HabitGateway habitDataAccess,
                                        ViewTasksAndHabitsOutputBoundary presenter) {
        this.presenter = presenter;
        this.taskDataAccess = taskDataAccess;
        this.habitDataAccess = habitDataAccess;
    }

    /**
     * Fetches tasks and habist using the data access objects and formats them into ArrayLists of Strings,
     * sending them to the presenter.
     * @param loggedInViewModel the Logged In View Modek
     */
    public void getFormattedTasksAndHabits(LoggedInViewModel loggedInViewModel) {
        try {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yy, HH:mm");

            final ArrayList<Task> taskList = this.taskDataAccess.fetchTasks(loggedInViewModel.getState().getUsername());

            final ArrayList<ArrayList<String>> formattedTasks = new ArrayList<>();

            for (Task task : taskList) {
                final ArrayList<String> formattedTask = new ArrayList<>();

                final String taskName = task.getName();
                final LocalDateTime taskDeadline = task.getDeadline();
                final String taskGroup = task.getTaskGroup();
                final boolean status = task.getStatus();
                final int priority = task.getPriority();
                final String description = task.getDescription();

                formattedTask.add(taskName);

                final String taskDeadlineToString = taskDeadline.format(formatter);
                formattedTask.add(taskDeadlineToString);

                formattedTask.add(taskGroup);

                if (status) {
                    formattedTask.add("Complete");
                }
                else {
                    formattedTask.add("Incomplete");
                }

                formattedTask.add(Integer.toString(priority));

                formattedTask.add(description);

                formattedTasks.add(formattedTask);
            }
            final ArrayList<Habit> habitList = this.habitDataAccess.fetchHabits(
                    loggedInViewModel.getState().getUsername());

            final ArrayList<ArrayList<String>> formattedHabits = new ArrayList<>();

            for (Habit habit : habitList) {
                final ArrayList<String> formattedHabit = new ArrayList<>();

                final String habitName = habit.getName();
                final LocalDateTime habitStartDateTime = habit.getStartDateTime();
                final Period habitFrequency = habit.getFrequency();
                final String habitGroup = habit.getHabitGroup();
                final int habitStreakCount = habit.getStreakCount();
                final int priority = habit.getPriority();
                final boolean status = habit.getStatus();
                final String description = habit.getDescription();

                formattedHabit.add(habitName);

                final String habitStartDateTimeToString = habitStartDateTime.format(formatter);

                formattedHabit.add(habitStartDateTimeToString);

                formattedHabit.add(Integer.toString(habitFrequency.getYears()) + " Years, "
                        + Integer.toString(habitFrequency.getMonths()) + " Months, "
                        + Integer.toString(habitFrequency.getDays()) + " Days");

                formattedHabit.add(habitGroup);

                formattedHabit.add(Integer.toString(habitStreakCount));

                formattedHabit.add(Integer.toString(priority));

                if (status) {
                    formattedHabit.add("Complete");
                }
                else {
                    formattedHabit.add("Incomplete");
                }

                formattedHabit.add(description);

                formattedHabits.add(formattedHabit);
            }
            final ViewTasksAndHabitsOutputData outputData = new ViewTasksAndHabitsOutputData(
                    formattedTasks, formattedHabits);
            presenter.prepareSuccessView(outputData);
        }
        catch (Exception exception) {
            presenter.prepareFailView("Failed to load task and habit data");
        }
    }

}
