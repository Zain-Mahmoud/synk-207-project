package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.leaderboard.ViewLeaderboardController;
import interface_adapter.leaderboard.ViewLeaderboardState;
import interface_adapter.leaderboard.ViewLeaderboardViewModel;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

/**
 * The View for displaying the Habit Streak Leaderboard (JavaFX).
 */
public class LeaderboardView extends VBox implements PropertyChangeListener {

    private final String viewName = "leaderboard";
    private final ViewLeaderboardViewModel viewLeaderboardViewModel;
    private ViewLeaderboardController viewLeaderboardController;
    private ViewManagerModel viewManagerModel;

    private final TableView<Map<String, Object>> table = new TableView<>();
    private final Label errorLabel = new Label();
    private final Button refreshButton = new Button("Refresh");
    private final Button backButton = new Button("← Back");

    public LeaderboardView(ViewLeaderboardViewModel viewLeaderboardViewModel) {
        this.viewLeaderboardViewModel = viewLeaderboardViewModel;
        this.viewLeaderboardViewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("leaderboard-container");
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(30));
        this.setSpacing(16);

        Label title = new Label("🏆 Habit Streak Leaderboard");
        title.getStyleClass().add("leaderboard-title");

        setupTable();

        errorLabel.getStyleClass().add("auth-error");

        refreshButton.getStyleClass().add("dashboard-btn");
        refreshButton.setPrefWidth(160);
        refreshButton.setOnAction(evt -> {
            if (viewLeaderboardController != null) {
                viewLeaderboardController.execute();
            }
        });

        backButton.getStyleClass().add("back-btn");
        backButton.setOnAction(evt -> {
            if (viewManagerModel != null) {
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChanged();
            }
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        this.getChildren().addAll(title, table, errorLabel, refreshButton, backButton);
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        TableColumn<Map<String, Object>, String> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getOrDefault("rank", ""))));
        rankCol.setPrefWidth(60);

        TableColumn<Map<String, Object>, String> nameCol = new TableColumn<>("Habit Name");
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getOrDefault("habitName", ""))));
        nameCol.setPrefWidth(200);

        TableColumn<Map<String, Object>, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getOrDefault("username", ""))));
        userCol.setPrefWidth(140);

        TableColumn<Map<String, Object>, Number> streakCol = new TableColumn<>("Streak");
        streakCol.setCellValueFactory(data -> {
            Object val = data.getValue().getOrDefault("streak", 0);
            if (val instanceof Number) {
                return new SimpleObjectProperty<>((Number) val);
            }
            return new SimpleObjectProperty<>(0);
        });
        streakCol.setPrefWidth(80);

        table.getColumns().addAll(rankCol, nameCol, userCol, streakCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            if (evt.getNewValue() instanceof ViewLeaderboardState) {
                final ViewLeaderboardState state = (ViewLeaderboardState) evt.getNewValue();
                if (state.getErrorMessage() != null) {
                    errorLabel.setText(state.getErrorMessage());
                } else {
                    errorLabel.setText("");
                    updateTable(state.getLeaderboardEntries());
                }
            }
        });
    }

    private void updateTable(List<Map<String, Object>> entries) {
        if (entries == null) return;
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList(entries);
        table.setItems(data);
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewLeaderboardController(ViewLeaderboardController controller) {
        this.viewLeaderboardController = controller;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
        // Auto-refresh when becoming visible
        this.visibleProperty().addListener((obs, wasVisible, isNowVisible) -> {
            if (isNowVisible && viewLeaderboardController != null) {
                viewLeaderboardController.execute();
            }
        });
    }
}
