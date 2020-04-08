package org.roux.window.tabs;

import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public abstract class CustomTab extends Tab {
    protected final Stage sourceWindow;

    public CustomTab(final Stage sourceWindow, final String name) {
        super(name);
        this.sourceWindow = sourceWindow;
        setClosable(false);
    }

    public void setRoot(final Parent root) {
        setContent(root);
    }

    protected void looseFocus() {
        sourceWindow.getScene().getRoot().requestFocus();
    }

}
