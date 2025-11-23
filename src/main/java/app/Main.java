package app;

import javax.swing.*;
import java.io.IOException;
import java.security.GeneralSecurityException; // TODO: Propagate calendar gateway initialization issues

public class Main {
    public static void main(String[] args) throws IOException, GeneralSecurityException { // TODO: Allow calendar gateway setup exceptions to surface
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addLeaderboardView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
                .addViewLeaderboardUseCase()
                .addSyncToGoogleCalendarUseCase() // TODO: Wire sync to Google Calendar use case into app
                .build();

        application.pack();
        application.setVisible(true);
    }
}
