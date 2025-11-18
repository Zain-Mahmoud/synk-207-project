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

        if ( inputdata.getTaskName() == null || inputdata.getTaskName().isBlank()) {
            presenter.prepareFailView("Task name cannot be empty.");
        }

        if (dao.existsByName(inputdata.getTaskName())) {
            presenter.prepareFailView("Task already exists.");
        }

        String taskName = inputdata.getTaskName();
        LocalDateTime deadline = inputdata.getDeadline();
        String taskGroup = inputdata.getTaskGroup();
        boolean status = inputdata.getstatus();
        int priority = inputdata.getPriority();

        Task newTask = new TaskBuilder()
                .setTaskName(taskName)
                .setDeadline(deadline)
                .setTaskGroup(taskGroup)
                .setStatus(status)
                .setPriority(priority)
                .build();

        dao.save(newTask);
        presenter.prepareSuccessView(
                new CreateTaskOutputData(newTask.getTaskName(), true, "Task created successfully!"));

    }
}