package app;

import javax.swing.*;
import java.io.IOException;
import java.security.GeneralSecurityException; // this is just for Type Safety

public class Main {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addLeaderboardView()
                .addStatsView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
                .addViewLeaderboardUseCase()
                .addSyncToGoogleCalendarUseCase()
                .addViewStatsUseCase()
                .build();

        application.pack();
        application.setVisible(true);
    }
}
