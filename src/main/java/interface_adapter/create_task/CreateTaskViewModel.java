package interface_adapter.create_task;

import interface_adapter.ViewModel;

public class CreateTaskViewModel extends ViewModel<CreateTaskState>{
    public static final String TITLE_LABEL = "Create New Task";
    public static final String USERNAME_LABEL = "Username";
    public static final String TASK_NAME_LABEL = "Task Name";
    public static final String DESCRIPTION_LABEL = "Description";
    public static final String DEADLINE_LABEL = "Deadline";
    public static final String TASK_GROUP_LABEL = "Task Group";
    public static final String PRIORITY_LABEL = "Priority";

    public static final String CREATE_BUTTON_LABEL = "Create Task";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    public CreateTaskViewModel() {
        super("create task");
        setState(new CreateTaskState());
    }


}
