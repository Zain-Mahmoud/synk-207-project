package use_case.modify_task;

public interface ModifyTaskInputBoundary {

    /**
     * Executes the modify task use case
     * @param modifyTaskInputData the input data
     */
    void execute(ModifyTaskInputData modifyTaskInputData);
}
