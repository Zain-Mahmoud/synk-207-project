package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.modify_habit.ModifyHabitState;
import interface_adapter.modify_habit.ModifyHabitViewModel;
import interface_adapter.modify_task.ModifyTaskState;
import interface_adapter.modify_task.ModifyTaskViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * View for displaying and managing tasks and habits (JavaFX).
 * Displays two tables with action buttons.
 */
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class ViewTasksAndHabitsView extends VBox implements PropertyChangeListener {

    private final String viewName = "view tasks and habits";
    private final ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ModifyHabitViewModel modifyHabitViewModel;
    private final ModifyTaskViewModel modifyTaskViewModel;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;

    private final TableView<ArrayList<String>> tasksTable = new TableView<>();
    private final TableView<ArrayList<String>> habitsTable = new TableView<>();

    private final Button backButton = new Button("\u2190 Back");
    private final Button createTaskButton = new Button("Create Task");
    private final Button createHabitButton = new Button("Create Habit");
    private final Button deleteTaskButton = new Button("Delete Task");
    private final Button deleteHabitButton = new Button("Delete Habit");

    public ViewTasksAndHabitsView(ViewTasksAndHabitsViewModel viewModel,
                                   ViewManagerModel viewManagerModel,
                                   LoggedInViewModel loggedInViewModel,
                                   ModifyHabitViewModel modifyHabitViewModel,
                                   ModifyTaskViewModel modifyTaskViewModel) {
        this.viewTasksAndHabitsViewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.modifyHabitViewModel = modifyHabitViewModel;
        this.modifyTaskViewModel = modifyTaskViewModel;

        this.viewTasksAndHabitsViewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("dashboard-container");
        this.setPadding(new Insets(20));
        this.setSpacing(16);

        setupTasksTable();
        setupHabitsTable();

        // ── Action buttons ──
        backButton.getStyleClass().add("back-btn");
        createTaskButton.getStyleClass().add("dashboard-btn");
        createHabitButton.getStyleClass().add("dashboard-btn");
        deleteTaskButton.getStyleClass().add("dashboard-btn");
        deleteTaskButton.setStyle("-fx-background-color: #3A3A4E;");
        deleteHabitButton.getStyleClass().add("dashboard-btn");
        deleteHabitButton.setStyle("-fx-background-color: #3A3A4E;");

        backButton.setOnAction(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChanged();
        });

        createTaskButton.setOnAction(e -> {
            viewManagerModel.setState("create task");
            viewManagerModel.firePropertyChanged();
        });

        createHabitButton.setOnAction(e -> {
            viewManagerModel.setState("create habit");
            viewManagerModel.firePropertyChanged();
        });

        deleteTaskButton.setOnAction(e -> {
            viewManagerModel.setState("delete task");
            viewManagerModel.firePropertyChanged();
        });

        deleteHabitButton.setOnAction(e -> {
            viewManagerModel.setState("delete habit");
            viewManagerModel.firePropertyChanged();
        });

        HBox taskButtons = new HBox(12, createTaskButton, deleteTaskButton);
        taskButtons.setAlignment(Pos.CENTER_LEFT);

        HBox habitButtons = new HBox(12, createHabitButton, deleteHabitButton);
        habitButtons.setAlignment(Pos.CENTER_LEFT);

        Label tasksTitle = new Label("Tasks");
        tasksTitle.getStyleClass().add("form-title");

        Label habitsTitle = new Label("Habits");
        habitsTitle.getStyleClass().add("form-title");

        VBox tasksSection = new VBox(8, tasksTitle, taskButtons, tasksTable);
        VBox.setVgrow(tasksTable, Priority.ALWAYS);

        VBox habitsSection = new VBox(8, habitsTitle, habitButtons, habitsTable);
        VBox.setVgrow(habitsTable, Priority.ALWAYS);

        VBox.setVgrow(tasksSection, Priority.ALWAYS);
        VBox.setVgrow(habitsSection, Priority.ALWAYS);

        this.getChildren().addAll(backButton, tasksSection, habitsSection);
    }

    @SuppressWarnings("unchecked")
    private void setupTasksTable() {
        String[] headers = {"Name", "Description", "Start", "Deadline", "Group", "Status", "Priority"};
        for (int i = 0; i < headers.length; i++) {
            final int colIdx = i;
            TableColumn<ArrayList<String>, String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(data -> {
                ArrayList<String> row = data.getValue();
                return new SimpleStringProperty(colIdx < row.size() ? row.get(colIdx) : "");
            });
            tasksTable.getColumns().add(col);
        }

        // Modify button column
        TableColumn<ArrayList<String>, Void> modifyCol = new TableColumn<>("Action");
        modifyCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modify");
            {
                btn.getStyleClass().add("back-btn");
                btn.setOnAction(e -> {
                    ArrayList<String> row = getTableView().getItems().get(getIndex());
                    handleModifyTask(row);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        tasksTable.getColumns().add(modifyCol);
        tasksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    @SuppressWarnings("unchecked")
    private void setupHabitsTable() {
        String[] headers = {"Name", "Start", "Frequency", "Group", "Status", "Priority", "Streak"};
        for (int i = 0; i < headers.length; i++) {
            final int colIdx = i;
            TableColumn<ArrayList<String>, String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(data -> {
                ArrayList<String> row = data.getValue();
                return new SimpleStringProperty(colIdx < row.size() ? row.get(colIdx) : "");
            });
            habitsTable.getColumns().add(col);
        }

        // Modify button column
        TableColumn<ArrayList<String>, Void> modifyCol = new TableColumn<>("Action");
        modifyCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modify");
            {
                btn.getStyleClass().add("back-btn");
                btn.setOnAction(e -> {
                    ArrayList<String> row = getTableView().getItems().get(getIndex());
                    handleModifyHabit(row);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        habitsTable.getColumns().add(modifyCol);
        habitsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void handleModifyTask(ArrayList<String> row) {
        if (row.size() >= 7) {
            ModifyTaskState state = new ModifyTaskState();
            state.setOldTaskName(row.get(0));
            state.setNewTaskName(row.get(0));
            state.setOldDescription(row.get(1));
            state.setDescription(row.get(1));
            state.setOldStartDateTime(row.get(2));
            state.setStartDateTime(row.get(2));
            state.setOldDeadline(row.get(3));
            state.setDeadline(row.get(3));
            state.setOldTaskGroup(row.get(4));
            state.setTaskGroup(row.get(4));
            boolean isComplete = "Completed".equalsIgnoreCase(row.get(5));
            state.setOldStatus(isComplete);
            state.setStatus(isComplete);
            state.setOldPriority(row.get(6));
            state.setPriority(row.get(6));
            modifyTaskViewModel.setState(state);
            modifyTaskViewModel.firePropertyChanged();

            viewManagerModel.setState("modify task");
            viewManagerModel.firePropertyChanged();
        }
    }

    private void handleModifyHabit(ArrayList<String> row) {
        if (row.size() >= 7) {
            ModifyHabitState state = new ModifyHabitState();
            state.setOldHabitName(row.get(0));
            state.setHabitName(row.get(0));
            state.setOldStartDateTime(row.get(1));
            state.setStartDateTime(row.get(1));
            state.setOldFrequency(row.get(2));
            state.setFrequency(row.get(2));
            state.setOldHabitGroup(row.get(3));
            state.setHabitGroup(row.get(3));
            boolean isComplete = "Completed".equalsIgnoreCase(row.get(4));
            state.setOldStatus(isComplete);
            state.setStatus(isComplete);
            state.setOldPriority(row.get(5));
            state.setPriority(row.get(5));
            state.setOldStreakCount(row.get(6));
            state.setStreakCount(row.get(6));
            modifyHabitViewModel.setState(state);
            modifyHabitViewModel.firePropertyChanged();

            viewManagerModel.setState("modify habit");
            viewManagerModel.firePropertyChanged();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            if (evt.getNewValue() instanceof ViewTasksAndHabitsState) {
                ViewTasksAndHabitsState state = (ViewTasksAndHabitsState) evt.getNewValue();
                updateTasksTable(state.getFormattedTasks());
                updateHabitsTable(state.getFormattedHabits());
            }
        });
    }

    private void updateTasksTable(ArrayList<ArrayList<String>> tasks) {
        if (tasks == null) return;
        ObservableList<ArrayList<String>> data = FXCollections.observableArrayList(tasks);
        tasksTable.setItems(data);
    }

    private void updateHabitsTable(ArrayList<ArrayList<String>> habits) {
        if (habits == null) return;
        ObservableList<ArrayList<String>> data = FXCollections.observableArrayList(habits);
        habitsTable.setItems(data);
    }

    public String getViewName() { return viewName; }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        // Already set via constructor
    }

    public void setViewTasksAndHabitsController(ViewTasksAndHabitsController controller) {
        this.viewTasksAndHabitsController = controller;
    }
}
