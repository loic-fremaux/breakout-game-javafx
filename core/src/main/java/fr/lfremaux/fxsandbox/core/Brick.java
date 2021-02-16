package fr.lfremaux.fxsandbox.core;

import javafx.scene.shape.Rectangle;

public class Brick {

    private final Rectangle uiObj;

    public Brick(Rectangle uiObj) {
        this.uiObj = uiObj;
    }

    public Rectangle getUiObj() {
        return uiObj;
    }
}
