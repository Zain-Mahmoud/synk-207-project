package app;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import data_access.FileUserDataAccessObject;
import data_access.GoogleCalendarDataAccessObject;
import data_access.HabitDataAccessObject;
import data_access.TaskDataAccessObject;
import entities.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.create_habit.CreateHabitController;
import interface_adapter.create_habit.CreateHabitPresenter;
import interface_adapter.create_habit.CreateHabitViewModel;
import interface_adapter.create_task.CreateTaskController;
import interface_adapter.create_task.CreateTaskPresenter;
import interface_adapter.create_task.CreateTaskViewModel;
import interface_adapter.delete_habit.DeleteHabitController;
import interface_adapter.delete_habit.DeleteHabitPresenter;
import interface_adapter.delete_habit.DeleteHabitViewModel;
import interface_adapter.delete_task.DeleteTaskController;
import interface_adapter.delete_task.DeleteTaskPresenter;
import interface_adapter.delete_task.DeleteTaskViewModel;
import interface_adapter.leaderboard.ViewLeaderboardController;
import interface_adapter.leaderboard.ViewLeaderboardPresenter;
import interface_adapter.leaderboard.ViewLeaderboardViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.modify_habit.ModifyHabitController;
import interface_adapter.modify_habit.ModifyHabitPresenter;
import interface_adapter.modify_habit.ModifyHabitViewModel;
import interface_adapter.modify_task.ModifyTaskController;
import interface_adapter.modify_task.ModifyTaskPresenter;
import interface_adapter.modify_task.ModifyTaskViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarController;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarPresenter;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarViewModel;
import interface_adapter.update_profile.UpdateProfileController;
import interface_adapter.update_profile.UpdateProfilePresenter;
import interface_adapter.update_profile.UpdateProfileViewModel;
import interface_adapter.view_stats.ViewStatsController;
import interface_adapter.view_stats.ViewStatsPresenter;
import interface_adapter.view_stats.ViewStatsViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsPresenter;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import use_case.create_habit.CreateHabitInputBoundary;
import use_case.create_habit.CreateHabitInteractor;
import use_case.create_habit.CreateHabitOutputBoundary;
import use_case.create_task.CreateTaskInputBoundary;
import use_case.create_task.CreateTaskInteractor;
import use_case.create_task.CreateTaskOutputBoundary;
import use_case.delete_habit.DeleteHabitInputBoundary;
import use_case.delete_habit.DeleteHabitInteractor;
import use_case.delete_habit.DeleteHabitOutputBoundary;
import use_case.delete_task.DeleteTaskInputBoundary;
import use_case.delete_task.DeleteTaskInteractor;
import use_case.delete_task.DeleteTaskOutputBoundary;
import use_case.gateways.CalendarGateway;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.modify_habit.ModifyHabitInputBoundary;
import use_case.modify_habit.ModifyHabitInteractor;
import use_case.modify_habit.ModifyHabitOutputBoundary;
import use_case.modify_task.ModifyTaskInputBoundary;
import use_case.modify_task.ModifyTaskInteractor;
import use_case.modify_task.ModifyTaskOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInteractor;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputBoundary;
import use_case.update_profile.UpdateProfileBoundary;
import use_case.update_profile.UpdateProfileInteractor;
import use_case.update_profile.UpdateProfileOutputBoundary;
import use_case.view_leaderboard.ViewLeaderboardInputBoundary;
import use_case.view_leaderboard.ViewLeaderboardInteractor;
import use_case.view_leaderboard.ViewLeaderboardOutputBoundary;
import use_case.view_stats.ViewStatsInputBoundary;
import use_case.view_stats.ViewStatsInteractor;
import use_case.view_stats.ViewStatsOutputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInteractor;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsOutputBoundary;
import view.CreateHabitView;
import view.CreateTaskView;
import view.DeleteHabitView;
import view.DeleteTaskView;
import view.LeaderboardView;
import view.LoggedInView;
import view.LoginView;
import view.ModifyHabitView;
import view.ModifyTaskView;
import view.SignupView;
import view.StatsView;
import view.UpdateProfileView;
import view.ViewManager;
import view.ViewTasksAndHabitsView;

