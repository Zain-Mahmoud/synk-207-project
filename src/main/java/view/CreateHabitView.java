package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.create_habit.CreateHabitController;
import interface_adapter.create_habit.CreateHabitState;
import interface_adapter.create_habit.CreateHabitViewModel;

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
 * View for creating a new Habit (JavaFX).
 */
public class CreateHabitView extends VBox implements PropertyChangeListener {

    private final String viewName = "create habit";
    private final CreateHabitViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private CreateHabitController controller;

    private final TextField habitNameField = new TextField();
    private final DatePicker startDatePicker = new DatePicker(LocalDate.now());
    private final Spinner<Integer> startHourSpinner = new Spinner<>(0, 23, 9);
    private final Spinner<Integer> startMinuteSpinner = new Spinner<>(0, 59, 0);
    private final Spinner<Integer> frequencySpinner = new Spinner<>(1, 365, 1);
    private final TextField habitGroupField = new TextField();
    private final TextField streakCountField = new TextField("0");
    private final TextField priorityField = new TextField("1");

    private final Label messageLabel = new Label();
    private final Button createButton = new Button("Create Habit");
    private final Button cancelButton = new Button("Cancel");

    public CreateHabitView(CreateHabitViewModel viewModel,
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

        Label title = new Label("Create Habit");
        title.getStyleClass().add("form-title");

        for (TextField f : new TextField[]{habitNameField, habitGroupField, streakCountField, priorityField}) {
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


        HBox buttons = new HBox(12, createButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(title,
                createField("Habit Name", habitNameField),
                createFieldWrapped("Start Date", startDatePicker),
                createFieldWrapped("Start Time", startTimeRow),
                createFieldWrapped("Frequency (days)", frequencySpinner),
                createField("Habit Group", habitGroupField),
                createField("Streak Count", streakCountField),
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
        if (controller == null) {
            showError("Create Habit controller not initialized.");
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String habitName = habitNameField.getText().trim();

        if (habitName.isEmpty()) {
            showError("Habit name cannot be empty.");
            return;
        }

        try {
            LocalDateTime startDateTime = LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue()));
            int frequency = frequencySpinner.getValue();
            String habitGroup = habitGroupField.getText().trim();
            String streakText = streakCountField.getText().trim();
            String priorityText = priorityField.getText().trim();

            int streakCount = streakText.isEmpty() ? 0 : Integer.parseInt(streakText);
            int priority = Integer.parseInt(priorityText);

            controller.execute(username, habitName, startDateTime,
                    frequency, habitGroup, streakCount, priority);
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
            CreateHabitState state = viewModel.getState();
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

                habitNameField.setText("");
                habitGroupField.setText("");
                streakCountField.setText("0");
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

    public void setCreateHabitController(CreateHabitController controller) {
        this.controller = controller;
    }
}
