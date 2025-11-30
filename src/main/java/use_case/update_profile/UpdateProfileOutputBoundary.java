package use_case.update_profile;

/**
 * The output boundary for the UpdateProfile Use Case.
 */
public interface UpdateProfileOutputBoundary {

    /**
     * Prepares the success view for the UpdateProfile Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(UpdateProfileOutputData outputData);

    /**
     * Prepares the failure view for the UpdateProfile Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
