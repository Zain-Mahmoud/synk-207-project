package view;

import interface_adapter.create_habit.CreateHabitController;
import interface_adapter.create_habit.CreateHabitState;
import interface_adapter.create_habit.CreateHabitViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateHabitView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create habit";

    private final CreateHabitViewModel createHabitViewModel;
    private CreateHabitController createHabitController;

    private String username;   // 外部设置：当前登录用户

    private final JTextField habitNameField = new JTextField(15);
    private final JTextField habitGroupField = new JTextField(15);

    private final JSpinner startDateSpinner;

    private final JSpinner frequencyCountSpinner =
            new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JComboBox<String> frequencyUnitCombo =
            new JComboBox<>(new String[]{"Per Day", "Per Week", "Per Month", "Per Year"});

    private final JSpinner streakSpinner =
            new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JSpinner prioritySpinner =
            new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));



    private final JButton createButton;
    private final JButton cancelButton;

    private final SimpleDateFormat dateTimeFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public CreateHabitView(CreateHabitViewModel createHabitViewModel) {
        this.createHabitViewModel = createHabitViewModel;
        createHabitViewModel.addPropertyChangeListener(this);

        setPreferredSize(new Dimension(520, 360));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel(CreateHabitViewModel.TITLE_LABEL, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(title, gbc);

        // 1. Habit Name
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel(CreateHabitViewModel.HABIT_NAME_LABEL + ":"), gbc);

        gbc.gridx = 1;
        add(habitNameField, gbc);

        // 2. Start Date & Time (Spinner)
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel(CreateHabitViewModel.START_DATETIME_LABEL + ":"), gbc);

        SpinnerDateModel startModel =
                new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        startDateSpinner = new JSpinner(startModel);
        startDateSpinner.setEditor(
                new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd HH:mm"));

        gbc.gridx = 1;
        add(startDateSpinner, gbc);

        // 3. Frequency: 次数
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Frequency (times):"), gbc);

        gbc.gridx = 1;
        add(frequencyCountSpinner, gbc);

        // 4. Frequency Unit: Per Day / Per Week
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Frequency Unit:"), gbc);

        gbc.gridx = 1;
        add(frequencyUnitCombo, gbc);

        // 5. Habit Group
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel(CreateHabitViewModel.HABIT_GROUP_LABEL + ":"), gbc);

        gbc.gridx = 1;
        add(habitGroupField, gbc);

        // 6. Initial Streak Count
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel(CreateHabitViewModel.STREAK_COUNT_LABEL + ":"), gbc);

        gbc.gridx = 1;
        add(streakSpinner, gbc);

        // 7. Priority
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel(CreateHabitViewModel.PRIORITY_LABEL + ":"), gbc);

        gbc.gridx = 1;
        add(prioritySpinner, gbc);


        // 9. Buttons
        JPanel buttons = new JPanel();
        createButton = new JButton(CreateHabitViewModel.CREATE_BUTTON_LABEL);
        cancelButton = new JButton(CreateHabitViewModel.CANCEL_BUTTON_LABEL);
        buttons.add(createButton);
        buttons.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttons, gbc);

        // ==== 监听 & 状态同步 ====

        createButton.addActionListener(e -> onCreateClicked());
        cancelButton.addActionListener(this);

        addHabitNameListener();
        addHabitGroupListener();

        // startDate 写入 startDateTimeText
        startDateSpinner.addChangeListener(e -> {
            Date date = (Date) startDateSpinner.getValue();
            String text = dateTimeFormat.format(date);
            CreateHabitState state = createHabitViewModel.getState();
            state.setStartDateTimeText(text);
            createHabitViewModel.setState(state);
        });

        frequencyCountSpinner.addChangeListener(e -> {
            CreateHabitState state = createHabitViewModel.getState();
            state.setFrequencyCount((Integer) frequencyCountSpinner.getValue());
            createHabitViewModel.setState(state);
        });

        frequencyUnitCombo.addActionListener(e -> {
            CreateHabitState state = createHabitViewModel.getState();
            state.setFrequencyUnit((String) frequencyUnitCombo.getSelectedItem());
            createHabitViewModel.setState(state);
        });

        streakSpinner.addChangeListener(e -> {
            CreateHabitState state = createHabitViewModel.getState();
            state.setStreakCount((Integer) streakSpinner.getValue());
            createHabitViewModel.setState(state);
        });

        prioritySpinner.addChangeListener(e -> {
            CreateHabitState state = createHabitViewModel.getState();
            state.setPriority((Integer) prioritySpinner.getValue());
            createHabitViewModel.setState(state);
        });


        // 初始化一次 startDateTimeText，避免 null
        initStartDateString();
    }

    private void initStartDateString() {
        Date start = (Date) startDateSpinner.getValue();
        CreateHabitState state = createHabitViewModel.getState();
        state.setStartDateTimeText(dateTimeFormat.format(start));
        createHabitViewModel.setState(state);
    }

    private void addHabitNameListener() {
        habitNameField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateHabitState state = createHabitViewModel.getState();
                state.setHabitName(habitNameField.getText());
                createHabitViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void addHabitGroupListener() {
        habitGroupField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                CreateHabitState state = createHabitViewModel.getState();
                state.setHabitGroup(habitGroupField.getText());
                createHabitViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void onCreateClicked() {
        if (createHabitController == null) {
            JOptionPane.showMessageDialog(this, "CreateHabitController is not set.");
            return;
        }
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No logged-in user. username not set.");
            return;
        }

        CreateHabitState state = createHabitViewModel.getState();

        try {
            // ⭐ 这里示例：给 Controller 多传两个 frequency 参数
            createHabitController.excute(
                    username,
                    state.getHabitName(),
                    state.getStartDateTimeText(),
                    state.getFrequencyCount(),
                    state.getFrequencyUnit(),
                    state.getHabitGroup(),
                    state.getStreakCount(),
                    state.getPriority()
            );

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Cancel not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CreateHabitState state = (CreateHabitState) evt.getNewValue();

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        } else if (state.getSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getSuccessMessage());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setCreateHabitController(CreateHabitController controller) {
        this.createHabitController = controller;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
