package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.create_task.CreateTaskController;
import interface_adapter.create_task.CreateTaskState;
import interface_adapter.create_task.CreateTaskViewModel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * View for creating a new Task (JavaFX).
 */
public class CreateTaskView extends VBox implements PropertyChangeListener {

    private final String viewName = "create task";
    private final CreateTaskViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private CreateTaskController createTaskController;

    private final TextField taskNameField = new TextField();
    private final TextField descriptionField = new TextField();
    private final DatePicker startDatePicker = new DatePicker(LocalDate.now());
    private final Spinner<Integer> startHourSpinner = new Spinner<>(0, 23, 9);
    private final Spinner<Integer> startMinuteSpinner = new Spinner<>(0, 59, 0);
    private final DatePicker deadlineDatePicker = new DatePicker(LocalDate.now().plusDays(1));
    private final Spinner<Integer> deadlineHourSpinner = new Spinner<>(0, 23, 17);
    private final Spinner<Integer> deadlineMinuteSpinner = new Spinner<>(0, 59, 0);
    private final TextField taskGroupField = new TextField();
    private final TextField priorityField = new TextField("1");

    private final Label messageLabel = new Label();
    private final Button createButton = new Button("Create Task");
    private final Button cancelButton = new Button("Cancel");

    public CreateTaskView(CreateTaskViewModel viewModel,
                          ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("form-container");
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(30, 50, 30, 50));
        this.setSpacing(12);

        Label title = new Label("Create Task");
        title.getStyleClass().add("form-title");

        for (TextField f : new TextField[]{taskNameField, descriptionField, taskGroupField, priorityField}) {
            f.getStyleClass().add("form-field");
        }

        messageLabel.getStyleClass().add("auth-error");

        createButton.getStyleClass().add("form-btn-save");
        cancelButton.getStyleClass().add("form-btn-cancel");

        createButton.setOnAction(evt -> handleCreate());
        cancelButton.setOnAction(evt -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChanged();
        });


        HBox startTimeRow = new HBox(8, startHourSpinner, new Label(":"), startMinuteSpinner);
        startTimeRow.setAlignment(Pos.CENTER_LEFT);

        HBox deadlineTimeRow = new HBox(8, deadlineHourSpinner, new Label(":"), deadlineMinuteSpinner);
        deadlineTimeRow.setAlignment(Pos.CENTER_LEFT);


        HBox buttons = new HBox(12, createButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(title,
                createField("Task Name", taskNameField),
                createField("Description", descriptionField),
                createFieldWrapped("Start Date", startDatePicker),
                createFieldWrapped("Start Time", startTimeRow),
                createFieldWrapped("Deadline", deadlineDatePicker),
                createFieldWrapped("Deadline Time", deadlineTimeRow),
                createField("Task Group", taskGroupField),
                createField("Priority (number)", priorityField),
                messageLabel, buttons);
    }

    private VBox createField(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-field-label");
        VBox box = new VBox(4, label, field);
        box.setMaxWidth(400);
        return box;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-field-label");
        return label;
    }

    private VBox createFieldWrapped(String labelText, javafx.scene.Node node) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-field-label");
        VBox box = new VBox(4, label, node);
        box.setMaxWidth(400);
        return box;
    }


    private void handleCreate() {
        if (createTaskController == null) {
            showError("Create Task controller not initialized.");
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String taskName = taskNameField.getText().trim();
        String description = descriptionField.getText().trim();
        String group = taskGroupField.getText().trim();
        String priorityText = priorityField.getText().trim();

        if (taskName.isEmpty()) {
            showError("Task name cannot be empty.");
            return;
        }

        try {
            LocalDateTime startTime = LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue()));
            LocalDateTime deadline = LocalDateTime.of(
                    deadlineDatePicker.getValue(),
                    LocalTime.of(deadlineHourSpinner.getValue(), deadlineMinuteSpinner.getValue()));

            if (deadline.isBefore(startTime)) {
                showError("Deadline must be after the start time.");
                return;
            }

            int priority = Integer.parseInt(priorityText);
            createTaskController.execute(username, taskName, description,
                    startTime, deadline, group, false, priority);
        } catch (Exception ex) {
            showError("Invalid input: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            if (!"state".equals(evt.getPropertyName())) return;
            CreateTaskState state = viewModel.getState();
            if (state.getErrorMessage() != null) {
                showError(state.getErrorMessage());
                state.setErrorMessage(null);
                viewModel.setState(state);
            } else if (state.getSuccessMessage() != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText(state.getSuccessMessage());
                alert.showAndWait();

                taskNameField.setText("");
                descriptionField.setText("");
                taskGroupField.setText("");
                priorityField.setText("1");

                state.setSuccessMessage(null);
                viewModel.setState(state);

                viewManagerModel.setState("view tasks and habits");
                viewManagerModel.firePropertyChanged();
            }
        });
    }

    public String getViewName() {
        return viewName;
    }

    public void setCreateTaskController(CreateTaskController controller) {
        this.createTaskController = controller;
    }
}
