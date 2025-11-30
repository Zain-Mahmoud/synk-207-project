package use_case.update_profile;

import entities.User;
import use_case.gateways.UserGateway;

/**
 * The Interactor for the Update Profile Use Case.
 *
 */
public class UpdateProfileInteractor implements UpdateProfileBoundary {

    private final UserGateway userDataAccess;
    private final UpdateProfileOutputBoundary presenter;

    public UpdateProfileInteractor(UserGateway userDataAccess,
                                   UpdateProfileOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(UpdateProfileInputData inputData) {
        String uid = inputData.getUid();
        String newUsername = inputData.getNewUsername();
        String newAvatarPath = inputData.getNewAvatarPath();
        String newPassword = inputData.getNewPassword();

        if (!userDataAccess.existsByUid(uid)) {
            presenter.prepareFailView("User does not exist.");
            return;
        }

        User user = userDataAccess.getByUid(uid);

        if (newUsername != null && !newUsername.isBlank()) {
            String trimmed = newUsername.trim();

            if (trimmed.isEmpty() || trimmed.length() > 12) {
                presenter.prepareFailView("Username must be 1–12 characters.");
                return;
            }

            if (!trimmed.equals(user.getUsername())
                    && userDataAccess.isUsernameTaken(trimmed)) {
                presenter.prepareFailView("Username already taken.");
                return;
            }

            user.setUsername(trimmed);
        }

        if (newAvatarPath != null && !newAvatarPath.isBlank()) {
            user.setAvatarPath(newAvatarPath);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            String trimmed = newPassword.trim();
            if (trimmed.length() < 6) {
                presenter.prepareFailView("Password must be longer than 6 characters.");
                return;
            }

            user.setPassword(trimmed);
        }

        userDataAccess.save(user);

        UpdateProfileOutputData outputData = new UpdateProfileOutputData(
                user.getUid(),
                user.getUsername(),
                user.getAvatarPath(),
                user.getPassword(),
                false
        );

        presenter.prepareSuccessView(outputData);
    }
}
