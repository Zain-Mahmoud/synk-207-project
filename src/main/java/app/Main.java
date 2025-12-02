package app;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.swing.JFrame; // this is just for Type Safety

public class Main {
    public static void  main(String[] args) throws IOException, GeneralSecurityException {
        AppBuilder appBuilder = new AppBuilder();
        // Extract Spreadsheet ID from URL: https://docs.google.com/spreadsheets/d/{ID}/edit
        appBuilder.useGoogleSheetsForHabits("1Gat5OK4kULhvgyYvtwMX0vjcwoGJmh21NtVWi9IuCWU");
        
        // Optional: Enable Google Sheets for online multi-user leaderboard
        // Uncomment and replace with your Google Sheet ID to enable:
        // String spreadsheetId = "YOUR_GOOGLE_SHEET_ID_HERE";
        // appBuilder.useGoogleSheetsForHabits(spreadsheetId);
        // See GOOGLE_SHEETS_SETUP.md for detailed setup instructions
        
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addLeaderboardView()
                .addModifyTaskView()
                .addModifyHabitView()
                .addViewTasksAndHabitsView()
                .addCreateTaskView()
                .addCreateHabitView()
                .addDeleteTaskView()
                .addDeleteHabitView()
                .addUpdateProfileView()
                .addStatsView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addModifyTaskUseCase()
                .addModifyHabitUseCase()
                .addViewLeaderboardUseCase()
                .addViewTasksAndHabitsUseCase()
                .addCreateTaskUseCase()
                .addDeleteTaskUseCase()
                .addCreateHabitUseCase()
                .addDeleteHabitUseCase()
                .addSyncToGoogleCalendarUseCase()
                .addViewStatsUseCase()
                .addUpdateProfileUseCase()
                .build();

        application.pack();
        application.setVisible(true);
    }
}
