package view;

import interface_adapter.delete_habit.DeleteHabitController;
import interface_adapter.delete_habit.DeleteHabitState;
import interface_adapter.delete_habit.DeleteHabitViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DeleteHabitView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "delete habit";

    private final DeleteHabitViewModel deleteHabitViewModel;

    private final JTextField usernameInputField = new JTextField(15);
    private final JTextField habitNameInputField = new JTextField(15);

    private DeleteHabitController deleteHabitController = null;

    private final JButton deleteButton;
    private final JButton cancelButton;

    public DeleteHabitView(DeleteHabitViewModel deleteHabitViewModel) {
        this.deleteHabitViewModel = deleteHabitViewModel;
        deleteHabitViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(DeleteHabitViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelTextPanel usernamePanel = new LabelTextPanel(
                new JLabel(DeleteHabitViewModel.USERNAME_LABEL), usernameInputField);

        LabelTextPanel habitNamePanel = new LabelTextPanel(
                new JLabel(DeleteHabitViewModel.HABIT_NAME_LABEL), habitNameInputField);

        deleteButton = new JButton(DeleteHabitViewModel.DELETE_BUTTON_LABEL);
        cancelButton = new JButton(DeleteHabitViewModel.CANCEL_BUTTON_LABEL);

        JPanel buttons = new JPanel();
        buttons.add(deleteButton);
        buttons.add(cancelButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteHabitState state = deleteHabitViewModel.getState();
                deleteHabitController.execute(
                        state.getUsername(),
                        state.getHabitName()
                );
            }
        });

        cancelButton.addActionListener(this);

        addUsernameListener();
        addHabitNameListener();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(usernamePanel);
        this.add(habitNamePanel);
        this.add(buttons);
    }

    private void addUsernameListener() {
        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                DeleteHabitState state = deleteHabitViewModel.getState();
                state.setUsername(usernameInputField.getText());
                deleteHabitViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void addHabitNameListener() {
        habitNameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                DeleteHabitState state = deleteHabitViewModel.getState();
                state.setHabitName(habitNameInputField.getText());
                deleteHabitViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Cancel not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DeleteHabitState state = (DeleteHabitState) evt.getNewValue();

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        } else if (state.getSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getSuccessMessage());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setDeleteHabitController(DeleteHabitController controller) {
        this.deleteHabitController = controller;
    }
}
