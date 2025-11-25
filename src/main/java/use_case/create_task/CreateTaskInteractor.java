package use_case.create_task;

import entities.Task;
import entities.TaskBuilder;

import java.time.LocalDateTime;

public class CreateTaskInteractor implements CreateTaskInputBoundary {
    private final CreateTaskUserDataAccessInterface dao;
    private final CreateTaskOutputBoundary presenter;

    public CreateTaskInteractor(CreateTaskUserDataAccessInterface dao,
                                CreateTaskOutputBoundary presenter) {
        this.presenter = presenter;
        this.dao = dao;
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

        if (dao.existsByName(inputdata.getTaskName())) {
            presenter.prepareFailView("Task already exists.");
            return;
        }

        String username = inputdata.getUsername();
        String taskName = inputdata.getTaskName();
        String description = inputdata.getDescription();
        LocalDateTime deadline = inputdata.getDeadline();
        String taskGroup = inputdata.getTaskGroup();
        boolean status = inputdata.getstatus();
        int priority = inputdata.getPriority();

        Task newTask = new TaskBuilder()
                .setTaskName(taskName)
                .setDescription(description)
                .setDeadline(deadline)
                .setTaskGroup(taskGroup)
                .setStatus(status)
                .setPriority(priority)
                .build();

        dao.save(newTask);
        presenter.prepareSuccessView(
                new CreateTaskOutputData(username, newTask.getName(), true, "Task created successfully!"));

    }
}