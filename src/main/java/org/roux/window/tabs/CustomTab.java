package org.roux.window.tabs;

import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public abstract class CustomTab extends Tab {

    //    private final double xOffset = 0;
    //    private final double yOffset = 0;

    protected final Stage sourceWindow;

    public CustomTab(final Stage sourceWindow, final String name) {
        super(name);
        this.sourceWindow = sourceWindow;
        setClosable(false);
    }

    public void setRoot(final Stage source, final Parent root) {
        //        root.setOnMousePressed(event -> {
        //            xOffset = source.getX() - event.getScreenX();
        //            yOffset = source.getY() - event.getScreenY();
        //        });
        //        root.setOnMouseDragged(event -> {
        //            source.setX(event.getScreenX() + xOffset);
        //            source.setY(event.getScreenY() + yOffset);
        //        });
        setContent(root);
    }

    protected void looseFocus() {
        sourceWindow.getScene().getRoot().requestFocus();
    }

}
