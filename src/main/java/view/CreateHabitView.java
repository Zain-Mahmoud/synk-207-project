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
 * Create Habit 的 Swing View。
 * 频率 frequency 以一个 LocalDateTime 字符串输入，不再使用 count/unit。
 */
public class CreateHabitView extends JPanel implements ActionListener, PropertyChangeListener {

    private final CreateHabitController controller;
    private final CreateHabitViewModel viewModel;

    // 这里直接保存当前用户名，由外部在构造时传入（你也可以改成从 LoggedInViewModel 读）
    private final String username;

    // UI 组件
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

        // 初始化 UI
        loadLabelsFromViewModel();
        setupLayout();
        addListeners();
    }

    private void loadLabelsFromViewModel() {
        // 标题可以在外面用，也可以不用
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
        habitNameField.setToolTipText("例如：Exercise, Reading...");
        formPanel.add(habitNameField, gbc);
        row++;

        // Start Date & Time
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.START_DATETIME_LABEL + ":"), gbc);

        gbc.gridx = 1;
        startDateTimeField.setToolTipText("格式: yyyy-MM-dd'T'HH:mm，例如 2025-11-23T09:00");
        formPanel.add(startDateTimeField, gbc);
        row++;

        // Frequency Date & Time
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.FREQUENCY_LABEL + ":"), gbc);

        gbc.gridx = 1;
        frequencyField.setToolTipText("下一次提醒时间，格式同上");
        formPanel.add(frequencyField, gbc);
        row++;

        // Habit Group
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel(CreateHabitViewModel.HABIT_GROUP_LABEL + ":"), gbc);

        gbc.gridx = 1;
        habitGroupField.setToolTipText("例如：Health, Study, Work...");
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
        // 从 view 中拿数据
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
            // 调用 controller（frequency 作为 LocalDateTime 字符串传入）
            controller.execute(
                    username,
                    habitName,
                    startDateTimeText,
                    frequencyDateTimeText,
                    habitGroup,
                    streakCount,
                    priority
            );
        } catch (IllegalArgumentException ex) {
            // 比如日期格式错误时 controller 抛出的
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "invalid input.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        // 如果你有 ViewManager，可以在这里切换回 LoggedIn 或别的 view。
        // 这里先简单清空表单。
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
                state.getStreakCount() == 0 ? "" : String.valueOf(state.getStreakCount())
        );
        priorityField.setText(
                state.getPriority() == 0 ? "" : String.valueOf(state.getPriority())
        );

        if (state.getErrorMessage() != null) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(state.getErrorMessage());
        } else if (state.getSuccessMessage() != null) {
            messageLabel.setForeground(new Color(0, 128, 0)); // 深绿色
            messageLabel.setText(state.getSuccessMessage());
            // 创建成功时也可以清空文本框
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
