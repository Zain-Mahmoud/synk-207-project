package interface_adapter.modify_task;

import interface_adapter.ViewModel;

public class ModifiedTaskViewModel extends ViewModel<ModifiedTaskState> {

    public ModifiedTaskViewModel() {
        super("modify task");
        setState(new ModifiedTaskState());
    }
}
