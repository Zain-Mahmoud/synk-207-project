package interface_adapter.create_habit;

import interface_adapter.ViewModel;

public class CreateHabitViewModel extends ViewModel<CreateHabitState> {

    public static final String TITLE_LABEL = "Create New Habit";

    public static final String HABIT_NAME_LABEL = "Habit Name";
    public static final String START_DATETIME_LABEL = "Start Date & Time";
    public static final String FREQUENCY_LABEL = "Frequency Date & Time";
    public static final String HABIT_GROUP_LABEL = "Habit Group";
    public static final String STREAK_COUNT_LABEL = "Initial Streak Count";
    public static final String PRIORITY_LABEL = "Priority (0-10)";
    public static final String CREATE_BUTTON_LABEL = "Create Habit";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    public CreateHabitViewModel(String viewName) {
        super(viewName);
        setState(new CreateHabitState());
    }
}
