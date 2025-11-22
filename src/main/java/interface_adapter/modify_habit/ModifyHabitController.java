package interface_adapter.modify_habit;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.modify_habit.ModifyHabitInputBoundary;
import use_case.modify_habit.ModifyHabitInputData;


import java.time.LocalDateTime;

public class ModifyHabitController {
    private final ModifyHabitInputBoundary modifyHabitUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public ModifyHabitController(ModifyHabitInputBoundary modifyHabitInteractor, LoggedInViewModel loggedInViewModel) {
        this.modifyHabitUseCaseInteractor = modifyHabitInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }


    /***
     * Executes modify habit use case
     * @param oldHabitName
     * @param oldPriority
     * @param oldStatus
     * @param oldStartDateTime
     * @param oldStreakCount
     * @param oldHabitGroup
     * @param oldHabitFrequency
     * @param newHabitName
     * @param newPriority
     * @param newStatus
     * @param newStartDateTime
     * @param newStreakCount
     * @param newHabitGroup
     * @param newHabitFrequency
     */
    public void execute(String oldHabitName, String oldPriority, boolean oldStatus, 
                        String oldStartDateTime, String oldStreakCount, 
                        String oldHabitGroup, String oldHabitFrequency,
                        String newHabitName, String newPriority, boolean newStatus, 
                        String newStartDateTime, String newStreakCount, 
                        String newHabitGroup, String newHabitFrequency){

        String username = loggedInViewModel.getState().getUsername();
        ModifyHabitInputData modifyHabitInputData = new ModifyHabitInputData(
                oldHabitName, oldPriority, oldStatus, oldStartDateTime, oldStreakCount, oldHabitGroup, oldHabitFrequency,
                newHabitName, newPriority, newStatus, newStartDateTime, newStreakCount, newHabitGroup, newHabitFrequency,
                username
        );

        this.modifyHabitUseCaseInteractor.execute(modifyHabitInputData);

    }

    public void switchToHabitListView(){
        this.modifyHabitUseCaseInteractor.switchToHabitListView();
    }

}
