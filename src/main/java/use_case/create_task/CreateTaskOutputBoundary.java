package use_case.create_task;

public interface CreateTaskOutputBoundary {
    /**
     * Prepares the success view for the Create Task Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(CreateTaskOutputData outputData);

    /**
     * Prepares the failure view for the Create Task Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