public class AppBuilder {
    private final StackPane rootPane = new StackPane();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(rootPane, viewManagerModel);

    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);
    final TaskDataAccessObject taskDataAccessObject = new TaskDataAccessObject();
    final HabitDataAccessObject habitDataAccessObject = new HabitDataAccessObject();
    private final CalendarGateway calendarGateway;

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
    private SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel;
    private SyncToGoogleCalendarController syncToGoogleCalendarController;
    private ModifyTaskViewModel modifyTaskViewModel;
    private ModifyTaskView modifyTaskView;
    private ModifyTaskController modifyTaskController;
    private ModifyHabitView modifyHabitView;
    private ModifyHabitController modifyHabitController;
    private ModifyHabitViewModel modifyHabitViewModel;
    private ViewStatsViewModel viewStatsViewModel;
    private StatsView statsView;
    private CreateTaskViewModel createTaskViewModel;
    private DeleteTaskViewModel deleteTaskViewModel;
    private CreateHabitViewModel createHabitViewModel;
    private DeleteHabitViewModel deleteHabitViewModel;
    private CreateTaskView createTaskView;
    private DeleteTaskView deleteTaskView;
    private CreateHabitView createHabitView;
    private DeleteHabitView deleteHabitView;
    private UpdateProfileViewModel updateProfileViewModel;
    private UpdateProfileView updateProfileView;

    public AppBuilder() throws IOException, GeneralSecurityException {
        calendarGateway = new GoogleCalendarDataAccessObject();
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        viewManager.addView(signupView.getViewName(), signupView);
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        viewManager.addView(loginView.getViewName(), loginView);
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        loggedInView.setViewManagerModel(viewManagerModel);
        viewManager.addView(loggedInView.getViewName(), loggedInView);
        return this;
    }

    public AppBuilder addViewTasksAndHabitsView() {
        viewTasksAndHabitsViewModel = new ViewTasksAndHabitsViewModel();
        viewtasksAndHabitsView = new ViewTasksAndHabitsView(viewTasksAndHabitsViewModel, viewManagerModel,
                loggedInViewModel, modifyHabitViewModel, modifyTaskViewModel);
        viewtasksAndHabitsView.setViewManagerModel(viewManagerModel);
        viewManager.addView(viewtasksAndHabitsView.getViewName(), viewtasksAndHabitsView);
        return this;
    }

    public AppBuilder addLeaderboardView() {
        viewLeaderboardViewModel = new ViewLeaderboardViewModel();
        leaderboardView = new LeaderboardView(viewLeaderboardViewModel);
        leaderboardView.setViewManagerModel(viewManagerModel);
        viewManager.addView(leaderboardView.getViewName(), leaderboardView);
        return this;
    }

    public AppBuilder addUpdateProfileView() {
        updateProfileViewModel = new UpdateProfileViewModel();
        updateProfileView = new UpdateProfileView(updateProfileViewModel);
        updateProfileView.setViewManagerModel(viewManagerModel);
        viewManager.addView(updateProfileView.getViewName(), updateProfileView);
        return this;
    }

    public AppBuilder addModifyTaskView() {
        modifyTaskViewModel = new ModifyTaskViewModel();
        modifyTaskView = new ModifyTaskView(modifyTaskViewModel);
        modifyTaskView.setViewManagerModel(viewManagerModel);
        viewManager.addView(modifyTaskView.getViewName(), modifyTaskView);
        return this;
    }

    public AppBuilder addModifyHabitView() {
        modifyHabitViewModel = new ModifyHabitViewModel();
        modifyHabitView = new ModifyHabitView(modifyHabitViewModel);
        modifyHabitView.setViewManagerModel(viewManagerModel);
        viewManager.addView(modifyHabitView.getViewName(), modifyHabitView);
        return this;
    }

    public AppBuilder addStatsView() {
        viewStatsViewModel = new ViewStatsViewModel();
        statsView = new StatsView(viewStatsViewModel, viewManagerModel);
        viewManager.addView(statsView.getViewName(), statsView);
        return this;
    }

    public AppBuilder addCreateTaskView() {
        if (createTaskViewModel == null) {
            createTaskViewModel = new CreateTaskViewModel("create task");
        }
        createTaskView = new CreateTaskView(createTaskViewModel, viewManagerModel, loggedInViewModel);
        viewManager.addView(createTaskView.getViewName(), createTaskView);
        return this;
    }

    public AppBuilder addCreateHabitView() {
        if (createHabitViewModel == null) {
            createHabitViewModel = new CreateHabitViewModel("create habit");
        }
        createHabitView = new CreateHabitView(createHabitViewModel, viewManagerModel, loggedInViewModel);
        viewManager.addView(createHabitView.getViewName(), createHabitView);
        return this;
    }

    public AppBuilder addDeleteTaskView() {
        if (deleteTaskViewModel == null) {
            deleteTaskViewModel = new DeleteTaskViewModel("delete task");
        }
        deleteTaskView = new DeleteTaskView(deleteTaskViewModel, viewManagerModel, loggedInViewModel);
        viewManager.addView(deleteTaskView.getViewName(), deleteTaskView);
        return this;
    }

    public AppBuilder addDeleteHabitView() {
        if (deleteHabitViewModel == null) {
            deleteHabitViewModel = new DeleteHabitViewModel("delete habit");
        }
        deleteHabitView = new DeleteHabitView(deleteHabitViewModel, viewManagerModel, loggedInViewModel);
        viewManager.addView(deleteHabitView.getViewName(), deleteHabitView);
        return this;
    }

    // ── Use Case wiring (unchanged from original) ──

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
                loggedInViewModel, loginViewModel, updateProfileViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);
        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        // Wire "Don't have an account? Sign up" navigation
        loginView.getToSignupButton().setOnAction(evt -> {
            viewManagerModel.setState("sign up");
            viewManagerModel.firePropertyChanged();
        });
        return this;
    }

    public AppBuilder addViewLeaderboardUseCase() {
        final ViewLeaderboardOutputBoundary viewLeaderboardOutputBoundary = new ViewLeaderboardPresenter(
                viewLeaderboardViewModel);
        final ViewLeaderboardInputBoundary viewLeaderboardInteractor = new ViewLeaderboardInteractor(
                habitDataAccessObject, viewLeaderboardOutputBoundary);
        ViewLeaderboardController viewLeaderboardController = new ViewLeaderboardController(viewLeaderboardInteractor);
        leaderboardView.setViewLeaderboardController(viewLeaderboardController);
        return this;
    }

    public AppBuilder addUpdateProfileUseCase() {
        final UpdateProfileOutputBoundary updateProfileOutputBoundary =
                new UpdateProfilePresenter(viewManagerModel, updateProfileViewModel, loggedInViewModel);
        final UpdateProfileBoundary updateProfileInteractor =
                new UpdateProfileInteractor(userDataAccessObject, updateProfileOutputBoundary);
        UpdateProfileController updateProfileController = new UpdateProfileController(updateProfileInteractor);
        updateProfileView.setUpdateProfileController(updateProfileController);
        return this;
    }

    public AppBuilder addSyncToGoogleCalendarUseCase() {
        if (syncToGoogleCalendarViewModel == null) {
            syncToGoogleCalendarViewModel = new SyncToGoogleCalendarViewModel();
        }
        SyncToGoogleCalendarOutputBoundary syncOutputBoundary = new SyncToGoogleCalendarPresenter(
                syncToGoogleCalendarViewModel);
        SyncToGoogleCalendarInputBoundary syncInteractor = new SyncToGoogleCalendarInteractor(taskDataAccessObject,
                calendarGateway, syncOutputBoundary);
        syncToGoogleCalendarController = new SyncToGoogleCalendarController(syncInteractor);
        loggedInView.setSyncToGoogleCalendarController(syncToGoogleCalendarController);
        loggedInView.setSyncToGoogleCalendarViewModel(syncToGoogleCalendarViewModel);
        return this;
    }

    public AppBuilder addModifyTaskUseCase() {
        final ModifyTaskOutputBoundary modifyTaskOutputBoundary = new ModifyTaskPresenter(viewManagerModel,
                modifyTaskViewModel, viewTasksAndHabitsViewModel);
        final ModifyTaskInputBoundary modifyTaskInteractor = new ModifyTaskInteractor(modifyTaskOutputBoundary,
                taskDataAccessObject);
        modifyTaskController = new ModifyTaskController(modifyTaskInteractor, loggedInViewModel);
        modifyTaskView.setModifyTaskController(modifyTaskController);
        return this;
    }

    public AppBuilder addModifyHabitUseCase() {
        final ModifyHabitOutputBoundary modifyHabitOutputBoundary = new ModifyHabitPresenter(viewManagerModel,
                modifyHabitViewModel, viewTasksAndHabitsViewModel);
        final ModifyHabitInputBoundary modifyHabitInteractor = new ModifyHabitInteractor(modifyHabitOutputBoundary,
                habitDataAccessObject);
        modifyHabitController = new ModifyHabitController(modifyHabitInteractor, loggedInViewModel);
        modifyHabitView.setModifyHabitController(modifyHabitController);
        return this;
    }

    public AppBuilder addViewTasksAndHabitsUseCase() {
        final ViewTasksAndHabitsOutputBoundary viewTasksAndHabitsOutputBoundary = new ViewTasksAndHabitsPresenter(
                viewManagerModel, viewTasksAndHabitsViewModel);
        final ViewTasksAndHabitsInputBoundary viewTasksAndHabitsInteractor = new ViewTasksAndHabitsInteractor(
                taskDataAccessObject, habitDataAccessObject, viewTasksAndHabitsOutputBoundary);
        this.viewTasksAndHabitsController =
                new ViewTasksAndHabitsController(viewTasksAndHabitsInteractor, loggedInViewModel);
        loggedInView.setViewTasksAndHabitsController(viewTasksAndHabitsController);
        viewtasksAndHabitsView.setViewTasksAndHabitsController(viewTasksAndHabitsController);
        return this;
    }

    public AppBuilder addViewStatsUseCase() {
        final ViewStatsOutputBoundary viewStatsOutputBoundary = new ViewStatsPresenter(viewStatsViewModel,
                viewManagerModel);
        final ViewStatsInputBoundary viewStatsInteractor = new ViewStatsInteractor(habitDataAccessObject,
                taskDataAccessObject, viewStatsOutputBoundary);
        ViewStatsController viewStatsController = new ViewStatsController(viewStatsInteractor, loggedInViewModel);
        loggedInView.setViewStatsController(viewStatsController);
        return this;
    }

    public AppBuilder addCreateTaskUseCase() {
        if (createTaskViewModel == null) {
            createTaskViewModel = new CreateTaskViewModel("create task");
        }
        final CreateTaskOutputBoundary createTaskOutputBoundary = new CreateTaskPresenter(createTaskViewModel,
                loggedInViewModel, viewManagerModel, viewTasksAndHabitsController);
        final CreateTaskInputBoundary createTaskInteractor = new CreateTaskInteractor(taskDataAccessObject,
                createTaskOutputBoundary);
        final CreateTaskController createTaskController = new CreateTaskController(createTaskInteractor);
        createTaskView.setCreateTaskController(createTaskController);
        return this;
    }

    public AppBuilder addDeleteTaskUseCase() {
        if (deleteTaskViewModel == null) {
            deleteTaskViewModel = new DeleteTaskViewModel("delete task");
        }
        final DeleteTaskOutputBoundary deleteTaskOutputBoundary =
                new DeleteTaskPresenter(deleteTaskViewModel, viewManagerModel, loggedInViewModel,
                        viewTasksAndHabitsController);
        final DeleteTaskInputBoundary deleteTaskInteractor =
                new DeleteTaskInteractor(deleteTaskOutputBoundary, taskDataAccessObject);
        final DeleteTaskController deleteTaskController = new DeleteTaskController(deleteTaskInteractor);
        deleteTaskView.setDeleteTaskController(deleteTaskController);
        return this;
    }

    public AppBuilder addCreateHabitUseCase() {
        if (createHabitViewModel == null) {
            createHabitViewModel = new CreateHabitViewModel("create habit");
        }
        final CreateHabitOutputBoundary createHabitOutputBoundary = new CreateHabitPresenter(createHabitViewModel,
                loggedInViewModel, viewManagerModel, viewTasksAndHabitsController);
        final CreateHabitInputBoundary createHabitInteractor = new CreateHabitInteractor(habitDataAccessObject,
                createHabitOutputBoundary);
        final CreateHabitController createHabitController = new CreateHabitController(createHabitInteractor);
        createHabitView.setCreateHabitController(createHabitController);
        return this;
    }

    public AppBuilder addDeleteHabitUseCase() {
        if (deleteHabitViewModel == null) {
            deleteHabitViewModel = new DeleteHabitViewModel("delete habit");
        }
        final DeleteHabitOutputBoundary deleteHabitOutputBoundary =
                new DeleteHabitPresenter(deleteHabitViewModel, viewManagerModel, loggedInViewModel,
                        viewTasksAndHabitsController);
        final DeleteHabitInputBoundary deleteHabitInteractor =
                new DeleteHabitInteractor(deleteHabitOutputBoundary, habitDataAccessObject);
        final DeleteHabitController deleteHabitController = new DeleteHabitController(deleteHabitInteractor);
        deleteHabitView.setDeleteHabitController(deleteHabitController);
        return this;
    }

    public Scene build() {
        Scene scene = new Scene(rootPane);

        // Show the signup view by default
        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChanged();

        return scene;
    }
}
