package interface_adapter.modify_habit;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.modify_habit.ModifyHabitInputBoundary;
import use_case.modify_habit.ModifyHabitInputData;

public class ModifyHabitController {
    private final ModifyHabitInputBoundary modifyHabitUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public ModifyHabitController(ModifyHabitInputBoundary modifyHabitInteractor, LoggedInViewModel loggedInViewModel) {
        this.modifyHabitUseCaseInteractor = modifyHabitInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    /***
     * Executes modify habit use case.
     * @param oldHabitName old habit name
     * @param oldPriority old habit priority
     * @param oldStatus old habit status
     * @param oldStartDateTime old habit start date time
     * @param oldStreakCount old habit streak count
     * @param oldHabitGroup old habit group
     * @param oldHabitFrequency old habit frequency
     * @param newHabitName old habit name
     * @param newPriority new habit priority
     * @param newStatus new habit status
     * @param newStartDateTime new habit start time
     * @param newStreakCount new habit streak count
     * @param newHabitGroup new habit group
     * @param newHabitFrequency new habit frequency
     */
    public void execute(String oldHabitName, String oldPriority, boolean oldStatus, 
                        String oldStartDateTime, String oldStreakCount, 
                        String oldHabitGroup, String oldHabitFrequency,
                        String newHabitName, String newPriority, boolean newStatus, 
                        String newStartDateTime, String newStreakCount, 
                        String newHabitGroup, String newHabitFrequency) {

        final String username = loggedInViewModel.getState().getUsername();
        final ModifyHabitInputData modifyHabitInputData = new ModifyHabitInputData(
                oldHabitName, oldPriority, oldStatus, oldStartDateTime, oldStreakCount, oldHabitGroup,
                oldHabitFrequency, newHabitName, newPriority, newStatus, newStartDateTime,
                newStreakCount, newHabitGroup, newHabitFrequency, username
        );

        this.modifyHabitUseCaseInteractor.execute(modifyHabitInputData);

    }

    /**
     * Switches to habit list view.
     */
    public void switchToHabitListView() {
        this.modifyHabitUseCaseInteractor.switchToHabitListView();
    }

}
