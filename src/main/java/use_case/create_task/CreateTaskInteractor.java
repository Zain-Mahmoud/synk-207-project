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
        LocalDateTime startTime = InputData.getStartTime();
        LocalDateTime deadline = InputData.getDeadline();
        String taskGroup = InputData.getTaskGroup();
        boolean status = InputData.getstatus();
        int priority = InputData.getPriority();
        String description = InputData.getDescription();

        Task newTask = new TaskBuilder()
                .setTaskName(taskName)
                .setStartTime(startTime)
                .setDeadline(deadline)
                .setTaskGroup(taskGroup)
                .setStatus(status)
                .setPriority(priority)
                .setDescription(description)
                .build();
    }
}
