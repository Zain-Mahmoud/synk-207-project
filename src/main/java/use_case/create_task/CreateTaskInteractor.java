package use_case.create_task;

import entities.Task;
import entities.TaskBuilder;

import java.time.LocalDateTime;

public class CreateTaskInteractor implements CreateTaskInputBoundary {
    private CreateTaskUserDataAccessInterface userDataAccess;
    private CreateTaskInputBoundary presenter;

    public CreateTaskInteractor( CreateTaskUserDataAccessInterface userDataAccess, CreateTaskInputBoundary presenter){
        this.presenter = presenter;
        this.userDataAccess = userDataAccess;
    }


    @Override
    public void execute(CreateTaskInputData InputData) {

        String taskName = InputData.getTaskName();
        LocalDateTime deadline = InputData.getDeadline();
        String taskGroup = InputData.getTaskGroup();
        boolean status = InputData.getstatus();
        int priority = InputData.getPriority();

        Task newTask = new TaskBuilder()
                .setTaskName(taskName)
                .setDeadline(deadline)
                .setTaskGroup(taskGroup)
                .setStatus(status)
                .setPriority(priority)
                .build();
    }
}
