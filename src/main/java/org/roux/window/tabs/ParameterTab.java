package org.roux.window.tabs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParameterTab extends CustomTab {

    public ParameterTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton) {
        super(sourceWindow, name, confirmButton, cancelButton);

        Slider searchMaxDisplaySlider = new Slider(5, 20, 10);

        VBox root = new VBox(searchMaxDisplaySlider);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setContent(root);
    }
}
