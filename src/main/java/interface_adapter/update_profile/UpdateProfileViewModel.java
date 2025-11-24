package interface_adapter.update_profile;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Update Profile View.
 */
public class UpdateProfileViewModel extends ViewModel<UpdateProfileState> {

    public static final String TITLE_LABEL = "Update Profile";
    public static final String USERNAME_LABEL = "New username";
    public static final String PASSWORD_LABEL = "Change Password";
    public static final String AVATAR_LABEL = "Choose Avatar";

    public static final String SAVE_BUTTON_LABEL = "Save";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    public UpdateProfileViewModel() {
        super("updateprofile");
        setState(new UpdateProfileState());
    }
}
