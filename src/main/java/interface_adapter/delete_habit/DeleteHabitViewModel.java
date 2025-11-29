package interface_adapter.delete_habit;

import interface_adapter.ViewModel;

public class DeleteHabitViewModel extends ViewModel<DeleteHabitState> {

    public static final String TITLE_LABEL = "Delete Habit";
    public static final String USERNAME_LABEL = "Username";
    public static final String HABIT_NAME_LABEL = "Habit name";
    public static final String DELETE_BUTTON_LABEL = "Delete";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    public DeleteHabitViewModel(String viewName) {
        super(viewName);
        setState(new DeleteHabitState());
    }
}
