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
        this.initStyle(StageStyle.UNDECORATED);
    }

    public void setRoot(Parent root) {
        this.scene = new Scene(root);
        JMetro jMetro = new JMetro(scene, Style.DARK);

        root.setOnMousePressed(event -> {
            xOffset = UndecoratedStage.this.getX() - event.getScreenX();
            yOffset = UndecoratedStage.this.getY() - event.getScreenY();
        });
        root.setOnMouseDragged(event -> {
            UndecoratedStage.this.setX(event.getScreenX() + xOffset);
            UndecoratedStage.this.setY(event.getScreenY() + yOffset);
        });
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        root.setStyle("-fx-border-color: #060606;");

        this.setScene(scene);
    }

}
