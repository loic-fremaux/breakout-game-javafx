package fr.lfremaux.fxsandbox.bootstrap;

import fr.lfremaux.fxsandbox.core.Breakout;
import javafx.application.Application;
import javafx.stage.Stage;

public class Bootstrap extends Application {

    public static void main(String... args) {
        System.out.println("Starting application...");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final Breakout breakout = new Breakout(stage);
        breakout.buildPanel();
    }
}
