package interface_adapter.update_profile;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.update_profile.UpdateProfileOutputBoundary;
import use_case.update_profile.UpdateProfileOutputData;

/**
 * The Presenter for the Update Profile Use Case.
 */
public class UpdateProfilePresenter implements UpdateProfileOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final UpdateProfileViewModel updateProfileViewModel;
    private final LoggedInViewModel loggedInViewModel;


    public UpdateProfilePresenter(ViewManagerModel viewManagerModel,
                                  UpdateProfileViewModel updateProfileViewModel,
                                  LoggedInViewModel loggedInViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.updateProfileViewModel = updateProfileViewModel;
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(UpdateProfileOutputData response) {
        // On success, switch back to main menu view.
        final UpdateProfileState state = updateProfileViewModel.getState();
        state.setUid(response.getUid());
        state.setUsername(response.getUsername());
        state.setAvatarPath(response.getAvatarPath());
        state.setPassword(response.getPassword());
        state.setUsernameError(null);
        state.setAvatarError(null);
        state.setSuccessMessage("Profile updated successfully.");

        updateProfileViewModel.setState(state);
        updateProfileViewModel.firePropertyChanged();

        LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setUid(response.getUid());
        loggedInState.setUsername(response.getUsername());
        loggedInState.setAvatarPath(response.getAvatarPath());
        loggedInState.setPassword(response.getPassword());
        loggedInViewModel.setState(loggedInState);
        loggedInViewModel.firePropertyChanged();

        viewManagerModel.setState(loggedInViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
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
