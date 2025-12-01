package interface_adapter.modify_task;

import interface_adapter.ViewManagerModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import use_case.modify_task.ModifyTaskOutputBoundary;
import use_case.modify_task.ModifyTaskOutputData;

public class ModifyTaskPresenter implements ModifyTaskOutputBoundary {
    private ViewManagerModel viewManagerModel;
    private ModifyTaskViewModel modifyTaskViewModel;
    private ViewTasksAndHabitsViewModel tasksViewModel;

    public ModifyTaskPresenter(ViewManagerModel viewManagerModel,
                               ModifyTaskViewModel modifyTaskViewModel,
                               ViewTasksAndHabitsViewModel tasksViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.modifyTaskViewModel = modifyTaskViewModel;
        this.tasksViewModel = tasksViewModel;
    }

    @Override
    public void prepareSuccessView(ModifyTaskOutputData outputData) {
        final ViewTasksAndHabitsState currState = tasksViewModel.getState();
        currState.setFormattedTasks(outputData.getTaskList());
        tasksViewModel.firePropertyChanged();

        switchToTaskListView();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ModifyTaskState modifyTaskState = modifyTaskViewModel.getState();
        modifyTaskState.setTaskError(errorMessage);
        modifyTaskViewModel.firePropertyChanged();
    }

    /**
     * Switches to task list view.
     */
    public void switchToTaskListView() {
        viewManagerModel.setState(tasksViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}
