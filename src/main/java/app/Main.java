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
                .addUpdateProfileView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addViewLeaderboardUseCase()
                .addUpdateProfileUseCase()
                .build();

        application.pack();
        application.setVisible(true);
    }
}
