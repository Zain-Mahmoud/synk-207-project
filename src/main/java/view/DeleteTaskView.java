package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.delete_task.DeleteTaskController;
import interface_adapter.delete_task.DeleteTaskState;
import interface_adapter.delete_task.DeleteTaskViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for deleting a task.
 * User only needs to enter the task name; username is taken from
 * the LoggedInViewModel.
 */
public class DeleteTaskView extends JPanel implements ActionListener, PropertyChangeListener {

    private final DeleteTaskViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private DeleteTaskController controller;

    private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JTextField taskNameField = new JTextField(15);
    private final JButton deleteButton = new JButton("Delete");
    private final JButton cancelButton = new JButton("Cancel");

    public DeleteTaskView(DeleteTaskViewModel viewModel,
                          ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Delete Task", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(new JLabel("Task Name:"), gbc);

        gbc.gridx = 1;
        centerPanel.add(taskNameField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(cancelButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonsPanel, BorderLayout.NORTH);
        southPanel.add(messageLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        this.viewModel.addPropertyChangeListener(this);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });

        cancelButton.addActionListener(e -> {
            clearMessages();
            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        });
    }

    private void handleDelete() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Delete Task controller not initialized.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String taskName = taskNameField.getText().trim();

        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Task name cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DeleteTaskState state = viewModel.getState();
        state.setUsername(username);
        state.setTaskName(taskName);
        viewModel.setState(state);

        controller.execute(username, taskName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // unused
    }

    private void clearMessages() {
        DeleteTaskState state = viewModel.getState();
        state.setErrorMessage(null);
        state.setSuccessMessage(null);
        viewModel.setState(state);
        messageLabel.setText(" ");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }
        DeleteTaskState state = viewModel.getState();

        if (state.getErrorMessage() != null) {

            JOptionPane.showMessageDialog(this, state.getErrorMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            messageLabel.setText(state.getErrorMessage());

            state.setErrorMessage(null);
            viewModel.setState(state);
        } else if (state.getSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getSuccessMessage(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            taskNameField.setText("");

            messageLabel.setText(" ");
            state.setSuccessMessage(null);
            viewModel.setState(state);

            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        }
    }

    public String getViewName() {
        return viewModel.getViewName();
    }

    public void setDeleteTaskController(DeleteTaskController controller) {
        this.controller = controller;
    }
}
