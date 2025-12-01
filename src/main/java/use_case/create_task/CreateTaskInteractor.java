package use_case.create_task;

import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskGateway;

import java.time.LocalDateTime;
import java.util.List;

public class CreateTaskInteractor implements CreateTaskInputBoundary {
    private final TaskGateway taskGateway;
    private final CreateTaskOutputBoundary presenter;

    public CreateTaskInteractor(TaskGateway taskGateway,
            CreateTaskOutputBoundary presenter) {
        this.presenter = presenter;
        this.taskGateway = taskGateway;
    }

    @Override
    public void execute(CreateTaskInputData inputdata) {

        if (inputdata.getUsername() == null || inputdata.getUsername().isBlank()) {
            presenter.prepareFailView("Username cannot be empty.");
            return;
        }

        if (inputdata.getTaskName() == null || inputdata.getTaskName().isBlank()) {
            presenter.prepareFailView("Task name cannot be empty.");
            return;
        }

        String username = inputdata.getUsername();
        String taskName = inputdata.getTaskName();

        // Check if task exists by fetching all tasks and searching by name
        List<Task> existingTasks = taskGateway.fetchTasks(username);
        boolean taskExists = existingTasks.stream()
                .anyMatch(task -> task.getName().equals(taskName));

        if (taskExists) {
            presenter.prepareFailView("Task already exists.");
            return;
        }

        String description = inputdata.getDescription();
        LocalDateTime startTime = inputdata.getStartTime();
        LocalDateTime deadline = inputdata.getDeadline();
        String taskGroup = inputdata.getTaskGroup();
        boolean status = inputdata.getstatus();
        int priority = inputdata.getPriority();

        Task newTask = new TaskBuilder()
                .setTaskName(taskName)
                .setDescription(description)
                .setStartTime(startTime)
                .setDeadline(deadline)
                .setTaskGroup(taskGroup)
                .setStatus(status)
                .setPriority(priority)
                .build();

        taskGateway.addTask(username, newTask);
        presenter.prepareSuccessView(
                new CreateTaskOutputData(username, newTask.getName(), true, "Task created successfully!"));

    }
}