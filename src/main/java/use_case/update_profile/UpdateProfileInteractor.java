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
        final String uid = inputData.getUid();
        final String newUsername = inputData.getNewUsername();
        final String newAvatarPath = inputData.getNewAvatarPath();
        final String newPassword = inputData.getNewPassword();

        if (!userDataAccess.existsByUid(uid)) {
            presenter.prepareFailView("User does not exist.");
            return;
        }

        final User user = userDataAccess.getByUid(uid);

        if (newUsername != null && !newUsername.isBlank()) {
            final String trimmed = newUsername.trim();
            final int usernameLimit = 12;
            if (trimmed.isEmpty() || trimmed.length() > usernameLimit) {
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
            final String trimmed = newPassword.trim();
            final int passwordLimit = 6;
            if (trimmed.length() < passwordLimit) {
                presenter.prepareFailView("Password must be longer than 6 characters.");
                return;
            }

            user.setPassword(trimmed);
        }

        userDataAccess.save(user);

        final UpdateProfileOutputData outputData = new UpdateProfileOutputData(
                user.getUid(),
                user.getUsername(),
                user.getAvatarPath(),
                user.getPassword(),
                false
        );

        presenter.prepareSuccessView(outputData);
    }
}
