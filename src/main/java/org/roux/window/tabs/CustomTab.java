package org.roux.window.tabs;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public abstract class CustomTab extends Tab {

    private double xOffset = 0;
    private double yOffset = 0;

    protected final Stage sourceWindow;
    private final Button confirmButton;
    private final Button cancelButton;

    public CustomTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton) {
        super(name);
        this.sourceWindow = sourceWindow;
        this.confirmButton = confirmButton;
        this.cancelButton = cancelButton;
        setClosable(false);
    }

    public void setRoot(Stage source, Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = source.getX() - event.getScreenX();
            yOffset = source.getY() - event.getScreenY();
        });
        root.setOnMouseDragged(event -> {
            source.setX(event.getScreenX() + xOffset);
            source.setY(event.getScreenY() + yOffset);
        });
        this.setContent(root);
    }

    protected void addConfirmButtonEvent(EventHandler<MouseEvent> event) {
        confirmButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event);
    }

    protected void addCancelButtonEvent(EventHandler<MouseEvent> event) {
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event);
    }

}
