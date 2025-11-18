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
        final UpdateProfileState state = updateProfileViewModel.getState();
        state.setUid(response.getUid());
        state.setUsername(response.getUsername());
        state.setAvatarPath(response.getAvatarPath());
        state.setUsernameError(null);
        state.setAvatarError(null);
        state.setSuccessMessage("Profile updated successfully.");

        updateProfileViewModel.setState(state);
        updateProfileViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final UpdateProfileState state = updateProfileViewModel.getState();
        state.setUsernameError(error);
        state.setSuccessMessage(null);

        updateProfileViewModel.setState(state);
        updateProfileViewModel.firePropertyChanged();
    }
}
