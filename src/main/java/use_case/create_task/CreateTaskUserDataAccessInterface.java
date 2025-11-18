package use_case.create_task;

import entities.Task;

public interface CreateTaskUserDataAccessInterface {

    boolean existsByName( String taskName);

    void save(Task task);
}
