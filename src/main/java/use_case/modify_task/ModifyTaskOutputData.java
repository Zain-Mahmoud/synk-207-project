package use_case.modify_task;

import entities.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ModifyTaskOutputData{

    private ArrayList<Task> taskList;

    public ModifyTaskOutputData(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }
}
