package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.modify_habit.ModifyHabitController;
import interface_adapter.modify_habit.ModifyHabitState;
import interface_adapter.modify_habit.ModifyHabitViewModel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModifyHabitView extends VBox implements PropertyChangeListener {
    private final String viewName = "modify habit";
    private final ModifyHabitViewModel modifyHabitViewModel;

    private final TextField newHabitName = new TextField();
    private final TextField newStartDateTime = new TextField();
    private final TextField newFrequency = new TextField();
    private final TextField newHabitGroup = new TextField();
    private final RadioButton habitCompleted = new RadioButton("Completed");
    private final RadioButton habitNotCompleted = new RadioButton("Not completed");
    private final ToggleGroup habitStatusGroup = new ToggleGroup();
    private final TextField newHabitPriority = new TextField();
    private final TextField newStreakCount = new TextField();

    private ModifyHabitController modifyHabitController = null;
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private ViewManagerModel viewManagerModel;

    public ModifyHabitView(ModifyHabitViewModel modifyHabitViewModel) {
        this.modifyHabitViewModel = modifyHabitViewModel;
        this.modifyHabitViewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("form-container");
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(30, 50, 30, 50));
        this.setSpacing(12);

        Label title = new Label("Modify Habit");
        title.getStyleClass().add("form-title");

        habitCompleted.setToggleGroup(habitStatusGroup);
        habitNotCompleted.setToggleGroup(habitStatusGroup);

        for (TextField f : new TextField[]{newHabitName, newStartDateTime, newFrequency,
                newHabitGroup, newHabitPriority, newStreakCount}) {
            f.getStyleClass().add("form-field");
        }

        // Listeners
        newHabitName.textProperty().addListener((o, ov, nv) ->
                modifyHabitViewModel.getState().setHabitName(nv));
        newStartDateTime.textProperty().addListener((o, ov, nv) ->
                modifyHabitViewModel.getState().setStartDateTime(nv));
        newFrequency.textProperty().addListener((o, ov, nv) ->
                modifyHabitViewModel.getState().setFrequency(nv));
        newHabitGroup.textProperty().addListener((o, ov, nv) ->
                modifyHabitViewModel.getState().setHabitGroup(nv));
        newHabitPriority.textProperty().addListener((o, ov, nv) ->
                modifyHabitViewModel.getState().setPriority(nv));
        newStreakCount.textProperty().addListener((o, ov, nv) ->
                modifyHabitViewModel.getState().setStreakCount(nv));

        habitCompleted.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) modifyHabitViewModel.getState().setStatus(true);
        });
        habitNotCompleted.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) modifyHabitViewModel.getState().setStatus(false);
        });

        save.getStyleClass().add("form-btn-save");
        cancel.getStyleClass().add("form-btn-cancel");

        cancel.setOnAction(evt -> modifyHabitController.switchToHabitListView());

        save.setOnAction(evt -> {
            ModifyHabitState currentState = modifyHabitViewModel.getState();
            modifyHabitController.execute(
                    currentState.getOldHabitName(), currentState.getOldPriority(),
                    currentState.getOldStatus(), currentState.getOldStartDateTime(),
                    currentState.getOldStreakCount(), currentState.getOldHabitGroup(),
                    currentState.getOldFrequency(),
                    currentState.getHabitName(), currentState.getPriority(),
                    currentState.getStatus(), currentState.getStartDateTime(),
                    currentState.getStreakCount(), currentState.getHabitGroup(),
                    currentState.getFrequency()
            );
        });

        HBox statusRow = new HBox(10, new Label("Status:"), habitCompleted, habitNotCompleted);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        HBox buttons = new HBox(12, save, cancel);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(title,
                createField("Habit Name", newHabitName),
                createField("Start Date/Time", newStartDateTime),
                createField("Frequency", newFrequency),
                createField("Habit Group", newHabitGroup),
                statusRow,
                createField("Priority", newHabitPriority),
                createField("Streak Count", newStreakCount),
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
            final ModifyHabitState currState = (ModifyHabitState) evt.getNewValue();
            if (currState.getHabitError() != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(currState.getHabitError());
                alert.showAndWait();
            } else {
                newHabitName.setText(currState.getOldHabitName());
                newFrequency.setText(currState.getOldFrequency());
                newHabitPriority.setText(currState.getOldPriority());
                newStreakCount.setText(currState.getOldStreakCount());
                newHabitGroup.setText(currState.getOldHabitGroup());
                newStartDateTime.setText(currState.getOldStartDateTime());
                habitCompleted.setSelected(currState.getOldStatus());
                habitNotCompleted.setSelected(!currState.getOldStatus());
            }
        });
    }

    public String getViewName() { return viewName; }

    public void setModifyHabitController(ModifyHabitController controller) {
        this.modifyHabitController = controller;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }
}
