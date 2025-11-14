package interface_adapter.modify_task;

import use_case.modify_task.ModifyTaskOutputBoundary;
import use_case.modify_task.ModifyTaskOutputData;

public class ModifiedTaskPresenter implements ModifyTaskOutputBoundary {
    @Override
    public void prepareSuccessView(ModifyTaskOutputData outputData) {

    }

    @Override
    public void prepareFailView(String errorMessage) {

    }
}
