package interface_adapter.update_profile;

import interface_adapter.ViewManagerModel;
import use_case.update_profile.UpdateProfileOutputBoundary;
import use_case.update_profile.UpdateProfileOutputData;

/**
 * The Presenter for the Update Profile Use Case.
 */
public class UpdateProfilePresenter implements UpdateProfileOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final UpdateProfileViewModel updateProfileViewModel;


    public UpdateProfilePresenter(ViewManagerModel viewManagerModel,
                                  UpdateProfileViewModel updateProfileViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.updateProfileViewModel = updateProfileViewModel;
    }

    @Override
    public void prepareSuccessView(UpdateProfileOutputData response) {
        // On success, switch back to main menu view.
        final UpdateProfileState updateState = updateProfileViewModel.getState();
        updateState.setUid(response.getUid());
        updateState.setUsername(response.getUsername());
        updateState.setAvatarPath(response.getAvatarPath());
        updateState.setUsernameError(null);
        updateState.setAvatarError(null);
        updateState.setSuccessMessage("Profile updated successfully.");

        updateProfileViewModel.setState(updateState);
        updateProfileViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final UpdateProfileState updateState = updateProfileViewModel.getState();
        updateState.setUsernameError(error);
        updateState.setSuccessMessage(null);

        updateProfileViewModel.setState(updateState);
        updateProfileViewModel.firePropertyChanged();
    }
}
