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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ViewTasksAndHabitsView extends JPanel implements ActionListener, PropertyChangeListener  {

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


    /**
     * Renders and edits the JButton component in the table cell.
     * This class acts as both the renderer (for display) and the editor (for interaction).
     */
    class ButtonHandler extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
        private final JButton button;
        private int clickedRow;
        private String designation;

        public ButtonHandler(String buttonText, String designation) {
            this.designation = designation;
            this.button = new JButton(buttonText);
            this.button.setOpaque(true);
            this.button.addActionListener(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof String) {
                button.setText((String) value);
            }

            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.clickedRow = row;
            // The button text is set from the String placeholder in the model
            if (value instanceof String) {
                button.setText((String) value);
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            // Return the string placeholder from the model
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
                String taskDueDateTime = taskModel.getValueAt(clickedRow, 1).toString(); // Back to Index 1
                String taskGroup = taskModel.getValueAt(clickedRow, 2).toString(); // Back to Index 2
                String taskStatusText = taskModel.getValueAt(clickedRow, 3).toString(); // Back to Index 3
                String taskPriority = taskModel.getValueAt(clickedRow, 4).toString(); // Back to Index 4
                String taskDescription = taskModel.getValueAt(clickedRow, 5).toString(); // Back to Index 5

                boolean taskStatus;
                if (taskStatusText.equals("Complete")){
                    taskStatus = true;
                } else{
                    taskStatus = false;
                }

                ModifyTaskState taskState = modifyTaskViewModel.getState();

                taskState.setOldTaskName(taskName);
                taskState.setOldDeadline(taskDueDateTime);
                taskState.setOldPriority(taskPriority);
                taskState.setOldStatus(taskStatus);
                taskState.setOldTaskGroup(taskGroup);
                taskState.setOldDescription(taskDescription);

                taskState.setNewTaskName(taskName);
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

        String[] taskColsWithButton = extendColumns(viewTasksAndHabitsViewModel.taskCols, "Modify");
        String[] habitColsWithButton = extendColumns(viewTasksAndHabitsViewModel.habitCols, "Modify");

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

        this.exitButton = new JButton("Exit");
        this.refreshButton = new JButton("Refresh");

        buttonPanel.add(this.exitButton);
        buttonPanel.add(this.refreshButton);

        final JLabel TableLabel = new JLabel("Tasks and Habits");

        setLayout(new BorderLayout());

        taskTable = new JTable(this.taskModel);
        taskTable.setFillsViewportHeight(true);
        JScrollPane taskScrollPane = new JScrollPane(taskTable);

        habitTable = new JTable(this.habitModel);
        habitTable.setFillsViewportHeight(true);
        JScrollPane habitScrollPane = new JScrollPane(habitTable);

        int taskButtonColumnIndex = taskTable.getColumnCount() - 1;
        ButtonHandler taskButtonHandler = new ButtonHandler("Modify Task", "task");
        taskTable.getColumnModel().getColumn(taskButtonColumnIndex).setCellRenderer(taskButtonHandler);
        taskTable.getColumnModel().getColumn(taskButtonColumnIndex).setCellEditor(taskButtonHandler);

        int habitButtonColumnIndex = habitTable.getColumnCount() - 1;
        ButtonHandler habitButtonHandler = new ButtonHandler("Modify Habit", "habit");
        habitTable.getColumnModel().getColumn(habitButtonColumnIndex).setCellRenderer(habitButtonHandler);
        habitTable.getColumnModel().getColumn(habitButtonColumnIndex).setCellEditor(habitButtonHandler);


        add(TablePanel, BorderLayout.SOUTH);
        add(TableLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(1200, 500));
        tabbedPane.addTab("Tasks", taskScrollPane);
        tabbedPane.addTab("Habits", habitScrollPane);

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


        if (this.viewTasksAndHabitsController != null && this.loggedInViewModel != null) {
            this.viewTasksAndHabitsController.getFormattedTasksAndHabits(this.loggedInViewModel);
        }

    }

    /**
     * Required method for ActionListener, though typically handled via anonymous classes now.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        // Empty
    }

    /**
     * Clears and updates the JTables with fresh data from the ViewModel.
     * Inserts the button's text as a String placeholder into each row of the DefaultTableModel.
     */
    public void updateTable(ArrayList<ArrayList<String>> taskList, ArrayList<ArrayList<String>> habitList) {

        taskModel.setRowCount(0);
        habitModel.setRowCount(0);

        for (ArrayList<String> row : taskList) {
            row.add("Modify Task");
            Object[] rowData = row.toArray();
            taskModel.addRow(rowData);
        }

        for (ArrayList<String> row : habitList) {
            // Add a String placeholder for the button column.
            row.add("Modify Habit");
            Object[] rowData = row.toArray();
            habitModel.addRow(rowData);
        }
    }

    /**
     * Setter for Dependency Injection, used by the main application builder to wire up the controller later.
     */
    public void setViewTasksAndHabitsController (ViewTasksAndHabitsController viewTasksAndHabitsController){
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
        if (this.loggedInViewModel != null) {
            this.viewTasksAndHabitsController.getFormattedTasksAndHabits(this.loggedInViewModel);
        }
    }

    public String getViewName () {
        return viewName;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Reacts to changes in the ViewModel state. This is how the table data is refreshed after a controller call.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            ViewTasksAndHabitsState state = (ViewTasksAndHabitsState) viewTasksAndHabitsViewModel.getState();


            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                state.setErrorMessage(null);
            }

            ArrayList<ArrayList<String>> formattedTasks = state.getFormattedTasks();
            ArrayList<ArrayList<String>> formattedHabits = state.getFormattedHabits();
            updateTable(formattedTasks, formattedHabits);
        }
    }
}