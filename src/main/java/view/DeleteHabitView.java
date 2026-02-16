package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.delete_habit.DeleteHabitController;
import interface_adapter.delete_habit.DeleteHabitState;
import interface_adapter.delete_habit.DeleteHabitViewModel;

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
 * View for deleting a habit (JavaFX).
 */
public class DeleteHabitView extends VBox implements PropertyChangeListener {

    private final String viewName = "delete habit";
    private final DeleteHabitViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private DeleteHabitController deleteHabitController;

    private final TextField habitNameField = new TextField();
    private final Button deleteButton = new Button("Delete");
    private final Button cancelButton = new Button("Cancel");

    public DeleteHabitView(DeleteHabitViewModel viewModel,
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

        Label title = new Label("Delete Habit");
        title.getStyleClass().add("form-title");

        habitNameField.getStyleClass().add("form-field");
        habitNameField.setPromptText("Enter habit name to delete");

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
                createField("Habit Name", habitNameField),
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
        if (deleteHabitController == null) {
            showError("Delete Habit controller not initialized.");
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String habitName = habitNameField.getText().trim();

        if (habitName.isEmpty()) {
            showError("Habit name cannot be empty.");
            return;
        }

        DeleteHabitState state = viewModel.getState();
        state.setUsername(username);
        state.setHabitName(habitName);
        viewModel.setState(state);

        deleteHabitController.execute(username, habitName);
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
            DeleteHabitState state = viewModel.getState();
            if (state.getErrorMessage() != null) {
                showError(state.getErrorMessage());
            } else if (state.getSuccessMessage() != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText(state.getSuccessMessage());
                alert.showAndWait();

                habitNameField.setText("");
                viewManagerModel.setState("view tasks and habits");
                viewManagerModel.firePropertyChanged();
            }
        });
    }

    public String getViewName() {
        return viewName;
    }

    public void setDeleteHabitController(DeleteHabitController controller) {
        this.deleteHabitController = controller;
    }
}
