package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import interface_adapter.ViewManagerModel;
import interface_adapter.modify_task.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.LocalDateTime;

public class ModifyTaskView extends JPanel implements ActionListener, PropertyChangeListener {
    private ViewManagerModel viewManagerModel;
    private final String viewName = "modify task";
    private final ModifyTaskViewModel modifyTaskViewModel;

    private final JTextField newTaskName = new JTextField(10);
    private final JTextField newTaskDeadline = new JTextField(10);
    private final JRadioButton taskCompleted = new JRadioButton("Completed");
    private final JRadioButton taskNotCompleted = new JRadioButton("Not completed");
    private final ButtonGroup newTaskStatus = new ButtonGroup();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();


    private final JTextField newDescription = new JTextField(10);
    private final JTextField newStartTime = new JTextField(10);
    private final JTextField newTaskGroup = new JTextField(10);


    private final JFormattedTextField newTaskPriority = new JFormattedTextField(numberFormat);
    private ModifyTaskController modifyTaskController = null;

    private final JButton save = new JButton("save");
    private final JButton cancel = new JButton("cancel");

    public ModifyTaskView(ModifyTaskViewModel modifyTaskViewModel) {
        ModifyTaskState modifyTaskState = modifyTaskViewModel.getState();

        this.modifyTaskViewModel = modifyTaskViewModel;
        this.modifyTaskViewModel.addPropertyChangeListener(this);

        newTaskName.setText(modifyTaskState.getOldTaskName());
        newTaskDeadline.setText(modifyTaskState.getOldDeadline());

        if (modifyTaskState.getStatus()){
            taskCompleted.setSelected(true);
        } else {
            taskNotCompleted.setSelected(true);
        }

        newTaskPriority.setText(modifyTaskState.getOldPriority());
        newTaskGroup.setText(modifyTaskState.getTaskGroup());
        newStartTime.setText(modifyTaskState.getOldStartTime());
        newDescription.setText(modifyTaskState.getDescription());

        newTaskStatus.add(taskCompleted);
        newTaskStatus.add(taskNotCompleted);


        JLabel taskNameLabel = new JLabel("Task name");
        JLabel taskDeadlineLabel = new JLabel("Task deadline (YYYY-MM-DDTHH:MM:SS)");
        JLabel taskStatusLabel = new JLabel("Task status");
        JLabel taskPriorityLabel = new JLabel("Task priority (Number)");
        JLabel taskDescriptionLabel = new JLabel("Description");
        JLabel taskStartTimeLabel = new JLabel("Start time (YYYY-MM-DDTHH:MM:SS)");
        JLabel taskGroupLabel = new JLabel("Task Group");


        newTaskName.getDocument().addDocumentListener(new DocumentListener() {
            private void documentStateHelper() {
                modifyTaskViewModel.getState().setNewTaskName(newTaskName.getText());
            }
            @Override public void insertUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentStateHelper(); }
        });

