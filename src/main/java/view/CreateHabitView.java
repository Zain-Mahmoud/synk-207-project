package view;

import interface_adapter.ViewModel;
import interface_adapter.create_habit.CreateHabitController;
import interface_adapter.create_habit.CreateHabitState;
import interface_adapter.create_habit.CreateHabitViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Swing View for Create Habit.
 * Frequency is input as a LocalDateTime string, no longer using count/unit.
 */
public class CreateHabitView extends JPanel implements ActionListener, PropertyChangeListener {

    private final CreateHabitController controller;
    private final CreateHabitViewModel viewModel;

    // Save current username directly, passed externally during construction (can be
    // changed to read from LoggedInViewModel)
    private final String username;

    // UI Components
    private final JTextField habitNameField = new JTextField(20);
    private final JTextField startDateTimeField = new JTextField(20);
    private final JTextField frequencyField = new JTextField(20);
    private final JTextField habitGroupField = new JTextField(20);
    private final JTextField streakCountField = new JTextField(5);
    private final JTextField priorityField = new JTextField(5);

    private final JLabel messageLabel = new JLabel(" ");

    private final JButton createButton = new JButton();
    private final JButton cancelButton = new JButton();

    public CreateHabitView(CreateHabitController controller,
            CreateHabitViewModel viewModel,
            String username) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.username = username;

        this.viewModel.addPropertyChangeListener(this);

        // Initialize UI
        loadLabelsFromViewModel();
        setupLayout();
        addListeners();
    }

    private void loadLabelsFromViewModel() {
        // Title can be used externally or not
        createButton.setText(CreateHabitViewModel.CREATE_BUTTON_LABEL);
        cancelButton.setText(CreateHabitViewModel.CANCEL_BUTTON_LABEL);
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Habit Name
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.HABIT_NAME_LABEL + ":"), gbc);

        gbc.gridx = 1;
        habitNameField.setToolTipText("e.g., Exercise, Reading...");
        formPanel.add(habitNameField, gbc);
        row++;

        // Start Date & Time
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.START_DATETIME_LABEL + ":"), gbc);

        gbc.gridx = 1;
        startDateTimeField.setToolTipText("Format: yyyy-MM-dd'T'HH:mm, e.g., 2025-11-23T09:00");
        formPanel.add(startDateTimeField, gbc);
        row++;

        // Frequency Date & Time
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.FREQUENCY_LABEL + ":"), gbc);

        gbc.gridx = 1;
        frequencyField.setToolTipText("Next reminder time, same format as above");
        formPanel.add(frequencyField, gbc);
        row++;

        // Habit Group
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.HABIT_GROUP_LABEL + ":"), gbc);

        gbc.gridx = 1;
        habitGroupField.setToolTipText("e.g., Health, Study, Work...");
        formPanel.add(habitGroupField, gbc);
        row++;

        // Streak Count
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.STREAK_COUNT_LABEL + ":"), gbc);

        gbc.gridx = 1;
        formPanel.add(streakCountField, gbc);
        row++;

        // Priority
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.PRIORITY_LABEL + ":"), gbc);

        gbc.gridx = 1;
        formPanel.add(priorityField, gbc);
        row++;

        // Message label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel, gbc);
        row++;

        this.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        createButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == createButton) {
            onCreate();
        } else if (source == cancelButton) {
            onCancel();
        }
    }

    private void onCreate() {
        // Get data from view
        String habitName = habitNameField.getText().trim();
        String startDateTimeText = startDateTimeField.getText().trim();
        String frequencyDateTimeText = frequencyField.getText().trim();
        String habitGroup = habitGroupField.getText().trim();
        String streakText = streakCountField.getText().trim();
        String priorityText = priorityField.getText().trim();

        int streakCount;
        int priority;

        try {
            streakCount = streakText.isEmpty() ? 0 : Integer.parseInt(streakText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Streak Count must be integer.",
                    "error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            priority = priorityText.isEmpty() ? 0 : Integer.parseInt(priorityText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Priority must be integer.",
                    "error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Call controller (frequency passed as LocalDateTime string)
            controller.execute(
                    username,
                    habitName,
                    startDateTimeText,
                    frequencyDateTimeText,
                    habitGroup,
                    streakCount,
                    priority);
        } catch (IllegalArgumentException ex) {
            // e.g., thrown by controller when date format is incorrect
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "invalid input.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        // If you have ViewManager, you can switch back to LoggedIn or other views here.
        // Clear form simply here.
        habitNameField.setText("");
        startDateTimeField.setText("");
        frequencyField.setText("");
        habitGroupField.setText("");
        streakCountField.setText("");
        priorityField.setText("");
        messageLabel.setText(" ");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        CreateHabitState state = (CreateHabitState) evt.getNewValue();

        habitNameField.setText(state.getHabitName());
        startDateTimeField.setText(state.getStartDateTimeText());
        frequencyField.setText(state.getFrequencyText());
        habitGroupField.setText(state.getHabitGroup());
        streakCountField.setText(
                state.getStreakCount() == 0 ? "" : String.valueOf(state.getStreakCount()));
        priorityField.setText(
                state.getPriority() == 0 ? "" : String.valueOf(state.getPriority()));

        if (state.getErrorMessage() != null) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(state.getErrorMessage());
        } else if (state.getSuccessMessage() != null) {
            messageLabel.setForeground(new Color(0, 128, 0)); // Dark green
            messageLabel.setText(state.getSuccessMessage());
            // Clear text fields on success
            habitNameField.setText("");
            startDateTimeField.setText("");
            frequencyField.setText("");
            habitGroupField.setText("");
            streakCountField.setText("");
            priorityField.setText("");
        } else {
            messageLabel.setText(" ");
        }
    }

    public String getViewName() {
        return viewModel.getViewName();
    }
}
