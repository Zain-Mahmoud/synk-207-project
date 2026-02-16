package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.delete_task.DeleteTaskController;
import interface_adapter.delete_task.DeleteTaskState;
import interface_adapter.delete_task.DeleteTaskViewModel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for deleting a task (JavaFX).
 */
public class DeleteTaskView extends VBox implements PropertyChangeListener {

    private final DeleteTaskViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private DeleteTaskController controller;

    private final TextField taskNameField = new TextField();
    private final Button deleteButton = new Button("Delete");
    private final Button cancelButton = new Button("Cancel");

    public DeleteTaskView(DeleteTaskViewModel viewModel,
                          ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("form-container");
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(30, 50, 30, 50));
        this.setSpacing(16);

        Label title = new Label("Delete Task");
        title.getStyleClass().add("form-title");

        taskNameField.getStyleClass().add("form-field");
        taskNameField.setPromptText("Enter task name to delete");

        deleteButton.getStyleClass().add("form-btn-save");
        deleteButton.setStyle("-fx-background-color: #EF4444;");
        cancelButton.getStyleClass().add("form-btn-cancel");

        deleteButton.setOnAction(evt -> handleDelete());
        cancelButton.setOnAction(evt -> {
            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        });

        HBox buttons = new HBox(12, deleteButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        this.getChildren().addAll(title,
                createField("Task Name", taskNameField),
                buttons);
    }

    private VBox createField(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-field-label");
        VBox box = new VBox(4, label, field);
        box.setMaxWidth(400);
        return box;
    }

    private void handleDelete() {
        if (controller == null) {
            showError("Delete Task controller not initialized.");
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String taskName = taskNameField.getText().trim();

        if (taskName.isEmpty()) {
            showError("Task name cannot be empty.");
            return;
        }

        DeleteTaskState state = viewModel.getState();
        state.setUsername(username);
        state.setTaskName(taskName);
        viewModel.setState(state);

        controller.execute(username, taskName);
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
            DeleteTaskState state = viewModel.getState();

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
                state.setSuccessMessage(null);
                viewModel.setState(state);

                viewManagerModel.setState("view tasks and habits");
                viewManagerModel.firePropertyChanged();
            }
        });
    }

    public String getViewName() {
        return viewModel.getViewName();
    }

    public void setDeleteTaskController(DeleteTaskController controller) {
        this.controller = controller;
    }
}
