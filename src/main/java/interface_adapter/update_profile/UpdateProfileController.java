package interface_adapter.update_profile;

import use_case.update_profile.UpdateProfileBoundary;
import use_case.update_profile.UpdateProfileInputData;

/**
 * Controller for the Update Profile Use Case.
 */
public class UpdateProfileController {

    private final UpdateProfileBoundary updateProfileUseCase;

    public UpdateProfileController(UpdateProfileBoundary updateProfileUseCase) {
        this.updateProfileUseCase = updateProfileUseCase;
    }

    /**
     * Executes the Update Profile Use Case.
     * @param uid the uid of the user
     * @param newUsername the new username to set to
     * @param newAvatarPath the new avatar path to set to
     * @param newPassword the new password to set to
     */
    public void execute(String uid,
                        String newUsername,
                        String newPassword,
                        String newAvatarPath) {
        UpdateProfileInputData inputData =
                new UpdateProfileInputData(uid, newUsername, newPassword, newAvatarPath);
        updateProfileUseCase.execute(inputData);
    }
}
