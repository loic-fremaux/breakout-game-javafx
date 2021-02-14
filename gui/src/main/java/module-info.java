module gui {

    requires core;
    requires javafx.controls;

    opens fr.lfremaux.fxsandbox.gui to javafx.graphics;
}