package fr.lfremaux.fxsandbox.core;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class Ball {

    private boolean xDirection;
    private boolean yDirection;

    private final Circle circle;

    private static final int BORDER_X = (Breakout.WINDOW_WIDTH >> 1) - (Breakout.BALL_RADIUS >> 1);
    private static final int BORDER_Y = (Breakout.WINDOW_HEIGHT >> 1) - (Breakout.BALL_RADIUS >> 1);

    public Ball(Circle circle) {
        this.circle = circle;
        this.xDirection = true;
        this.yDirection = true;
    }

    public void tick(Rectangle pane, List<Brick> bricks) {
        this.circle.setTranslateX(this.circle.getTranslateX() + (xDirection ? -1 : +1) * Breakout.BALL_SPEED);
        this.circle.setTranslateY(this.circle.getTranslateY() + (yDirection ? -1 : +1) * Breakout.BALL_SPEED);

        checkBorderCollisions();
        checkPaneCollisions(pane);
    }

    private void checkBorderCollisions() {
        if (this.circle.getTranslateX() > BORDER_X || this.circle.getTranslateX() < -BORDER_X) {
            this.invertXDirection();
        }
        if (this.circle.getTranslateY() > BORDER_Y || this.circle.getTranslateY() < -BORDER_Y) {
            this.invertYDirection();
        }
    }

    private void checkPaneCollisions(Rectangle pane) {
        double circlePosX = this.circle.getTranslateX();


    }

    public Circle getCircle() {
        return circle;
    }

    public void invertXDirection() {
        this.xDirection = !this.xDirection;
    }

    public void invertYDirection() {
        this.yDirection = !this.yDirection;
    }
}
