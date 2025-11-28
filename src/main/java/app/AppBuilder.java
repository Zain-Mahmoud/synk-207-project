package app;

import java.awt.CardLayout;
import java.io.IOException;
import java.security.GeneralSecurityException; // Type Safety

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.FileUserDataAccessObject;
import data_access.GoogleCalendarDataAccessObject;
import data_access.HabitDataAccessObject;
import data_access.TaskDataAccessObject;
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
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsPresenter;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarController;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarPresenter;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarViewModel;
import interface_adapter.view_stats.ViewStatsController;
import interface_adapter.view_stats.ViewStatsPresenter;
import interface_adapter.view_stats.ViewStatsViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.gateways.CalendarGateway;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInteractor;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputBoundary;
import use_case.view_leaderboard.ViewLeaderboardInputBoundary;
import use_case.view_leaderboard.ViewLeaderboardInteractor;
import use_case.view_leaderboard.ViewLeaderboardOutputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInteractor;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsOutputBoundary;
import view.*;
import use_case.view_stats.ViewStatsInputBoundary;
import use_case.view_stats.ViewStatsInteractor;
import use_case.view_stats.ViewStatsOutputBoundary;
import view.*;


public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // set which data access implementation to use, can be any
    // of the classes from the data_access package
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);
    final TaskDataAccessObject taskDataAccessObject = new TaskDataAccessObject();
    final HabitDataAccessObject habitDataAccessObject = new HabitDataAccessObject();
    private final CalendarGateway calendarGateway; // Calendar gateway used for syncing to Google Calendar

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private ViewTasksAndHabitsView viewtasksAndHabitsView;
    private ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;
    private LoginView loginView;
    private LeaderboardView leaderboardView;
    private ViewLeaderboardViewModel viewLeaderboardViewModel;
    private SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel; //View model carrying sync status updates
    private SyncToGoogleCalendarController syncToGoogleCalendarController; // Controller to kick off sync flow
    private ViewStatsViewModel viewStatsViewModel;
    private StatsView statsView;

    public AppBuilder() throws IOException, GeneralSecurityException { // Constructor now accounts for calendar gateway setup
        cardPanel.setLayout(cardLayout);
        calendarGateway = new GoogleCalendarDataAccessObject(); // Initialize Google Calendar gateway implementation
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

    public AppBuilder addViewTasksAndHabitsView() {
        viewTasksAndHabitsViewModel = new ViewTasksAndHabitsViewModel();
        viewtasksAndHabitsView = new ViewTasksAndHabitsView(viewTasksAndHabitsViewModel, viewManagerModel, loggedInViewModel, viewTasksAndHabitsController);
        viewtasksAndHabitsView.setViewManagerModel(viewManagerModel);
        cardPanel.add(viewtasksAndHabitsView, viewtasksAndHabitsView.getViewName());
        return this;
    }

    public AppBuilder addLeaderboardView() {
        viewLeaderboardViewModel = new ViewLeaderboardViewModel();
        leaderboardView = new LeaderboardView(viewLeaderboardViewModel);
        leaderboardView.setViewManagerModel(viewManagerModel);
        cardPanel.add(leaderboardView, leaderboardView.getViewName());
        return this;
    }

    public AppBuilder addStatsView(){
        viewStatsViewModel = new ViewStatsViewModel();
        statsView = new StatsView(viewStatsViewModel);
        statsView.setViewManager(viewManagerModel);
        cardPanel.add(statsView, statsView.getViewName());
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
                habitDataAccessObject, viewLeaderboardOutputBoundary);

        ViewLeaderboardController viewLeaderboardController = new ViewLeaderboardController(viewLeaderboardInteractor);
        leaderboardView.setViewLeaderboardController(viewLeaderboardController);
        return this;
    }

    public AppBuilder addSyncToGoogleCalendarUseCase() { // Wire sync-to-calendar use case components together
        if (syncToGoogleCalendarViewModel == null) {
            syncToGoogleCalendarViewModel = new SyncToGoogleCalendarViewModel();
        }
        SyncToGoogleCalendarOutputBoundary syncOutputBoundary =
                new SyncToGoogleCalendarPresenter(syncToGoogleCalendarViewModel); // Presenter connecting sync interactor to UI
        SyncToGoogleCalendarInputBoundary syncInteractor =
                new SyncToGoogleCalendarInteractor(taskDataAccessObject, calendarGateway, syncOutputBoundary); // Interactor to sync tasks to calendar
        syncToGoogleCalendarController = new SyncToGoogleCalendarController(syncInteractor); // Controller invoked by logged-in view
        loggedInView.setSyncToGoogleCalendarController(syncToGoogleCalendarController); // Inject controller into logged-in view
        loggedInView.setSyncToGoogleCalendarViewModel(syncToGoogleCalendarViewModel); // Provide sync view model for UI updates
        return this;
    }

    public AppBuilder addViewTasksAndHabitsUseCase() {
        final ViewTasksAndHabitsOutputBoundary viewTasksAndHabitsOutputBoundary = new ViewTasksAndHabitsPresenter(viewManagerModel, viewTasksAndHabitsViewModel);
        final ViewTasksAndHabitsInputBoundary viewTasksAndHabitsInteractor = new ViewTasksAndHabitsInteractor
                (taskDataAccessObject, habitDataAccessObject, userDataAccessObject, viewTasksAndHabitsOutputBoundary);

        ViewTasksAndHabitsController viewTasksAndHabitsController = new ViewTasksAndHabitsController(viewTasksAndHabitsInteractor, loggedInViewModel);
        loggedInView.setViewTasksAndHabitsController(viewTasksAndHabitsController);
        return this;
    }

    public AppBuilder addViewStatsUseCase(){
        final ViewStatsOutputBoundary viewStatsOutputBoundary = new ViewStatsPresenter(viewStatsViewModel,
                viewManagerModel);
        final ViewStatsInputBoundary viewStatsInteractor = new ViewStatsInteractor(habitDataAccessObject,
                taskDataAccessObject, viewStatsOutputBoundary);

        ViewStatsController viewStatsController = new ViewStatsController(viewStatsInteractor, loggedInViewModel);
        loggedInView.setViewStatsController(viewStatsController);

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

