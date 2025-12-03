package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import interface_adapter.ViewManagerModel;
import interface_adapter.view_stats.ViewStatsState;
import interface_adapter.view_stats.ViewStatsViewModel;


public class StatsView extends JPanel implements ActionListener, PropertyChangeListener {

    private final ViewManagerModel viewManagerModel;

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font VALUE_FONT = new Font("SansSerif", Font.BOLD, 36);

    // Updated, more modern color palette (same visual layout)
    private static final Color PRIMARY_COLOR = new Color(255, 161, 108);      // soft indigo
    private static final Color SECONDARY_COLOR = new Color(242, 244, 248);   // light gray background
    private static final Color CARD_BORDER_COLOR = new Color(218, 222, 232); // subtle card border
    private static final Color MUTED_TEXT_COLOR = new Color(120, 130, 150);  // muted label text

    private final JLabel MAX_STREAK_VALUE = new JLabel("0");
    private final JLabel TASKS_COMPLETED_VALUE = new JLabel("0 / 0");
    private final JLabel HABITS_COMPLETED_VALUE = new JLabel("0 / 0");

    private final JButton back = new JButton("Back");

    private final String viewName = "view stats";
    private final ViewStatsViewModel viewStatsViewModel;

    public StatsView(ViewStatsViewModel viewStatsViewModel, ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        this.viewStatsViewModel = viewStatsViewModel;
        this.viewStatsViewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(SECONDARY_COLOR);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        final JLabel title = new JLabel("Your Productivity Dashboard", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR.darker());
        title.setBorder(new EmptyBorder(0, 0, 30, 0));
        this.add(title, BorderLayout.NORTH);

        final JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(SECONDARY_COLOR);
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(createStatCard("Longest active habit streak", MAX_STREAK_VALUE), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(createStatCard("Total Tasks Completed", TASKS_COMPLETED_VALUE), gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        contentPanel.add(createStatCard("Total Habits Completed", HABITS_COMPLETED_VALUE), gbc);

        this.add(contentPanel, BorderLayout.CENTER);

        back.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChanged();
        });

        // Just recolor the existing back button, no layout change
        back.setBackground(PRIMARY_COLOR);
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(SECONDARY_COLOR);
        backButtonPanel.add(back);

        this.add(backButtonPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper method to create a stylized card for a single statistic.
     * @param title statistics card title
     * @param valueLabel label value
     */
    private JPanel createStatCard(String title, JLabel valueLabel) {
        final JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        Border line = BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1);
        Border margin = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        card.setBorder(BorderFactory.createCompoundBorder(line, margin));

        final JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(MUTED_TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(VALUE_FONT);
        valueLabel.setForeground(PRIMARY_COLOR.darker());
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // No actions currently defined for buttons, so this is left empty.
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof ViewStatsState) {
            final ViewStatsState currState = (ViewStatsState) evt.getNewValue();
            setText(currState);
        }
    }

    /**
     * Updates the content of the value labels based on the state.
     * @param currState current state
     */
    private void setText(ViewStatsState currState) {
        MAX_STREAK_VALUE.setText(String.valueOf(currState.getLongestHabitStreak()));
        TASKS_COMPLETED_VALUE.setText(currState.getNumTasksCompleted() + " / " + currState.getTotalTasks());
        HABITS_COMPLETED_VALUE.setText(currState.getNumHabitsCompleted() + " / " + currState.getTotalHabits());
    }

    /**
     * Sets the view manager model.
     * @param viewManagerModel view manager model
     */
    public void setViewManager(ViewManagerModel viewManagerModel) {

    }

    /**
     * Returns view name.
     * @return view name
     */
    public String getViewName() {
        return viewName;
    }
}
