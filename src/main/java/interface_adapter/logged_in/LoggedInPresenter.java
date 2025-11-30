package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;

/**
 * The Presenter for the Change Password Use Case.
 */
public class LoggedInPresenter {

    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoggedInPresenter(ViewManagerModel viewManagerModel,
                             LoggedInViewModel loggedInViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
    }
}
