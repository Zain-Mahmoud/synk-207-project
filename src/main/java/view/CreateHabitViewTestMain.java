package view;

import interface_adapter.create_habit.CreateHabitController;
import interface_adapter.create_habit.CreateHabitState;
import interface_adapter.create_habit.CreateHabitViewModel;
import use_case.create_habit.CreateHabitInputBoundary;
import use_case.create_habit.CreateHabitInputData;

import javax.swing.*;

public class CreateHabitViewTestMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 创建 ViewModel 和 View
            CreateHabitViewModel viewModel = new CreateHabitViewModel("create habit");
            CreateHabitView view = new CreateHabitView(viewModel);

            // 2. 准备一个假的 interactor，只打印参数并设置成功信息
            CreateHabitInputBoundary fakeInteractor = new CreateHabitInputBoundary() {
                @Override
                public void excute(CreateHabitInputData data) {
                    System.out.println("== CreateHabitView TEST ==");
                    System.out.println("username = " + data.getUsername());
                    System.out.println("habitName = " + data.getHabitName());
                    System.out.println("startDateTime = " + data.getStartDateTime());
                    System.out.println("frequencyCount = " + data.getFrequencyCount());
                    System.out.println("frequencyUnit = " + data.getFrequencyUnit());
                    System.out.println("habitGroup = " + data.getHabitGroup());
                    System.out.println("streakCount = " + data.getStreakCount());
                    System.out.println("priority = " + data.getPriority());

                    // 测试：向 ViewModel 写入成功消息，触发弹窗
                    CreateHabitState state = viewModel.getState();
                    state.setSuccessMessage("测试成功：已准备创建习惯 '" + data.getHabitName() + "'");
                    state.setErrorMessage(null);
                    viewModel.setState(state);
                    viewModel.firePropertyChanged();
                }
            };

            // 3. 用 fake interactor 创建 Controller
            CreateHabitController controller = new CreateHabitController(fakeInteractor);

            // 4. 将 controller 和 username 注入到 View
            view.setCreateHabitController(controller);
            view.setUsername("testUser");  // 模拟已登录用户

            // 5. 创建窗口显示 View
            JFrame frame = new JFrame("Create Habit View TEST");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
