package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;

import interface_adapter.logged_in.LoggedInViewModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;

public class ViewTasksAndHabitsView extends JPanel {

    private final ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;
    private final String viewName = "view tasks and habits";
    private ViewManagerModel viewManagerModel;
    private LoggedInViewModel loggedInViewModel;


    public ViewTasksAndHabitsView(ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel,
                                  ViewManagerModel viewManagerModel, LoggedInViewModel loggedInViewModel, ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.viewTasksAndHabitsViewModel = viewTasksAndHabitsViewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;

        final JPanel mainPanel = new JPanel();
        final JPanel taskTablePanel = new JPanel();
        final JPanel habitTablePanel = new JPanel();

        final JButton exitButton = new JButton("Exit");
        final JButton refreshButton = new JButton("Refresh");

        mainPanel.add(taskTablePanel);
        mainPanel.add(habitTablePanel);
        mainPanel.add(exitButton);
        mainPanel.add(refreshButton);

        final JLabel taskTableLabel = new JLabel("Task Table");
        final JLabel habitTableLabel = new JLabel("Habit Table");

        final DefaultTableModel taskModel = new DefaultTableModel(viewTasksAndHabitsViewModel.taskCols, 0);
        final DefaultTableModel habitModel = new DefaultTableModel(viewTasksAndHabitsViewModel.habitCols, 0);

        JTable taskTable = new JTable(taskModel);
        JTable habitTable = new JTable(habitModel);

        taskModel.addRow(ViewTasksAndHabitsViewModel.sampleTask);
        habitModel.addRow(ViewTasksAndHabitsViewModel.sampleHabit);

        taskTablePanel.add(taskTableLabel);
        taskTablePanel.add(taskTable);
        habitTablePanel.add(habitTableLabel);
        habitTablePanel.add(habitTable);

        this.add(mainPanel);

        taskModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {

                int row = e.getFirstRow();
                int col = e.getColumn();

                Object changedValue = taskModel.getValueAt(row, col);

                String taskName = taskModel.getValueAt(row, 0).toString();

                viewTasksAndHabitsController.execute("task", col, taskName, changedValue);

            }
        });

        habitModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                Object changedValue = habitModel.getValueAt(row, col);

                String habitName = habitModel.getValueAt(row, 0).toString();

                viewTasksAndHabitsController.execute("habit", col, habitName, changedValue);

            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(exitButton)) {
                    viewTasksAndHabitsController.exit();
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(refreshButton)) {
                    viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);

                    ViewTasksAndHabitsState state = (ViewTasksAndHabitsState) viewTasksAndHabitsViewModel.getState();

                    ArrayList<ArrayList<String>> taskList = state.getFormattedTasks();

                    for (ArrayList<String> row : taskList) {
                        Object[] rowData = row.toArray();
                        taskModel.addRow(rowData);
                    }

                    ArrayList<ArrayList<String>> habitList = state.getFormattedHabits();

                    for (ArrayList<String> row : habitList) {
                        Object[] rowData = row.toArray();
                        habitModel.addRow(rowData);
                    }
                }
            }
        });
    }

    public void setViewTasksAndHabitsController (ViewTasksAndHabitsController viewTasksAndHabitsController){
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
    }

    public String getViewName () {
        return viewName;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }
}


