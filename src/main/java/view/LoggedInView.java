package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarController;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarControllerState;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_stats.ViewStatsController;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * The logged-in dashboard view (JavaFX).
 */
public class LoggedInView extends VBox implements PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;

    private ViewManagerModel viewManagerModel;
    private SyncToGoogleCalendarController syncToGoogleCalendarController;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;
    private SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel;
    private ViewStatsController viewStatsController;

    private final Label usernameLabel;
    private final ImageView avatarView;
    private final Label syncStatusLabel;

    private final Button logOut;
    private final Button viewTasksAndHabits;
    private final Button viewLeaderboard;
    private final Button updateProfile;
    private final Button syncCalendarButton;
    private final Button viewStats;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("dashboard-container");
        this.setAlignment(Pos.CENTER);

        // ── Title ──
        Label title = new Label("SYNK");
        title.getStyleClass().add("dashboard-title");

        // ── User info ──
        avatarView = new ImageView();
        avatarView.setFitWidth(48);
        avatarView.setFitHeight(48);
        avatarView.setPreserveRatio(true);

        usernameLabel = new Label();
        usernameLabel.getStyleClass().add("dashboard-welcome");

        HBox userPanel = new HBox(12, avatarView, usernameLabel);
        userPanel.setAlignment(Pos.CENTER);

        // ── Buttons ──
        viewTasksAndHabits = createDashboardButton("View Tasks & Habits");
        viewStats = createDashboardButton("View Statistics");
        viewLeaderboard = createDashboardButton("View Leaderboard");
        updateProfile = createDashboardButton("Update Profile");
        syncCalendarButton = createDashboardButton("Sync to Google Calendar");
        logOut = createDashboardButton("Log Out");
        logOut.getStyleClass().addAll("dashboard-btn-danger");

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(16);
        buttonGrid.setVgap(16);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.add(viewTasksAndHabits, 0, 0);
        buttonGrid.add(viewStats, 1, 0);
        buttonGrid.add(viewLeaderboard, 0, 1);
        buttonGrid.add(updateProfile, 1, 1);
        buttonGrid.add(syncCalendarButton, 0, 2);
        buttonGrid.add(logOut, 1, 2);

        syncStatusLabel = new Label();
        syncStatusLabel.getStyleClass().add("sync-status");

        // ── Spacers ──
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        this.getChildren().addAll(topSpacer, title, userPanel, buttonGrid,
                syncStatusLabel, bottomSpacer);

        // ── Event handlers ──
        logOut.setOnAction(evt -> {
            if (viewManagerModel != null) {
                // Clear state if needed, then go to login
                viewManagerModel.setState("log in");
                viewManagerModel.firePropertyChanged();
            }
        });

        viewLeaderboard.setOnAction(evt -> {
            if (viewManagerModel != null) {
                viewManagerModel.setState("leaderboard");
                viewManagerModel.firePropertyChanged();
            }
        });

        updateProfile.setOnAction(evt -> {
            if (viewManagerModel != null) {
                LoggedInState state = loggedInViewModel.getState();
                viewManagerModel.setState("updateprofile");
                viewManagerModel.firePropertyChanged();
            }
        });

        viewTasksAndHabits.setOnAction(evt -> {
            if (viewManagerModel != null) {
                viewTasksAndHabitsController.getFormattedTasksAndHabits();
                viewManagerModel.setState("view tasks and habits");
                viewManagerModel.firePropertyChanged();
            }
        });

        viewStats.setOnAction(evt -> {
            if (viewManagerModel != null) {
                viewStatsController.execute();
                viewManagerModel.setState("view stats");
                viewManagerModel.firePropertyChanged();
            }
        });

        syncCalendarButton.setOnAction(evt -> {
            if (syncToGoogleCalendarController != null) {
                final LoggedInState currentState = loggedInViewModel.getState();
                syncToGoogleCalendarController.execute(currentState.getUsername());
            }
        });
    }

    private Button createDashboardButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("dashboard-btn");
        return button;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            if (evt.getPropertyName().equals("state")) {
                final LoggedInState state = (LoggedInState) evt.getNewValue();
                usernameLabel.setText("Welcome back, " + state.getUsername() + "!");
                updateAvatar(state.getAvatarPath());

                if (viewTasksAndHabitsController != null && state.getUsername() != null
                        && !state.getUsername().isEmpty()) {
                    viewTasksAndHabitsController.getFormattedTasksAndHabits();
                }
            } else if (evt.getPropertyName().equals("sync")) {
                final SyncToGoogleCalendarControllerState syncState =
                        (SyncToGoogleCalendarControllerState) evt.getNewValue();
                if (syncState.isSuccess()) {
                    syncStatusLabel.setText(syncState.getStatusMessage());
                    syncStatusLabel.getStyleClass().setAll("sync-status", "sync-status-success");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sync Complete");
                    alert.setHeaderText(null);
                    alert.setContentText(syncState.getStatusMessage());
                    alert.showAndWait();
                } else {
                    syncStatusLabel.setText("Error: " + syncState.getError());
                    syncStatusLabel.getStyleClass().setAll("sync-status", "sync-status-error");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Sync Failed");
                    alert.setHeaderText(null);
                    alert.setContentText(syncState.getError());
                    alert.showAndWait();
                }
            }
        });
    }

    private void updateAvatar(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            avatarView.setImage(null);
            return;
        }
        try {
            File file = new File(avatarPath);
            if (file.exists()) {
                avatarView.setImage(new Image(file.toURI().toString(), 48, 48, true, true));
            }
        } catch (Exception e) {
            avatarView.setImage(null);
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setSyncToGoogleCalendarController(SyncToGoogleCalendarController controller) {
        this.syncToGoogleCalendarController = controller;
    }

    public void setSyncToGoogleCalendarViewModel(SyncToGoogleCalendarViewModel viewModel) {
        this.syncToGoogleCalendarViewModel = viewModel;
        this.syncToGoogleCalendarViewModel.addPropertyChangeListener(this);
    }

    public void setViewTasksAndHabitsController(ViewTasksAndHabitsController controller) {
        this.viewTasksAndHabitsController = controller;
    }

    public void setViewStatsController(ViewStatsController viewStatsController) {
        this.viewStatsController = viewStatsController;
    }
}