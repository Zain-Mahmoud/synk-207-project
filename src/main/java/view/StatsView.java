package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.view_stats.ViewStatsState;
import interface_adapter.view_stats.ViewStatsViewModel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StatsView extends VBox implements PropertyChangeListener {

    private final ViewManagerModel viewManagerModel;
    private final String viewName = "view stats";
    private final ViewStatsViewModel viewStatsViewModel;

    private final Label maxStreakValue = new Label("0");
    private final Label tasksCompletedValue = new Label("0 / 0");
    private final Label habitsCompletedValue = new Label("0 / 0");
    private final Button back = new Button("← Back");

    public StatsView(ViewStatsViewModel viewStatsViewModel, ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewStatsViewModel = viewStatsViewModel;
        this.viewStatsViewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("dashboard-container");
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(30));
        this.setSpacing(24);

        Label title = new Label("Your Productivity Dashboard");
        title.getStyleClass().add("form-title");

        HBox cardsRow = new HBox(20,
                createStatCard("Longest Active Streak", maxStreakValue),
                createStatCard("Tasks Completed", tasksCompletedValue),
                createStatCard("Habits Completed", habitsCompletedValue));
        cardsRow.setAlignment(Pos.CENTER);

        back.getStyleClass().add("back-btn");
        back.setOnAction(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChanged();
        });

        this.getChildren().addAll(title, cardsRow, back);
    }

    private VBox createStatCard(String titleText, Label valueLabel) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(200);

        Label cardTitle = new Label(titleText);
        cardTitle.getStyleClass().add("stat-card-title");

        valueLabel.getStyleClass().add("stat-card-value");

        card.getChildren().addAll(cardTitle, valueLabel);
        return card;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            if (evt.getNewValue() instanceof ViewStatsState) {
                final ViewStatsState currState = (ViewStatsState) evt.getNewValue();
                setText(currState);
            }
        });
    }

    private void setText(ViewStatsState currState) {
        maxStreakValue.setText(String.valueOf(currState.getLongestHabitStreak()));
        tasksCompletedValue.setText(currState.getNumTasksCompleted() + " / " + currState.getTotalTasks());
        habitsCompletedValue.setText(currState.getNumHabitsCompleted() + " / " + currState.getTotalHabits());
    }

    public void setViewManager(ViewManagerModel viewManagerModel) {
        // No-op, already set via constructor
    }

    public String getViewName() {
        return viewName;
    }
}
