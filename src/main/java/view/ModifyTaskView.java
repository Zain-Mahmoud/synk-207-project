package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import interface_adapter.ViewModel;
import interface_adapter.modify_task.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.LocalDateTime;

public class ModifyTaskView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "modify task";
    private final ModifyTaskViewModel modifyTaskViewModel;

    private final JTextField newTaskName = new JTextField();
    private final JTextField newTaskDeadline = new JTextField();
    private final JRadioButton taskCompleted = new JRadioButton("Completed");
    private final JRadioButton taskNotCompleted = new JRadioButton("Not completed");
    private final ButtonGroup newTaskStatus = new ButtonGroup();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private final JFormattedTextField newTaskPriority = new JFormattedTextField(numberFormat);
    private ModifyTaskController modifyTaskController = null;

    private final JButton save = new JButton("save");
    private final JButton cancel = new JButton("cancel");

    public ModifyTaskView(ModifyTaskViewModel modifyTaskViewModel) {
        this.modifyTaskViewModel = modifyTaskViewModel;
        this.modifyTaskViewModel.addPropertyChangeListener(this);

        newTaskStatus.add(taskCompleted);
        newTaskStatus.add(taskNotCompleted);

        JLabel taskNameLabel = new JLabel("Task name");
        JLabel taskDeadlineLabel = new JLabel("Task deadline");
        JLabel taskStatusLabel = new JLabel("Task status");
        JLabel taskPriorityLabel = new JLabel("Task priority");

        newTaskName.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyTaskState currentState = modifyTaskViewModel.getState();
                currentState.setNewTaskName(newTaskName.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });
        newTaskDeadline.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyTaskState currentState = modifyTaskViewModel.getState();
                currentState.setDeadline(newTaskDeadline.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });

        newTaskPriority.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyTaskState currentState = modifyTaskViewModel.getState();
                currentState.setPriority(Integer.parseInt(newTaskPriority.getText()));
            }
            public void documentStateHelper(int num) {
                final ModifyTaskState currentState = modifyTaskViewModel.getState();
                currentState.setPriority(-1);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper(-1);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try{
                    Integer.parseInt(newTaskPriority.getText());
                    documentStateHelper();
                } catch (NumberFormatException exc){
                    documentStateHelper(-1);
                }
            }
        });

        taskCompleted.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final ModifyTaskState currentState = modifyTaskViewModel.getState();
                currentState.setStatus(true);
            }
        });

        taskNotCompleted.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final ModifyTaskState currentState = modifyTaskViewModel.getState();
                currentState.setStatus(false);
            }
        });



        JPanel taskStatus = new JPanel();
        JPanel taskPriority = new JPanel();
        JPanel taskName = new JPanel();
        JPanel taskDeadline = new JPanel();

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

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(save);
        buttons.add(cancel);

        this.add(taskName);
        this.add(taskDeadline);
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
                modifyTaskController.execute(currentState.getNewTaskName(), currentState.getPriority(),
                        LocalDateTime.now(), currentState.getStatus());
                modifyTaskController.switchToTaskListView();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public String getViewName() {
        return viewName;
    }


    public void setModifyTaskController(ModifyTaskController modifyTaskController) {
        this.modifyTaskController = modifyTaskController;
    }



}
