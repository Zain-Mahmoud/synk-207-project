package use_case.view_tasks_and_habits;

import data_access.InMemoryHabitDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entities.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsPresenter;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import org.junit.jupiter.api.Test;
import use_case.gateways.HabitGateway;
import use_case.gateways.TaskGateway;
import use_case.gateways.UserGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ViewTasksAndHabitsInteractorTest {

    @Test
    void SuccessTest() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        UserGateway userGateway = new InMemoryUserDataAccessObject();

        LoggedInViewModel loggedInViewModel = new LoggedInViewModel();

        ViewTasksAndHabitsViewModel testViewModel = new ViewTasksAndHabitsViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        TestPresenter presenter = new TestPresenter(viewManagerModel, testViewModel);

        TaskBuilder taskBuilder = new TaskBuilder();
        HabitBuilder habitBuilder = new HabitBuilder();

        Task task1 = taskBuilder
                .setTaskGroup("Problem set")
                .setTaskName("Problem Set")
                .setStartTime(LocalDateTime.parse("2025-11-26T00:00:00"))
                .setDeadline(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setStatus(false)
                .setPriority(2)
                .setDescription("Finishing a problem set")
                .build();

        Task task2 = taskBuilder
                .setTaskGroup("project")
                .setTaskName("project")
                .setStartTime(LocalDateTime.parse("2025-11-26T00:00:00"))
                .setDeadline(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setStatus(false)
                .setPriority(2)
                .setDescription("Finishing a project")
                .build();

        Habit habit1 = habitBuilder.setHabitName("Not programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setPriority(1)
                .setStatus(true)
                .setStreakCount(1)
                .setDescription("Not doing some programming")
                .build();

        Habit habit2 = habitBuilder.setHabitName("Programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setPriority(4)
                .setStatus(false)
                .setStreakCount(10)
                .setDescription("Doing some programming")
                .build();

        UserFactory factory = new UserFactory();
        User arya = factory.create("arya", "123");
        userGateway.save(arya);

        taskGateway.addTask("arya", task1);
        taskGateway.addTask("arya", task2);
        habitGateway.addHabit("arya", habit1);
        habitGateway.addHabit("arya", habit2);

        ViewTasksAndHabitsInteractor interactor =
                new ViewTasksAndHabitsInteractor(taskGateway, habitGateway, presenter);

        ArrayList<String> f1 = new ArrayList<>();
        f1.add("Problem Set");
        f1.add("26 November 25, 00:00");
        f1.add("27 November 25, 00:00");
        f1.add("Problem Set");
        f1.add("Incomplete");
        f1.add("2");
        f1.add("Finishing a problem set");

        ArrayList<String> f2 = new ArrayList<>();
        f2.add("project");
        f2.add("26 November 25, 00:00");
        f2.add("27 November 25, 00:00");
        f2.add("project");
        f2.add("Incomplete");
        f2.add("2");
        f2.add("Finishing a project");

        ArrayList<String> h1 = new ArrayList<>();
        h1.add("Not programming");
        h1.add("27 November 25, 00:00");
        h1.add("1");
        h1.add("Programming");
        h1.add("1");
        h1.add("Not doing some programming");

        ArrayList<String> h2 = new ArrayList<>();
        h2.add("Programming");
        h2.add("27 November 25, 00:00");
        h2.add("1");
        h2.add("Programming");
        h2.add("1");
        h2.add("Doing some programming");

        interactor.getFormattedTasksAndHabits(loggedInViewModel);

        ViewTasksAndHabitsState resultState = testViewModel.getState();
        assertNotNull(resultState);

        assertEquals(2, resultState.getFormattedTasks().size());
        assertEquals(2, resultState.getFormattedHabits().size());

        assertEquals(f1, resultState.getFormattedTasks().get(0));
        assertEquals(f2, resultState.getFormattedTasks().get(1));

        assertEquals(h1, resultState.getFormattedHabits().get(0));
        assertEquals(h2, resultState.getFormattedHabits().get(1));
    }

    // --------------------------------
    // Minimal presenter for capturing output
    // --------------------------------
    private static class TestPresenter extends ViewTasksAndHabitsPresenter {

        private final ViewTasksAndHabitsViewModel vm;

        TestPresenter(ViewManagerModel viewManagerModel, ViewTasksAndHabitsViewModel vm) {
            super(viewManagerModel, vm);
            this.vm = vm;
        }

        @Override
        public void prepareSuccessView(ViewTasksAndHabitsOutputData data) {
            ViewTasksAndHabitsState state = new ViewTasksAndHabitsState();
            state.setFormattedTasks(data.getFormattedTasks());
            state.setFormattedHabits(data.getFormattedHabits());
            vm.setState(state);
        }

        @Override
        public void prepareFailView(String error) {
            fail("Interactor should not fail in this test");
        }
    }
}