        newTaskDeadline.getDocument().addDocumentListener(new DocumentListener() {
            private void documentStateHelper() {
                modifyTaskViewModel.getState().setDeadline(newTaskDeadline.getText());
            }
            @Override public void insertUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentStateHelper(); }
        });

        newTaskPriority.getDocument().addDocumentListener(new DocumentListener() {
            private void documentStateHelper() {
                modifyTaskViewModel.getState().setPriority(newTaskPriority.getText());
            }
            @Override public void insertUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentStateHelper(); }
        });


        newDescription.getDocument().addDocumentListener(new DocumentListener() {
            private void documentStateHelper() {
                modifyTaskViewModel.getState().setDescription(newDescription.getText());
            }
            @Override public void insertUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentStateHelper(); }
        });

        newStartTime.getDocument().addDocumentListener(new DocumentListener() {
            private void documentStateHelper() {
                modifyTaskViewModel.getState().setStartTime(newStartTime.getText());
            }
            @Override public void insertUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentStateHelper(); }
        });

        newTaskGroup.getDocument().addDocumentListener(new DocumentListener() {
            private void documentStateHelper() {
                modifyTaskViewModel.getState().setTaskGroup(newTaskGroup.getText());
            }
            @Override public void insertUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void removeUpdate(DocumentEvent e) { documentStateHelper(); }
            @Override public void changedUpdate(DocumentEvent e) { documentStateHelper(); }
        });


        taskCompleted.addChangeListener(e -> {
            if (taskCompleted.isSelected()) {
                modifyTaskViewModel.getState().setStatus(true);
            }
        });

        taskNotCompleted.addChangeListener(e -> {
            if (taskNotCompleted.isSelected()) {
                modifyTaskViewModel.getState().setStatus(false);
            }
        });


        JPanel taskName = new JPanel();
        JPanel taskDeadline = new JPanel();
        JPanel taskStatus = new JPanel();
        JPanel taskPriority = new JPanel();
        JPanel taskDescription = new JPanel();
        JPanel taskStartTime = new JPanel();
        JPanel taskGroup = new JPanel();

        taskStatus.add(taskStatusLabel);
        taskStatus.add(taskCompleted);
        taskStatus.add(taskNotCompleted);
        taskStatus.setLayout(new BoxLayout(taskStatus, BoxLayout.X_AXIS));

        taskName.add(taskNameLabel);
        taskName.add(newTaskName);
        taskName.setLayout(new BoxLayout(taskName, BoxLayout.X_AXIS));

        taskDeadline.add(taskDeadlineLabel);
        taskDeadline.add(newTaskDeadline);
        taskDeadline.setLayout(new BoxLayout(taskDeadline, BoxLayout.X_AXIS));

        taskPriority.add(taskPriorityLabel);
        taskPriority.add(newTaskPriority);
        taskPriority.setLayout(new BoxLayout(taskPriority, BoxLayout.X_AXIS));

        taskDescription.add(taskDescriptionLabel);
        taskDescription.add(newDescription);
        taskDescription.setLayout(new BoxLayout(taskDescription, BoxLayout.X_AXIS));

        taskStartTime.add(taskStartTimeLabel);
        taskStartTime.add(newStartTime);
        taskStartTime.setLayout(new BoxLayout(taskStartTime, BoxLayout.X_AXIS));

        taskGroup.add(taskGroupLabel);
        taskGroup.add(newTaskGroup);
        taskGroup.setLayout(new BoxLayout(taskGroup, BoxLayout.X_AXIS));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(save);
        buttons.add(cancel);

        this.add(taskName);
        this.add(taskDeadline);
        this.add(taskStartTime);
        this.add(taskDescription);
        this.add(taskGroup);
        this.add(taskStatus);
        this.add(taskPriority);
        this.add(buttons);

        cancel.addActionListener(evt -> {
            if (evt.getSource().equals(cancel)){
                modifyTaskController.switchToTaskListView();
            }
        });

        save.addActionListener(evt -> {
            if (evt.getSource().equals(save)){
                ModifyTaskState currentState = modifyTaskViewModel.getState();



                String oldTaskName = currentState.getOldTaskName();
                String oldPriority = currentState.getOldPriority();
                String oldDeadline = currentState.getOldDeadline();
                boolean oldStatus = currentState.getOldStatus();
                String oldTaskGroup = currentState.getOldTaskGroup();
                String oldDescription = currentState.getOldDescription();
                String oldStartTime = currentState.getOldStartTime();

                modifyTaskController.execute(
                        oldTaskName,
                        oldPriority,
                        oldDeadline,
                        oldStatus,
                        oldTaskGroup,
                        oldDescription,
                        oldStartTime,
                        currentState.getNewTaskName(),
                        currentState.getPriority(),
                        currentState.getDeadline(),
                        currentState.getStatus(),
                        currentState.getTaskGroup(),
                        currentState.getDescription(),
                        currentState.getStartTime()
                );

            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        ModifyTaskState state = (ModifyTaskState) evt.getNewValue();

        newTaskName.setText(state.getNewTaskName());
        newTaskDeadline.setText(state.getDeadline());
        newTaskPriority.setText(state.getPriority());
        taskCompleted.setSelected(state.getStatus());
        taskNotCompleted.setSelected(!state.getStatus());
        newDescription.setText(state.getDescription());
        newStartTime.setText(state.getStartTime());
        newTaskGroup.setText(state.getTaskGroup());
    }

    public String getViewName() {
        return viewName;
    }

    public void setModifyTaskController(ModifyTaskController modifyTaskController) {
        this.modifyTaskController = modifyTaskController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }
}