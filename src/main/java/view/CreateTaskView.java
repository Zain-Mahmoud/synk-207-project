package view;

import interface_adapter.create_task.CreateTaskController;
import interface_adapter.create_task.CreateTaskState;
import interface_adapter.create_task.CreateTaskViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreateTaskView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create task";

    private final CreateTaskViewModel createTaskViewModel;

    private final JTextField usernameInputField = new JTextField(15);
    private final JTextField taskNameInputField = new JTextField(15);
    private final JTextField descriptionInputField = new JTextField(15);
    private final JTextField deadlineInputField = new JTextField(15);
    private final JTextField taskGroupInputField = new JTextField(15);
    private final JTextField priorityInputField = new JTextField(15);

    private CreateTaskController createTaskController = null;

    private final JButton createButton;
    private final JButton cancelButton;

    public CreateTaskView(CreateTaskViewModel createTaskViewModel) {
        this.createTaskViewModel = createTaskViewModel;
        createTaskViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(CreateTaskViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelTextPanel usernamePanel = new LabelTextPanel(
                new JLabel(CreateTaskViewModel.USERNAME_LABEL), usernameInputField);

        LabelTextPanel taskNamePanel = new LabelTextPanel(
                new JLabel(CreateTaskViewModel.TASK_NAME_LABEL), taskNameInputField);

        LabelTextPanel descriptionPanel = new LabelTextPanel(
                new JLabel(CreateTaskViewModel.DESCRIPTION_LABEL), descriptionInputField);

        LabelTextPanel deadlinePanel = new LabelTextPanel(
                new JLabel(CreateTaskViewModel.DEADLINE_LABEL), deadlineInputField);

        LabelTextPanel taskGroupPanel = new LabelTextPanel(
                new JLabel(CreateTaskViewModel.TASK_GROUP_LABEL), taskGroupInputField);

        LabelTextPanel priorityPanel = new LabelTextPanel(
                new JLabel(CreateTaskViewModel.PRIORITY_LABEL), priorityInputField);

        createButton = new JButton(CreateTaskViewModel.CREATE_BUTTON_LABEL);
        cancelButton = new JButton(CreateTaskViewModel.CANCEL_BUTTON_LABEL);

        JPanel buttons = new JPanel();
        buttons.add(createButton);
        buttons.add(cancelButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateTaskState state = createTaskViewModel.getState();

                LocalDateTime deadline = null;
                try {
                    if (state.getDeadline() != null) {
                        deadline = state.getDeadline();
                    } else if (!deadlineInputField.getText().trim().isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        deadline = LocalDateTime.parse(deadlineInputField.getText().trim(), formatter);
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(CreateTaskView.this,
                            "Invalid deadline format. Use: yyyy-MM-dd HH:mm");
                    return;
                }

                int priority = 0;
                try {
                    priority = Integer.parseInt(priorityInputField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CreateTaskView.this,
                            "Priority must be a number");
                    return;
                }

                createTaskController.execute(
                        state.getUsername(),
                        state.getTaskName(),
                        state.getDescription(),
                        deadline,
                        state.getTaskGroup(),
                        false,
                        priority
                );
            }
        });

        cancelButton.addActionListener(this);

        addUsernameListener();
        addTaskNameListener();
        addDescriptionListener();
        addDeadlineListener();
        addTaskGroupListener();
        addPriorityListener();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(usernamePanel);
        this.add(taskNamePanel);
        this.add(descriptionPanel);
        this.add(deadlinePanel);
        this.add(taskGroupPanel);
        this.add(priorityPanel);
        this.add(buttons);
    }

    private void addUsernameListener() {
        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateTaskState state = createTaskViewModel.getState();
                state.setUsername(usernameInputField.getText());
                createTaskViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void addTaskNameListener() {
        taskNameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateTaskState state = createTaskViewModel.getState();
                state.setTaskName(taskNameInputField.getText());
                createTaskViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void addDescriptionListener() {
        descriptionInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateTaskState state = createTaskViewModel.getState();
                state.setDescription(descriptionInputField.getText());
                createTaskViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void addDeadlineListener() {
        deadlineInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateTaskState state = createTaskViewModel.getState();
                try {
                    if (!deadlineInputField.getText().trim().isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime deadline = LocalDateTime.parse(deadlineInputField.getText().trim(), formatter);
                        state.setDeadline(deadline);
                    } else {
                        state.setDeadline(null);
                    }
                } catch (DateTimeParseException e) {
                    // Invalid format, don't update state
                }
                createTaskViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void addTaskGroupListener() {
        taskGroupInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateTaskState state = createTaskViewModel.getState();
                state.setTaskGroup(taskGroupInputField.getText());
                createTaskViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void addPriorityListener() {
        priorityInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateTaskState state = createTaskViewModel.getState();
                try {
                    if (!priorityInputField.getText().trim().isEmpty()) {
                        int priority = Integer.parseInt(priorityInputField.getText().trim());
                        state.setPriority(priority);
                    } else {
                        state.setPriority(0);
                    }
                } catch (NumberFormatException e) {
                    // Invalid number, don't update state
                }
                createTaskViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Cancel not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CreateTaskState state = (CreateTaskState) evt.getNewValue();

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        } else if (state.getSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getSuccessMessage());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setCreateTaskController(CreateTaskController controller) {
        this.createTaskController = controller;
    }
}