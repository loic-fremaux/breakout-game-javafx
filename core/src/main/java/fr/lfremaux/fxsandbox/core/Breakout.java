package fr.lfremaux.fxsandbox.core;

import fr.lfremaux.fxsandbox.core.util.ThreadPool;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Breakout {

    private Ball ball;
    private Rectangle pane;
    private List<Brick> bricks = new LinkedList<>();

    private final Stage stage;

    private static final int TICK_RATE = 60;

    protected static final int BALL_SPEED = 5;
    protected static final int BALL_RADIUS = 25;

    private static final int BRICK_WIDTH = 100;
    private static final int BRICK_HEIGHT = 25;

    private static final int PANE_WIDTH = 125;
    private static final int PANE_HEIGHT = 20;

    protected static final int WINDOW_WIDTH = 1280;
    protected static final int WINDOW_HEIGHT = 720;

    private static final int BOTTOM_MARGIN = 50;

    public Breakout(Stage stage) {
        this.stage = stage;
    }

    public void gameLoop() {
        ThreadPool.executeTimer(UUID.randomUUID(), () -> {
            this.ball.tick();
        }, 0, 1000 / TICK_RATE, TimeUnit.MILLISECONDS);
    }

    public void buildPanel() {
        final StackPane panel = new StackPane();
        final Scene scene = new Scene(panel, WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setScene(scene);
        stage.show();

        final Rectangle pane = createPane(panel);
        this.pane = pane;

        final Circle circle = createBall(panel);
        this.ball = new Ball(circle);

        scene.setOnMouseMoved(e -> {
            Platform.runLater(() -> {
                pane.setTranslateX(e.getX()-(WINDOW_WIDTH >> 1));
            });
        });

        gameLoop();
    }

    private Rectangle createBricks(StackPane pane) {
        final Rectangle box = new Rectangle();
        box.setWidth(BRICK_WIDTH);
        box.setHeight(BRICK_HEIGHT);
        box.setStyle("-fx-background-color: #33ff8c");

        pane.getChildren().add(box);
        return box;
    }

    private Rectangle createPane(StackPane pane) {
        final Rectangle box = new Rectangle();
        box.setTranslateY((WINDOW_HEIGHT >> 1) - BOTTOM_MARGIN);
        box.setWidth(PANE_WIDTH);
        box.setHeight(PANE_HEIGHT);
        box.setStyle("-fx-background-color: #33ff8c");

        pane.getChildren().add(box);
        return box;
    }

    private Circle createBall(StackPane pane) {
        final Circle circle = new Circle();
        circle.setTranslateY((WINDOW_HEIGHT >> 1) - BOTTOM_MARGIN - (PANE_HEIGHT >> 1) - BALL_RADIUS);
        circle.setRadius(BALL_RADIUS);
        circle.setStyle("-fx-background-color: #33daff");

        pane.getChildren().add(circle);
        return circle;
    }
}
