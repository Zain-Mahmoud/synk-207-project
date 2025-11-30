package interface_adapter.modify_habit;

import interface_adapter.ViewManagerModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import use_case.modify_habit.ModifyHabitOutputBoundary;
import use_case.modify_habit.ModifyHabitOutputData;

public class ModifyHabitPresenter implements ModifyHabitOutputBoundary {
    private ViewManagerModel viewManagerModel;
    private ModifyHabitViewModel modifyHabitViewModel;
    private ViewTasksAndHabitsViewModel habitsViewModel;

    public ModifyHabitPresenter(ViewManagerModel viewManagerModel,
                                ModifyHabitViewModel modifyHabitViewModel,
                                ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.modifyHabitViewModel = modifyHabitViewModel;
        this.habitsViewModel = viewTasksAndHabitsViewModel;
    }

    @Override
    public void prepareSuccessView(ModifyHabitOutputData outputData) {
        final ViewTasksAndHabitsState currState = habitsViewModel.getState();
        currState.setFormattedHabits(outputData.getHabitList());
        habitsViewModel.firePropertyChanged();

        switchToHabitListView();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ModifyHabitState modifyTaskState = modifyHabitViewModel.getState();
        modifyTaskState.setHabitError(errorMessage);
        modifyHabitViewModel.firePropertyChanged();
    }

    /**
     * Switches to habit list view.
     */
    public void switchToHabitListView() {
        viewManagerModel.setState(habitsViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}
