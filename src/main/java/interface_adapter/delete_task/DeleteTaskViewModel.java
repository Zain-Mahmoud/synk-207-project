package interface_adapter.delete_task;

import interface_adapter.ViewModel;

public class DeleteTaskViewModel extends ViewModel<DeleteTaskState> {
    public static final String TITLE_LABEL = "Delete Task";
    public static final String DELETE_BUTTON_LABEL = "Delete Task";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";
    public static final String CONFIRM_MESSAGE = "Are you sure you want to delete task '%s'?";

    public DeleteTaskViewModel(String viewName) {
        super(viewName);
        setState(new DeleteTaskState());
    }
}
