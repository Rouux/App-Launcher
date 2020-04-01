module org.roux {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires json.simple;
    requires org.jfxtras.styles.jmetro;
    requires org.apache.commons.io;
    requires easybind;
    requires org.scenicview.scenicview;

    exports org.roux;
    exports org.roux.window;
    exports org.roux.application;
    exports org.roux.utils;
}