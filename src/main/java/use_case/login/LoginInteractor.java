package use_case.login;

import entities.User;
import use_case.gateways.UserGateway;

/**
 * The Login Interactor.
 */
public class LoginInteractor implements LoginInputBoundary {
    private final UserGateway userDataAccessObject;
    private final LoginOutputBoundary loginPresenter;

    public LoginInteractor(UserGateway userDataAccessInterface,
                           LoginOutputBoundary loginOutputBoundary) {
        this.userDataAccessObject = userDataAccessInterface;
        this.loginPresenter = loginOutputBoundary;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();
        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView(username + ": Account does not exist.");
        }
        else {
            final String pwd = userDataAccessObject.getByName(username).getPassword();
            if (!password.equals(pwd)) {
                loginPresenter.prepareFailView("Incorrect password for \"" + username + "\".");
            }
            else {

                final User user = userDataAccessObject.getByName(loginInputData.getUsername());

                final LoginOutputData loginOutputData =
                        new LoginOutputData(user.getUid(), user.getUsername(), user.getAvatarPath(), false);
                loginPresenter.prepareSuccessView(loginOutputData);
            }
        }
    }
}
