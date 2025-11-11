package use_case.create_task;

/**
 * Input Boundary for actions which are related to create a task.
 */
public interface CreateTaskInputBoundary {
    /**
     * Executes the create_task use case.
     * @param createTaskInputData the input data
     */
    void execute(CreateTaskInputData createTaskInputData);
}
