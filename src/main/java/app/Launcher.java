package app;

public class Launcher {
    /**
     * Entry point workaround for JavaFX applications.
     * Running this class instead of Main directly avoids "JavaFX runtime components are missing" errors
     * when keeping the JavaFX JARs on the classpath rather than the module path.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
