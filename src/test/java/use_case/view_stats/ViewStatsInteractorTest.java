package use_case.view_stats;
import data_access.InMemoryHabitDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entities.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import use_case.gateways.HabitGateway;
import use_case.gateways.TaskGateway;
import use_case.gateways.UserGateway;

public class ViewStatsInteractorTest {

    @Test
    void SuccessTest(){
        UserGateway userDAO = new InMemoryUserDataAccessObject();
        HabitGateway habitDAO = new InMemoryHabitDataAccessObject();
        TaskGateway taskDAO = new InMemoryTaskDataAccessObject();

        UserFactory userFactory = new UserFactory();
        HabitBuilder habitBuilder = new HabitBuilder();
        TaskBuilder taskBuilder = new TaskBuilder();

        Habit habit = habitBuilder.setHabitName("Not programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setPriority(1)
                .setStatus(true)
                .setStreakCount(1)
                .build();
        Habit habit2 = habitBuilder.setHabitName("Programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setPriority(4)
                .setStatus(false)
                .setStreakCount(10)
                .build();
        Task task = taskBuilder
                .setTaskGroup("Problem set")
                .setTaskName("Problem set")
                .setDeadline(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setStatus(true)
                .setPriority(2)
                .build();

        Task task2 = taskBuilder
                .setTaskGroup("project")
                .setTaskName("project")
                .setDeadline(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setStatus(false)
                .setPriority(2)
                .build();

        User zain = userFactory.create("zain", "123");
        userDAO.save(zain);

        habitDAO.addHabit("zain", habit2);
        habitDAO.addHabit("zain", habit);

        taskDAO.addTask("zain", task);
        taskDAO.addTask("zain", task2);

        ViewStatsOutputBoundary viewStatsOutputBoundary = new ViewStatsOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewStatsOutputData outputData) {
                assertEquals(10, outputData.getLongestHabitStreak());
                assertEquals(1, outputData.getNumHabitsCompleted());
                assertEquals(2, outputData.getTotalHabits());
                assertEquals(1, outputData.getNumTasksCompleted());
                assertEquals(2, outputData.getTotalTasks());
            }

            @Override
            public void switchToTaskList() {
                fail("Should not switch to task list");
            }

        };
        ViewStatsInputData viewStatsInputData = new ViewStatsInputData("zain");
        ViewStatsInputBoundary viewStatsInteractor = new ViewStatsInteractor(habitDAO, taskDAO,
                viewStatsOutputBoundary);
        viewStatsInteractor.execute(viewStatsInputData);


    }

    @Test
    void SuccessSwitch(){
        UserGateway userDAO = new InMemoryUserDataAccessObject();
        HabitGateway habitDAO = new InMemoryHabitDataAccessObject();
        TaskGateway taskDAO = new InMemoryTaskDataAccessObject();

        UserFactory userFactory = new UserFactory();

        User user = userFactory.create("zain", "123");
        userDAO.save(user);





        ViewStatsOutputBoundary viewStatsOutputBoundary = new ViewStatsOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewStatsOutputData outputData) {
                fail("Should not be successful");
            }

            @Override
            public void switchToTaskList() {
                return;
            }

        };
        ViewStatsInputData viewStatsInputData = new ViewStatsInputData("zain");
        ViewStatsInputBoundary viewStatsInteractor = new ViewStatsInteractor(habitDAO, taskDAO,
                viewStatsOutputBoundary);
        viewStatsInteractor.switchToTaskView();
    }
}
