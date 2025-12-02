package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.modify_habit.ModifyHabitState;
import interface_adapter.modify_habit.ModifyHabitViewModel;
import interface_adapter.modify_task.ModifyTaskState;
import interface_adapter.modify_task.ModifyTaskViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ViewTasksAndHabitsView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final Color PRIMARY_COLOR = new Color(255, 161, 108);
    private static final Color BACKGROUND_COLOR = new Color(248, 244, 242);
    private static final Color CARD_BORDER_COLOR = new Color(232, 222, 218);
    private static final Color MUTED_TEXT_COLOR = new Color(150, 131, 120);
    private static final Color TABLE_GRID_COLOR = new Color(230, 233, 240);
    private static final Color TABLE_ALT_ROW_COLOR = new Color(252, 251, 250);
    private static final Color TABLE_SELECTION_COLOR = new Color(252, 240, 232);

    private final ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;
    private final String viewName = "view tasks and habits";
    private ViewManagerModel viewManagerModel;
    private LoggedInViewModel loggedInViewModel;
    private ModifyHabitViewModel modifyHabitViewModel;
    private ModifyTaskViewModel modifyTaskViewModel;

    private DefaultTableModel taskModel;
    private DefaultTableModel habitModel;
    private JTable taskTable;
    private JTable habitTable;

    private final JButton refreshButton;
    private final JButton exitButton;
    private final JButton createTaskButton;
    private final JButton deleteTaskButton;
    private final JButton createHabitButton;
    private final JButton deleteHabitButton;

    class ButtonHandler extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
        private final JButton button;
        private int clickedRow;
        private String designation;

        public ButtonHandler(String buttonText, String designation) {
            this.designation = designation;
            this.button = new JButton(buttonText);
            this.button.setOpaque(true);
            this.button.setBackground(PRIMARY_COLOR);
            this.button.setForeground(Color.WHITE);
            this.button.setFocusPainted(false);
            this.button.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            this.button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.button.addActionListener(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof String) {
                button.setText((String) value);
            }

            if (isSelected) {
                button.setBackground(PRIMARY_COLOR.darker());
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
            }
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.clickedRow = row;
            if (value instanceof String) {
                button.setText((String) value);
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (designation.equals("task")) {
                return taskModel.getValueAt(clickedRow, taskTable.getColumnCount() - 1);
            } else {
                return habitModel.getValueAt(clickedRow, habitTable.getColumnCount() - 1);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();

            if (designation.equals("task")) {

                String taskName = taskModel.getValueAt(clickedRow, 0).toString();
                String taskStartTime = taskModel.getValueAt(clickedRow, 1).toString();
                String taskDueDateTime = taskModel.getValueAt(clickedRow, 2).toString();
                String taskGroup = taskModel.getValueAt(clickedRow, 3).toString();
                String taskStatusText = taskModel.getValueAt(clickedRow, 4).toString();
                String taskPriority = taskModel.getValueAt(clickedRow, 5).toString();
                String taskDescription = taskModel.getValueAt(clickedRow, 6).toString();

                boolean taskStatus;
                if (taskStatusText.equals("Complete")){
                    taskStatus = true;
                } else{
                    taskStatus = false;
                }

                ModifyTaskState taskState = modifyTaskViewModel.getState();

                taskState.setOldTaskName(taskName);
                taskState.setOldStartDateTime(taskStartTime);
                taskState.setOldDeadline(taskDueDateTime);
                taskState.setOldPriority(taskPriority);
                taskState.setOldStatus(taskStatus);
                taskState.setOldTaskGroup(taskGroup);
                taskState.setOldDescription(taskDescription);

                taskState.setNewTaskName(taskName);
                taskState.setStartDateTime(taskStartTime);
                taskState.setDeadline(taskDueDateTime);
                taskState.setPriority(taskPriority);
                taskState.setStatus(taskStatus);
                taskState.setTaskGroup(taskGroup);
                taskState.setDescription(taskDescription);

                modifyTaskViewModel.firePropertyChanged();
                viewManagerModel.setState(modifyTaskViewModel.getViewName());
                viewManagerModel.firePropertyChanged();

            } else if (designation.equals("habit")) {
                String habitName = habitModel.getValueAt(clickedRow, 0).toString();
                String startTime = habitModel.getValueAt(clickedRow, 1).toString();
                String habitFrequency = habitModel.getValueAt(clickedRow, 2).toString();
                String habitGroup = habitModel.getValueAt(clickedRow, 3).toString();
                String habitStreakCount = habitModel.getValueAt(clickedRow, 4).toString();
                String habitPriority = habitModel.getValueAt(clickedRow, 5).toString();
                String habitStatusText = habitModel.getValueAt(clickedRow,6).toString();
                boolean habitStatus;
                if (habitStatusText.equals("Complete")){
                    habitStatus = true;
                } else{
                    habitStatus = false;
                }
                ModifyHabitState habitState = modifyHabitViewModel.getState();
                habitState.setOldHabitName(habitName);
                habitState.setOldHabitGroup(habitGroup);
                habitState.setOldFrequency(habitFrequency);
                habitState.setOldStatus(habitStatus);
                habitState.setOldPriority(habitPriority);
                habitState.setOldStreakCount(habitStreakCount);
                habitState.setOldStartDateTime(startTime);

                habitState.setHabitName(habitName);
                habitState.setHabitGroup(habitGroup);
                habitState.setFrequency(habitFrequency);
                habitState.setStatus(habitStatus);
                habitState.setPriority(habitPriority);
                habitState.setStreakCount(habitStreakCount);
                habitState.setStartDateTime(startTime);

                modifyHabitViewModel.firePropertyChanged();
                viewManagerModel.setState(modifyHabitViewModel.getViewName());
                viewManagerModel.firePropertyChanged();
            }
        }
    }

    private String[] extendColumns(String[] originalCols, String newColName) {
        String[] newCols = new String[originalCols.length + 1];
        System.arraycopy(originalCols, 0, newCols, 0, originalCols.length);
        newCols[originalCols.length] = newColName;
        return newCols;
    }

    public ViewTasksAndHabitsView(ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel,
                                  ViewManagerModel viewManagerModel, LoggedInViewModel loggedInViewModel,
                                  ModifyHabitViewModel modifyHabitViewModel, ModifyTaskViewModel modifyTaskViewModel) {

        this.viewTasksAndHabitsViewModel = viewTasksAndHabitsViewModel;
        this.viewTasksAndHabitsViewModel.addPropertyChangeListener(this);
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.modifyHabitViewModel = modifyHabitViewModel;
        this.modifyTaskViewModel = modifyTaskViewModel;

        String[] taskColsWithButton = extendColumns(viewTasksAndHabitsViewModel.TASKCOLS, "Modify");
        String[] habitColsWithButton = extendColumns(viewTasksAndHabitsViewModel.HABITCOLS, "Modify");

        this.taskModel = new DefaultTableModel(taskColsWithButton, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == getColumnCount() - 1;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == getColumnCount() - 1) {
                    return JButton.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        this.habitModel = new DefaultTableModel(habitColsWithButton, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == getColumnCount() - 1;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == getColumnCount() - 1) {
                    return JButton.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        final JPanel TablePanel = new JPanel();
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.exitButton = new JButton("Exit");
        this.refreshButton = new JButton("Refresh");
        this.createTaskButton = new JButton("Create Task");
        this.deleteTaskButton = new JButton("Delete Task");
        this.createHabitButton = new JButton("Create Habit");
        this.deleteHabitButton = new JButton("Delete Habit");

        styleActionButton(this.createTaskButton);
        styleActionButton(this.deleteTaskButton);
        styleActionButton(this.createHabitButton);
        styleActionButton(this.deleteHabitButton);
        styleActionButton(this.refreshButton);
        styleActionButton(this.exitButton);

        buttonPanel.add(this.createTaskButton);
        buttonPanel.add(this.deleteTaskButton);
        buttonPanel.add(this.createHabitButton);
        buttonPanel.add(this.deleteHabitButton);
        buttonPanel.add(this.refreshButton);
        buttonPanel.add(this.exitButton);

        final JLabel TableLabel = new JLabel("Tasks and Habits");
        TableLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        TableLabel.setForeground(PRIMARY_COLOR.darker());
        TableLabel.setBorder(new EmptyBorder(12, 16, 12, 16));

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        taskTable = new JTable(this.taskModel);
        taskTable.setFillsViewportHeight(true);
        taskTable.setRowHeight(26);
        taskTable.setGridColor(TABLE_GRID_COLOR);
        taskTable.setShowVerticalLines(false);
        taskTable.setSelectionBackground(TABLE_SELECTION_COLOR);
        JScrollPane taskScrollPane = new JScrollPane(taskTable);
        taskScrollPane.getViewport().setBackground(Color.WHITE);
        styleTableHeader(taskTable);

        habitTable = new JTable(this.habitModel);
        habitTable.setFillsViewportHeight(true);
        habitTable.setRowHeight(26);
        habitTable.setGridColor(TABLE_GRID_COLOR);
        habitTable.setShowVerticalLines(false);
        habitTable.setSelectionBackground(TABLE_SELECTION_COLOR);
        JScrollPane habitScrollPane = new JScrollPane(habitTable);
        habitScrollPane.getViewport().setBackground(Color.WHITE);
        styleTableHeader(habitTable);

        styleTableStriping(taskTable);
        styleTableStriping(habitTable);

        int taskButtonColumnIndex = taskTable.getColumnCount() - 1;
        ButtonHandler taskButtonHandler = new ButtonHandler("Modify Task", "task");
        taskTable.getColumnModel().getColumn(taskButtonColumnIndex).setCellRenderer(taskButtonHandler);
        taskTable.getColumnModel().getColumn(taskButtonColumnIndex).setCellEditor(taskButtonHandler);

        int habitButtonColumnIndex = habitTable.getColumnCount() - 1;
        ButtonHandler habitButtonHandler = new ButtonHandler("Modify Habit", "habit");
        habitTable.getColumnModel().getColumn(habitButtonColumnIndex).setCellRenderer(habitButtonHandler);
        habitTable.getColumnModel().getColumn(habitButtonColumnIndex).setCellEditor(habitButtonHandler);

        TablePanel.setBackground(BACKGROUND_COLOR);

        add(TablePanel, BorderLayout.SOUTH);
        add(TableLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(1200, 500));
        tabbedPane.addTab("Tasks", taskScrollPane);
        tabbedPane.addTab("Habits", habitScrollPane);
        tabbedPane.setBackground(BACKGROUND_COLOR);

        add(tabbedPane, BorderLayout.CENTER);

        this.exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(exitButton)) {
                    viewManagerModel.setState(loggedInViewModel.getViewName());
                    viewManagerModel.firePropertyChanged();
                }
            }
        });

        this.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(refreshButton)) {

                    if (viewTasksAndHabitsController != null) {
                        viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
                    } else {
                        JOptionPane.showMessageDialog(ViewTasksAndHabitsView.this,
                                "Initialization in progress. Please wait a moment.",
                                "Loading", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        createTaskButton.addActionListener(e -> {
            viewManagerModel.setState("create task");
            viewManagerModel.firePropertyChanged();
        });

        deleteTaskButton.addActionListener(e -> {
            viewManagerModel.setState("delete task");
            viewManagerModel.firePropertyChanged();
        });

        createHabitButton.addActionListener(e -> {
            viewManagerModel.setState("create habit");
            viewManagerModel.firePropertyChanged();
        });

        deleteHabitButton.addActionListener(e -> {
            viewManagerModel.setState("delete habit");
            viewManagerModel.firePropertyChanged();
        });

        if (this.viewTasksAndHabitsController != null && this.loggedInViewModel != null) {
            this.viewTasksAndHabitsController.getFormattedTasksAndHabits(this.loggedInViewModel);
        }

    }

    private void styleActionButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
    }

    private void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER_COLOR));
    }

    private void styleTableStriping(JTable table) {
        DefaultTableCellRenderer baseRenderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = baseRenderer.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(TABLE_SELECTION_COLOR);
                } else {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(TABLE_ALT_ROW_COLOR);
                    }
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(Color.DARK_GRAY);
                }
                return c;
            }
        });
    }

    public void actionPerformed(ActionEvent evt) {
        // Empty
    }

    public void updateTable(ArrayList<ArrayList<String>> taskList, ArrayList<ArrayList<String>> habitList) {

        taskModel.setRowCount(0);
        habitModel.setRowCount(0);

        for (ArrayList<String> row : taskList) {
            row.add("Modify Task");
            Object[] rowData = row.toArray();
            taskModel.addRow(rowData);
        }

        for (ArrayList<String> row : habitList) {
            row.add("Modify Habit");
            Object[] rowData = row.toArray();
            habitModel.addRow(rowData);
        }
    }

    public void setViewTasksAndHabitsController(ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
        if (this.loggedInViewModel != null) {
            this.viewTasksAndHabitsController.getFormattedTasksAndHabits(this.loggedInViewModel);
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            ViewTasksAndHabitsState state = (ViewTasksAndHabitsState) viewTasksAndHabitsViewModel.getState();

            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Data Loading Error",
                        JOptionPane.ERROR_MESSAGE);
                state.setErrorMessage(null);
            }

            ArrayList<ArrayList<String>> formattedTasks = state.getFormattedTasks();
            ArrayList<ArrayList<String>> formattedHabits = state.getFormattedHabits();
            updateTable(formattedTasks, formattedHabits);
        }
    }
}
