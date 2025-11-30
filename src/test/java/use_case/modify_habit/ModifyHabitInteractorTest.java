package use_case.modify_habit;

import data_access.InMemoryHabitDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entities.Habit;
import entities.HabitBuilder;
import entities.User;
import entities.UserFactory;
import org.junit.jupiter.api.Test;
import use_case.gateways.HabitGateway;
import use_case.gateways.UserGateway;


import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

public class ModifyHabitInteractorTest {

    @Test
    void successTest(){
        ModifyHabitInputData inputData = new ModifyHabitInputData("Programming",
                "1", true, "2025-11-27T00:00:00", "1",
                "Programming", "1", "Programming", "2",
                true, "2025-11-27T00:00:00", "5",
                "Programming", "1", "Zain");

        UserGateway userGateway = new InMemoryUserDataAccessObject();
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();

        UserFactory userFactory = new UserFactory();
        HabitBuilder habitBuilder = new HabitBuilder();

        User user = userFactory.create("Zain", "12345");
        Habit oldHabit = habitBuilder.setHabitName("Programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setPriority(1)
                .setStatus(true)
                .setStreakCount(1)
                .build();
        Habit newHabit = habitBuilder.setHabitName("Programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setPriority(2)
                .setStatus(true)
                .setStreakCount(5)
                .build();

        userGateway.createUser(user);
        habitGateway.addHabit("Zain", oldHabit);
        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData outputData) {
                assertEquals(1, outputData.getHabitList().toArray().length);
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected");
            }

            @Override
            public void switchToHabitListView() {

            }

        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.execute(inputData);
        assertEquals(true, habitGateway.getHabitsForUser("Zain").contains(newHabit));
        assertEquals(false, habitGateway.getHabitsForUser("Zain").contains(oldHabit));
    }
    @Test
    void successTestSwitch(){


        UserGateway userGateway = new InMemoryUserDataAccessObject();
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();

        UserFactory userFactory = new UserFactory();
        HabitBuilder habitBuilder = new HabitBuilder();

        User user = userFactory.create("Zain", "12345");
        Habit oldHabit = habitBuilder.setHabitName("Programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setPriority(1)
                .setStatus(true)
                .setStreakCount(1)
                .build();


        userGateway.createUser(user);
        habitGateway.addHabit("Zain", oldHabit);
        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected");
            }

            @Override
            public void switchToHabitListView() {

            }

        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.switchToHabitListView();

    }

    @Test
    void failTestDate(){
        ModifyHabitInputData inputData = new ModifyHabitInputData("Programming",
                "1", true, "2025-11-27T00:00:00", "1",
                "Programming", "1", "Programming", "2",
                true, "2025-11-2700:00:00", "5",
                "Programming", "1", "Zain");

        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();

        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                fail("Use case failure is unexpected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Invalid Date/Time Format", errorMessage);
            }

            @Override
            public void switchToHabitListView() {

            }

        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.execute(inputData);

    }

    @Test
    void failTestTime(){
        ModifyHabitInputData inputData = new ModifyHabitInputData("Programming",
                "1", true, "2025-11-27T00:00:00", "1",
                "Programming", "1", "Programming", "2",
                true, "2025-11-27T00:00:00", "wrong format",
                "Programming", "1", "Zain");


        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();

        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                fail("Use case failure is unexpected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Invalid Priority or Streak Count", errorMessage);
            }

            @Override
            public void switchToHabitListView() {

            }

        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.execute(inputData);

    }

    @Test
    void failRepeatTaskName(){
        ModifyHabitInputData inputData = new ModifyHabitInputData("Programming",
                "1", true, "2025-11-27T00:00:00", "1",
                "Programming", "1", "Studying", "2",
                true, "2025-11-27T00:00:00", "5",
                "Programming", "1", "Zain");


        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        HabitBuilder habitBuilder = new HabitBuilder();

        Habit differentHabit = habitBuilder
                .setHabitName("Studying")
                .setHabitGroup("Studying")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setPriority(2)
                .setStatus(true)
                .setStreakCount(5)
                .build();
        Habit differentHabit2 = habitBuilder
                .setHabitName("Face care")
                .setHabitGroup("Face care")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse( "2025-11-27T00:00:00"))
                .setPriority(2)
                .setStatus(true)
                .setStreakCount(5)
                .build();

        habitGateway.addHabit("Zain", differentHabit2);
        habitGateway.addHabit("Zain", differentHabit);

        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                return;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Habit already exists", errorMessage);
            }

            @Override
            public void switchToHabitListView() {

            }

        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.execute(inputData);

    }

}
