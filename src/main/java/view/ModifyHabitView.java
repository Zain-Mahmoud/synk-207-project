package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import interface_adapter.ViewManagerModel;
import interface_adapter.modify_habit.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;


public class ModifyHabitView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "modify habit";
    private final ModifyHabitViewModel modifyHabitViewModel;

    private final JTextField newHabitName = new JTextField();
    private final JTextField newStartDateTime = new JTextField();
    private final JTextField newFrequency = new JTextField();
    private final JTextField newHabitGroup = new JTextField();
    private final JRadioButton habitCompleted = new JRadioButton("Completed");
    private final JRadioButton habitNotCompleted = new JRadioButton("Not completed");
    private final ButtonGroup newHabitStatus = new ButtonGroup();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private final JFormattedTextField newHabitPriority = new JFormattedTextField(numberFormat);
    private final JFormattedTextField newStreakCount = new JFormattedTextField(numberFormat);

    private ModifyHabitController modifyHabitController = null;

    private final JButton save = new JButton("save");
    private final JButton cancel = new JButton("cancel");
    private ViewManagerModel viewManagerModel;

    public ModifyHabitView(ModifyHabitViewModel modifyHabitViewModel) {

        ModifyHabitState currState = modifyHabitViewModel.getState();

        this.modifyHabitViewModel = modifyHabitViewModel;
        this.modifyHabitViewModel.addPropertyChangeListener(this);

        newHabitStatus.add(habitCompleted);
        newHabitStatus.add(habitNotCompleted);

        JLabel habitNameLabel = new JLabel("Habit name");
        JLabel startDateTimeLabel = new JLabel("Start date/time");
        JLabel frequencyLabel = new JLabel("Frequency");
        JLabel habitGroupLabel = new JLabel("Habit group");
        JLabel habitStatusLabel = new JLabel("Habit status");
        JLabel habitPriorityLabel = new JLabel("Habit priority");
        JLabel streakCountLabel = new JLabel("Streak count");




        if (currState.getStatus()){
            habitCompleted.setSelected(true);
        } else{
            habitCompleted.setSelected(true);
        }

        newHabitName.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();
                currentState.setHabitName(newHabitName.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });

        newStartDateTime.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();
                currentState.setStartDateTime(newStartDateTime.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });

        newFrequency.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();
                currentState.setFrequency(newFrequency.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });

        newHabitGroup.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();
                currentState.setHabitGroup(newHabitGroup.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });

        newHabitPriority.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();
                    currentState.setPriority(newHabitPriority.getText());

            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });

        newStreakCount.getDocument().addDocumentListener(new DocumentListener() {

            public void documentStateHelper() {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();

                    currentState.setStreakCount(newStreakCount.getText());

            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentStateHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentStateHelper();
            }
        });

        habitCompleted.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();
                currentState.setStatus(true);
            }
        });

        habitNotCompleted.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final ModifyHabitState currentState = modifyHabitViewModel.getState();
                currentState.setStatus(false);
            }
        });

        JPanel habitStatus = new JPanel();
        JPanel habitPriority = new JPanel();
        JPanel habitName = new JPanel();
        JPanel startDateTime = new JPanel();
        JPanel frequency = new JPanel();
        JPanel habitGroup = new JPanel();
        JPanel streakCount = new JPanel();

        habitStatus.add(habitStatusLabel);
        habitStatus.add(habitCompleted);
        habitStatus.add(habitNotCompleted);
        habitStatus.setLayout(new BoxLayout(habitStatus, BoxLayout.X_AXIS));

        habitName.add(habitNameLabel);
        habitName.add(newHabitName);
        habitName.setLayout(new BoxLayout(habitName, BoxLayout.X_AXIS));

        startDateTime.add(startDateTimeLabel);
        startDateTime.add(newStartDateTime);
        startDateTime.setLayout(new BoxLayout(startDateTime, BoxLayout.X_AXIS));

        frequency.add(frequencyLabel);
        frequency.add(newFrequency);
        frequency.setLayout(new BoxLayout(frequency, BoxLayout.X_AXIS));

        habitGroup.add(habitGroupLabel);
        habitGroup.add(newHabitGroup);
        habitGroup.setLayout(new BoxLayout(habitGroup, BoxLayout.X_AXIS));

        habitPriority.add(habitPriorityLabel);
        habitPriority.add(newHabitPriority);
        habitPriority.setLayout(new BoxLayout(habitPriority, BoxLayout.X_AXIS));

        streakCount.add(streakCountLabel);
        streakCount.add(newStreakCount);
        streakCount.setLayout(new BoxLayout(streakCount, BoxLayout.X_AXIS));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(save);
        buttons.add(cancel);

        this.add(habitName);
        this.add(startDateTime);
        this.add(frequency);
        this.add(habitGroup);
        this.add(habitStatus);
        this.add(habitPriority);
        this.add(streakCount);
        this.add(buttons);

        cancel.addActionListener(evt -> {
            if (evt.getSource().equals(cancel)){
                modifyHabitController.switchToHabitListView();
            }
        });

        save.addActionListener(evt -> {
            if (evt.getSource().equals(save)){
                ModifyHabitState currentState = modifyHabitViewModel.getState();
                modifyHabitController.execute(currentState.getOldHabitName(), currentState.getOldPriority(),
                        currentState.getOldStatus(),
                        currentState.getOldStartDateTime(),
                        currentState.getOldStreakCount(),
                        currentState.getOldHabitGroup(),
                        currentState.getOldFrequency(),
                        currentState.getHabitName(),
                        currentState.getPriority(),
                        currentState.getStatus(),
                        currentState.getStartDateTime(),
                        currentState.getStreakCount(),
                        currentState.getHabitGroup(),
                        currentState.getFrequency()
                );
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final ModifyHabitState currState = (ModifyHabitState) evt.getNewValue();
        if (currState.getHabitError() != null){
            JOptionPane.showMessageDialog(this, currState.getHabitError());
        } else {
            newHabitName.setText(currState.getOldHabitName());
            newFrequency.setText(currState.getOldFrequency());
            newHabitPriority.setText(currState.getOldPriority());
            newStreakCount.setText(currState.getOldStreakCount());
            newHabitGroup.setText(currState.getOldHabitGroup());
            newStartDateTime.setText(currState.getOldStartDateTime());
            if (currState.getOldStatus()) {
                habitCompleted.setSelected(true);
            } else {
                habitCompleted.setSelected(false);
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setModifyHabitController(ModifyHabitController modifyHabitController) {
        this.modifyHabitController = modifyHabitController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel){
        this.viewManagerModel = viewManagerModel;
    }
}
