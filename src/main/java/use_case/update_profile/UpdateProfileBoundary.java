package use_case.update_profile;

/**
 * Input Boundary for actions which are related to updating user profile.
 */
public interface UpdateProfileBoundary {

    /**
     * Executes the UpdateProfile use case.
     * @param updateProfileInputData the input data
     */
    void execute(UpdateProfileInputData updateProfileInputData);
}
