package view;

import interface_adapter.delete_task.DeleteTaskController;
import interface_adapter.delete_task.DeleteTaskState;
import interface_adapter.delete_task.DeleteTaskViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * Interface for DeleteTask:
 * - Displays delete confirmation message (with username + taskName)
 * - Provides Delete / Cancel buttons
 * - Listens to ViewModel state changes, updates success / failure messages
 */
public class DeleteTaskView extends JPanel {

    private final DeleteTaskController controller;
    private final DeleteTaskViewModel viewModel;

    // Components
    private final JLabel titleLabel;
    private final JLabel confirmLabel;
    private final JLabel successLabel;
    private final JLabel errorLabel;
    private final JButton deleteButton;
    private final JButton cancelButton;

    public DeleteTaskView(DeleteTaskController controller,
            DeleteTaskViewModel viewModel,
            String username,
            String taskName) {

        this.controller = controller;
        this.viewModel = viewModel;

        // Write username / taskName to state first
        DeleteTaskState state = viewModel.getState();
        state.setUsername(username);
        state.setTaskName(taskName);
        state.setSuccessMessage(null);
        state.setErrorMessage(null);
        viewModel.setState(state);

        // Initialize components
        titleLabel = new JLabel(DeleteTaskViewModel.TITLE_LABEL, SwingConstants.CENTER);
        confirmLabel = new JLabel("", SwingConstants.CENTER);
        successLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel = new JLabel("", SwingConstants.CENTER);

        deleteButton = new JButton(DeleteTaskViewModel.DELETE_BUTTON_LABEL);
        cancelButton = new JButton(DeleteTaskViewModel.CANCEL_BUTTON_LABEL);

        // Simple style settings (adjust as needed)
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        successLabel.setForeground(new Color(0, 128, 0)); // Green
        errorLabel.setForeground(Color.RED);

        setLayout(new BorderLayout(10, 10));

        // Top title
        add(titleLabel, BorderLayout.NORTH);

        // Middle: Confirmation text + message
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1, 5, 5));
        centerPanel.add(confirmLabel);
        centerPanel.add(successLabel);
        centerPanel.add(errorLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom button row
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize interface text once
        updateFromState(viewModel.getState());

        // Bind button events
        addListeners();

        // Listen for ViewModel changes (e.g., Presenter updated state)
        viewModel.addPropertyChangeListener(evt -> {
            // Update UI from ViewModel state on every change
            DeleteTaskState newState = viewModel.getState();
            updateFromState(newState);
        });
    }

    /**
     * Update interface display based on current state
     */
    private void updateFromState(DeleteTaskState state) {
        String username = state.getUsername();
        String taskName = state.getTaskName();

        // Basic confirmation text follows ViewModel constants
        // CONFIRM_MESSAGE = "Are you sure you want to delete task '%s'?"
        String baseConfirm = String.format(DeleteTaskViewModel.CONFIRM_MESSAGE,
                taskName == null ? "" : taskName);

        // If username exists, append a sentence
        if (username != null && !username.isEmpty()) {
            confirmLabel.setText(baseConfirm + " for user '" + username + "'?");
        } else {
            confirmLabel.setText(baseConfirm);
        }

        // Success / Failure message
        successLabel.setText(state.getSuccessMessage() == null ? "" : state.getSuccessMessage());
        errorLabel.setText(state.getErrorMessage() == null ? "" : state.getErrorMessage());
    }

    /**
     * Bind button events
     */
    private void addListeners() {
        // Delete: Call controller.execute(username, taskName)
        deleteButton.addActionListener(e -> {
            DeleteTaskState state = viewModel.getState();
            String username = state.getUsername();
            String taskName = state.getTaskName();
            controller.execute(username, taskName);
        });

        // Cancel: Simple handling (clear message), navigation delegated to upper
        // ViewManager
        cancelButton.addActionListener(e -> {
            DeleteTaskState state = viewModel.getState();
            state.setSuccessMessage(null);
            state.setErrorMessage(null);
            viewModel.setState(state);
            viewModel.firePropertyChanged();
        });
    }
}
