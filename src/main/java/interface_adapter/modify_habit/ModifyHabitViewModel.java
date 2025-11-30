package interface_adapter.modify_habit;

import interface_adapter.ViewModel;

public class ModifyHabitViewModel extends ViewModel<ModifyHabitState> {

    public ModifyHabitViewModel() {
        super("modify habit");
        setState(new ModifyHabitState());
    }
}
