package org.roux.window.tabs;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public abstract class CustomTab extends Tab {

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

    protected void addConfirmButtonEvent(EventHandler<MouseEvent> event) {
        confirmButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event);
    }

    protected void addCancelButtonEvent(EventHandler<MouseEvent> event) {
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event);
    }

}
