package interface_adapter.modify_task;

import interface_adapter.ViewModel;

public class ModifyTaskViewModel extends ViewModel<ModifyTaskState> {

    public ModifyTaskViewModel() {
        super("modify task");
        setState(new ModifyTaskState());
    }
}
