package app;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.swing.JFrame; // this is just for Type Safety

public class Main {
    public static void  main(String[] args) {
        try {
            AppBuilder appBuilder = new AppBuilder();
            
            // ===== Google Drive online leaderboard feature is temporarily disabled =====
            // The Google Drive integration code is preserved for future use.
            // Currently, leaderboard uses local habits.csv via LocalLeaderboardDataAccessObject.
            // 
            // To re-enable Google Drive leaderboard in the future, uncomment the following:
            // try {
            //     appBuilder.useGoogleDriveForLeaderboard("https://drive.google.com/file/d/1OPZrtD153oxLSRMMnTkR28OtR5f4jSwn/view?usp=drive_link");
            //     System.out.println("Google Drive leaderboard enabled successfully.");
            // } catch (Exception e) {
            //     System.err.println("Warning: Failed to initialize Google Drive leaderboard. Falling back to local CSV for leaderboard.");
            //     System.err.println("Error: " + e.getMessage());
            // }
            //
            // Note: All habit CRUD operations (create, modify, delete) use local habits.csv
            // Only the leaderboard view would read from Google Drive (if configured above)
        
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
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
