package app;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addLeaderboardView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
//                .addViewLeaderboardUseCase()
                .build();

        application.pack();
        application.setVisible(true);
    }
}
