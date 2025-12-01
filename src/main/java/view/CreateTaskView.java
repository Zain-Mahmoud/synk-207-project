package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.create_task.CreateTaskController;
import interface_adapter.create_task.CreateTaskState;
import interface_adapter.create_task.CreateTaskViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;

/**
 * View for creating a new Task.
 * All business attributes are entered by the user, while the username
 * is taken from the LoggedInViewModel.
 */
public class CreateTaskView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create task";

    private final CreateTaskViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private CreateTaskController createTaskController;

    private final JTextField taskNameField = new JTextField(15);
    private final JTextField descriptionField = new JTextField(15);
    private final JTextField startTimeField = new JTextField(15);
    private final JTextField deadlineField = new JTextField(15);
    private final JTextField taskGroupField = new JTextField(15);
    private final JTextField priorityField = new JTextField(15);

    private final JButton createButton = new JButton("Create");
    private final JButton cancelButton = new JButton("Cancel");

    public CreateTaskView(CreateTaskViewModel viewModel,
                          ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel(CreateTaskViewModel.TITLE_LABEL, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Task name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.TASK_NAME_LABEL + ":"), gbc);
        gbc.gridx = 1;
        formPanel.add(taskNameField, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.DESCRIPTION_LABEL + ":"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);
        row++;

        // Start time
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Start Time (yyyy-MM-ddTHH:mm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(startTimeField, gbc);
        row++;

        // Deadline
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.DEADLINE_LABEL + " (yyyy-MM-ddTHH:mm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(deadlineField, gbc);
        row++;

        // Task group
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.TASK_GROUP_LABEL + ":"), gbc);
        gbc.gridx = 1;
        formPanel.add(taskGroupField, gbc);
        row++;

        // Priority
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.PRIORITY_LABEL + " (integer):"), gbc);
        gbc.gridx = 1;
        formPanel.add(priorityField, gbc);
        row++;

        add(formPanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(createButton);
        buttons.add(cancelButton);
        add(buttons, BorderLayout.SOUTH);

        // Listeners
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreate();
            }
        });

        cancelButton.addActionListener(e -> {
            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        });
    }

    private void handleCreate() {
        if (createTaskController == null) {
            JOptionPane.showMessageDialog(this,
                    "Create Task controller not initialized.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String taskName = taskNameField.getText().trim();
        String description = descriptionField.getText().trim();
        String startTimeText = startTimeField.getText().trim();
        String deadlineText = deadlineField.getText().trim();
        String group = taskGroupField.getText().trim();
        String priorityText = priorityField.getText().trim();

        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task name cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeText);
            LocalDateTime deadline = LocalDateTime.parse(deadlineText);
            int priority = Integer.parseInt(priorityText);

            createTaskController.execute(username, taskName, description,
                    startTime, deadline, group, false, priority);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // unused
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }
        CreateTaskState state = viewModel.getState();
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (state.getSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getSuccessMessage(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            // 清空输入
            taskNameField.setText("");
            descriptionField.setText("");
            startTimeField.setText("");
            deadlineField.setText("");
            taskGroupField.setText("");
            priorityField.setText("");

            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setCreateTaskController(CreateTaskController controller) {
        this.createTaskController = controller;
    }
}
