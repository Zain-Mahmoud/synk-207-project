package interface_adapter.modify_habit;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.modify_habit.ModifyHabitInputBoundary;
import use_case.modify_habit.ModifyHabitInputData;


import java.time.LocalDateTime;

public class ModifyHabitController {
    private final ModifyHabitInputBoundary modifyHabitUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;
    // TODO obtain previous task information from Arya's task list view model
    public ModifyHabitController(ModifyHabitInputBoundary modifyHabitInteractor, LoggedInViewModel loggedInViewModel) {
        this.modifyHabitUseCaseInteractor = modifyHabitInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }


    /***
     * Executes modify habit use case
     * @param newHabitName
     * @param newPriority
     * @param newStatus
     * @param newStartDateTime
     * @param newStreakCount
     * @param newHabitGroup
     * @param newHabitFrequency
     */
    public void execute(String newHabitName, int newPriority, boolean newStatus, 
                        LocalDateTime newStartDateTime, int newStreakCount, 
                        String newHabitGroup, LocalDateTime newHabitFrequency){

        String username = loggedInViewModel.getState().getUsername();
        ModifyHabitInputData modifyHabitInputData = new ModifyHabitInputData(newHabitName, newPriority, newStatus, username, newStartDateTime, newStreakCount, newHabitGroup, newHabitFrequency);

        this.modifyHabitUseCaseInteractor.execute(modifyHabitInputData);

    }

    public void switchToHabitListView(){
        this.modifyHabitUseCaseInteractor.switchToHabitListView();
    }

}
