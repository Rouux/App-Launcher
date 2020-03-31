package org.roux.window;

import javafx.scene.Parent;
import javafx.scene.Scene;
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

        root.setOnMousePressed(event -> {
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
        });
        root.setOnMouseDragged(event -> {
            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);
        });
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        root.setStyle("-fx-border-color: #060606;");

        setScene(scene);
    }

}
