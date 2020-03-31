package org.roux.window.tabs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParameterTab extends CustomTab {

    public ParameterTab(final Stage sourceWindow, final String name, final Button confirmButton,
                        final Button cancelButton) {
        super(sourceWindow, name, confirmButton, cancelButton);

        final Slider searchMaxDisplaySlider = new Slider(5, 20, 10);

        final VBox root = new VBox(searchMaxDisplaySlider);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setRoot(sourceWindow, root);
    }
}
