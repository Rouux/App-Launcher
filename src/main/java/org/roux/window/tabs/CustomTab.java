package org.roux.window.tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public abstract class CustomTab extends Tab {

    protected final Stage sourceWindow;
    protected final Button confirmButton;
    protected final Button cancelButton;

    public CustomTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton) {
        super(name);
        this.sourceWindow = sourceWindow;
        this.confirmButton = confirmButton;
        this.cancelButton = cancelButton;
        setClosable(false);
    }

}
