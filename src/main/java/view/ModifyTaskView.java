package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.modify_task.ModifyTaskController;
import interface_adapter.modify_task.ModifyTaskState;
import interface_adapter.modify_task.ModifyTaskViewModel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModifyTaskView extends VBox implements PropertyChangeListener {
    private ViewManagerModel viewManagerModel;
    private final String viewName = "modify task";
    private final ModifyTaskViewModel modifyTaskViewModel;

    private final TextField newTaskName = new TextField();
    private final TextField newTaskDeadline = new TextField();
    private final TextField newStartDateTime = new TextField();
    private final RadioButton taskCompleted = new RadioButton("Completed");
    private final RadioButton taskNotCompleted = new RadioButton("Not completed");
    private final ToggleGroup taskStatusGroup = new ToggleGroup();
    private final TextField newDescription = new TextField();
    private final TextField newTaskGroup = new TextField();
    private final TextField newTaskPriority = new TextField();
    private ModifyTaskController modifyTaskController = null;

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");

    public ModifyTaskView(ModifyTaskViewModel modifyTaskViewModel) {
        this.modifyTaskViewModel = modifyTaskViewModel;
        this.modifyTaskViewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("form-container");
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(30, 50, 30, 50));
        this.setSpacing(12);

        Label title = new Label("Modify Task");
        title.getStyleClass().add("form-title");

        taskCompleted.setToggleGroup(taskStatusGroup);
        taskNotCompleted.setToggleGroup(taskStatusGroup);

        // Style all fields
        for (TextField f : new TextField[]{newTaskName, newTaskDeadline, newStartDateTime,
                newDescription, newTaskGroup, newTaskPriority}) {
            f.getStyleClass().add("form-field");
        }

        // Listeners to sync state
        newTaskName.textProperty().addListener((o, ov, nv) ->
                modifyTaskViewModel.getState().setNewTaskName(nv));
        newTaskDeadline.textProperty().addListener((o, ov, nv) ->
                modifyTaskViewModel.getState().setDeadline(nv));
        newStartDateTime.textProperty().addListener((o, ov, nv) ->
                modifyTaskViewModel.getState().setStartDateTime(nv));
        newTaskPriority.textProperty().addListener((o, ov, nv) ->
                modifyTaskViewModel.getState().setPriority(nv));
        newDescription.textProperty().addListener((o, ov, nv) ->
                modifyTaskViewModel.getState().setDescription(nv));
        newTaskGroup.textProperty().addListener((o, ov, nv) ->
                modifyTaskViewModel.getState().setTaskGroup(nv));

        taskCompleted.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) modifyTaskViewModel.getState().setStatus(true);
        });
        taskNotCompleted.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) modifyTaskViewModel.getState().setStatus(false);
        });

        save.getStyleClass().add("form-btn-save");
        cancel.getStyleClass().add("form-btn-cancel");

        cancel.setOnAction(evt -> modifyTaskController.switchToTaskListView());

        save.setOnAction(evt -> {
            ModifyTaskState currentState = modifyTaskViewModel.getState();
            modifyTaskController.execute(
                    currentState.getOldTaskName(), currentState.getOldPriority(),
                    currentState.getOldDeadline(), currentState.getOldStartDateTime(),
                    currentState.getOldStatus(), currentState.getOldTaskGroup(),
                    currentState.getOldDescription(),
                    currentState.getNewTaskName(), currentState.getPriority(),
                    currentState.getDeadline(), currentState.getStartDateTime(),
                    currentState.getStatus(), currentState.getTaskGroup(),
                    currentState.getDescription()
            );
        });

        HBox statusRow = new HBox(10, new Label("Status:"), taskCompleted, taskNotCompleted);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        HBox buttons = new HBox(12, save, cancel);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(title,
                createField("Task Name", newTaskName),
                createField("Deadline", newTaskDeadline),
                createField("Start Date/Time", newStartDateTime),
                createField("Description", newDescription),
                createField("Task Group", newTaskGroup),
                statusRow,
                createField("Priority", newTaskPriority),
                buttons);
    }

    private VBox createField(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-field-label");
        VBox box = new VBox(4, label, field);
        box.setMaxWidth(400);
        return box;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            ModifyTaskState state = (ModifyTaskState) evt.getNewValue();
            if (state.getTaskError() != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(state.getTaskError());
                alert.showAndWait();
                state.setTaskError(null);
            } else {
                newTaskName.setText(state.getNewTaskName());
                newTaskDeadline.setText(state.getDeadline());
                newStartDateTime.setText(state.getStartDateTime());
                newTaskPriority.setText(state.getPriority());
                taskCompleted.setSelected(state.getStatus());
                taskNotCompleted.setSelected(!state.getStatus());
                newDescription.setText(state.getDescription());
                newTaskGroup.setText(state.getTaskGroup());
            }
        });
    }

    public String getViewName() { return viewName; }

    public void setModifyTaskController(ModifyTaskController controller) {
        this.modifyTaskController = controller;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }
}