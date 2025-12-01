package use_case.delete_task;

public interface DeleteTaskOutputBoundary {

    void prepareSuccessView(DeleteTaskOutputData outputData);

    void prepareFailView(String outputData);
}
