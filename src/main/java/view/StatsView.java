package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.view_stats.ViewStatsController;
import interface_adapter.view_stats.ViewStatsState;
import interface_adapter.view_stats.ViewStatsViewModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StatsView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "view stats";

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font VALUE_FONT = new Font("SansSerif", Font.BOLD, 36);
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241); // Light Gray background

    private final JLabel MAX_STREAK_VALUE = new JLabel("0");
    private final JLabel TASKS_COMPLETED_VALUE = new JLabel("0 / 0");
    private final JLabel HABITS_COMPLETED_VALUE = new JLabel("0 / 0");

    private ViewStatsController viewStatsController = null;
    private final ViewStatsViewModel viewStatsViewModel;

    public StatsView(ViewStatsViewModel viewStatsViewModel) {
        this.viewStatsViewModel = viewStatsViewModel;
        this.viewStatsViewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout(10, 10)); // 10px spacing
        this.setBackground(SECONDARY_COLOR);
        this.setBorder(new EmptyBorder(20, 20, 20, 20)); // Outer padding

        JLabel title = new JLabel("Your Productivity Dashboard", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR.darker());
        title.setBorder(new EmptyBorder(0, 0, 30, 0)); // Spacing below title
        this.add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between cards


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
    }

    /**
     * Helper method to create a stylized card for a single statistic.
     */
    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);


        Border line = BorderFactory.createLineBorder(PRIMARY_COLOR, 1);
        Border margin = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        card.setBorder(BorderFactory.createCompoundBorder(line, margin));


        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(Color.GRAY.darker());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Value Label (The actual number)
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
            ViewStatsState currState = (ViewStatsState) evt.getNewValue();
            setText(currState);
        }
    }

    /**
     * Updates the content of the value labels based on the state.
     */
    private void setText(ViewStatsState currState) {
        MAX_STREAK_VALUE.setText(String.valueOf(currState.getLongestHabitStreak()));
        TASKS_COMPLETED_VALUE.setText(currState.getNumTasksCompleted() + " / " + currState.getTotalTasks());
        HABITS_COMPLETED_VALUE.setText(currState.getNumHabitsCompleted() + " / " + currState.getTotalHabits());
    }

    public void setViewStatsController(ViewStatsController viewStatsController) {
        this.viewStatsController = viewStatsController;
    }

    public void setViewManager(ViewManagerModel viewManagerModel) {

    }

    public String getViewName() {
        return viewName;
    }
}