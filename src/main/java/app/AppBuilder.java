package app;

import java.awt.CardLayout;
import java.io.IOException;
import java.security.GeneralSecurityException; // TODO: Surface calendar gateway initialization errors

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.FileUserDataAccessObject;
import data_access.GoogleCalendarDataAccessObject; // TODO: Use Google Calendar implementation for syncing
import data_access.TaskHabitDataAccessObject;
import entities.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.leaderboard.ViewLeaderboardController;
import interface_adapter.leaderboard.ViewLeaderboardPresenter;
import interface_adapter.leaderboard.ViewLeaderboardViewModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarController; // TODO: Controller for sync use case
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarPresenter; // TODO: Presenter for sync use case
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarViewModel; // TODO: View model for sync status
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.gateways.CalendarGateway; // TODO: Gateway abstraction for calendar operations
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInputBoundary; // TODO: Input boundary for sync use case
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInteractor; // TODO: Interactor performing sync logic
import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputBoundary; // TODO: Output boundary for sync use case
import use_case.view_leaderboard.ViewLeaderboardInputBoundary;
import use_case.view_leaderboard.ViewLeaderboardInteractor;
import use_case.view_leaderboard.ViewLeaderboardOutputBoundary;
import use_case.view_leaderboard.ViewLeaderboardUserDataAccessInterface;
import view.LeaderboardView;
import view.LoggedInView;
import view.LoginView;
import view.SignupView;
import view.ViewManager;

import java.nio.file.Path;
import java.nio.file.Paths;


public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // set which data access implementation to use, can be any
    // of the classes from the data_access package
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);
    final TaskHabitDataAccessObject taskHabitDataAccessObject;
    final ViewLeaderboardUserDataAccessInterface viewLeaderboardUserDataAccessInterface;
    private final CalendarGateway calendarGateway; // TODO: Calendar gateway used for syncing to Google Calendar

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private LeaderboardView leaderboardView;
    private ViewLeaderboardViewModel viewLeaderboardViewModel;
    private SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel; // TODO: View model carrying sync status updates
    private SyncToGoogleCalendarController syncToGoogleCalendarController; // TODO: Controller to kick off sync flow

    public AppBuilder() throws IOException, GeneralSecurityException { // TODO: Constructor now accounts for calendar gateway setup
        cardPanel.setLayout(cardLayout);
        Path habitsPath = Paths.get("habits.csv");
        taskHabitDataAccessObject = new TaskHabitDataAccessObject(habitsPath);
        calendarGateway = new GoogleCalendarDataAccessObject(); // TODO: Initialize Google Calendar gateway implementation
        viewLeaderboardUserDataAccessInterface = null;
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        loggedInView.setViewManagerModel(viewManagerModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addLeaderboardView() {
        viewLeaderboardViewModel = new ViewLeaderboardViewModel();
        leaderboardView = new LeaderboardView(viewLeaderboardViewModel);
        cardPanel.add(leaderboardView, leaderboardView.getViewName());
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new LoggedInPresenter(viewManagerModel,
                loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    public AppBuilder addViewLeaderboardUseCase() {
        final ViewLeaderboardOutputBoundary viewLeaderboardOutputBoundary = new ViewLeaderboardPresenter(viewLeaderboardViewModel);
        final ViewLeaderboardInputBoundary viewLeaderboardInteractor = new ViewLeaderboardInteractor(
                viewLeaderboardUserDataAccessInterface, viewLeaderboardOutputBoundary);

        ViewLeaderboardController viewLeaderboardController = new ViewLeaderboardController(viewLeaderboardInteractor);
        leaderboardView.setViewLeaderboardController(viewLeaderboardController);
        return this;
    }

    public AppBuilder addSyncToGoogleCalendarUseCase() { // TODO: Wire sync-to-calendar use case components together
        if (syncToGoogleCalendarViewModel == null) { // TODO: Avoid recreating sync view model on repeated builder calls
            syncToGoogleCalendarViewModel = new SyncToGoogleCalendarViewModel(); // TODO: Build sync view model once for lifecycle
        }
        SyncToGoogleCalendarOutputBoundary syncOutputBoundary =
                new SyncToGoogleCalendarPresenter(syncToGoogleCalendarViewModel); // TODO: Presenter connecting sync interactor to UI
        SyncToGoogleCalendarInputBoundary syncInteractor =
                new SyncToGoogleCalendarInteractor(taskHabitDataAccessObject, calendarGateway, syncOutputBoundary); // TODO: Interactor to sync tasks to calendar
        syncToGoogleCalendarController = new SyncToGoogleCalendarController(syncInteractor); // TODO: Controller invoked by logged-in view
        loggedInView.setSyncToGoogleCalendarController(syncToGoogleCalendarController); // TODO: Inject controller into logged-in view
        loggedInView.setSyncToGoogleCalendarViewModel(syncToGoogleCalendarViewModel); // TODO: Provide sync view model for UI updates
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("User Login Example");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChanged();

        return application;
    }
}

