package app;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the SYNK JavaFX application.
 * Sets up the AtlantaFX dark theme and delegates to AppBuilder for wiring.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Apply the AtlantaFX dark theme globally
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

        AppBuilder appBuilder = new AppBuilder();
        Scene scene = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addLeaderboardView()
                .addModifyTaskView()
                .addModifyHabitView()
                .addViewTasksAndHabitsView()
                .addCreateTaskView()
                .addCreateHabitView()
                .addDeleteTaskView()
                .addDeleteHabitView()
                .addUpdateProfileView()
                .addStatsView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addModifyTaskUseCase()
                .addModifyHabitUseCase()
                .addViewLeaderboardUseCase()
                .addViewTasksAndHabitsUseCase()
                .addCreateTaskUseCase()
                .addDeleteTaskUseCase()
                .addCreateHabitUseCase()
                .addDeleteHabitUseCase()
                .addSyncToGoogleCalendarUseCase()
                .addViewStatsUseCase()
                .addUpdateProfileUseCase()
                .build();

        // Load the custom dark-mode CSS on top of AtlantaFX
        scene.getStylesheets().add(
                getClass().getResource("/styles/dark-theme.css").toExternalForm());

        primaryStage.setTitle("SYNK — Productivity & Habit Tracker");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
