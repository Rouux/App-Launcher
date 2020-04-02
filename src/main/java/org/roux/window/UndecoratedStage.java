package org.roux.window;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

public abstract class UndecoratedStage extends Stage {

    private double xOffset = 0;
    private double yOffset = 0;

    protected Scene scene;

    public UndecoratedStage() {
        initStyle(StageStyle.UNDECORATED);
    }

    public void setRoot(final Parent root) {
        scene = new Scene(root);
        final JMetro jMetro = new JMetro(scene, Style.DARK);
        setScene(scene);

        root.setOnMousePressed(event -> {
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
        });
        root.setOnMouseDragged(event -> {
            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);
        });
        root.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                System.out.println("Enter -> OK");
            } else if(event.getCode().equals(KeyCode.ESCAPE)) {
                System.out.println("Escape -> CANCEL");
            }
        });
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        root.setStyle("-fx-border-color: #060606;");

        showingProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue) {
                onOpenWindow();
            }
        });
    }

    protected void onOpenWindow() {}

    protected void onClosingWindow() {}

}
