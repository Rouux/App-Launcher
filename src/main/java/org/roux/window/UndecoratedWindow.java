package org.roux.window;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

public abstract class UndecoratedWindow extends Stage {

    protected Scene scene;

    public UndecoratedWindow() {
        initStyle(StageStyle.UNDECORATED);
    }

    public void setRoot(final Parent root) {
        scene = new Scene(root);
        setScene(scene);
        final JMetro jMetro = new JMetro();
        jMetro.setScene(scene);
        jMetro.setStyle(Style.DARK);

        root.setOnKeyReleased(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                System.out.println("Enter -> OK");
                onConfirmAction();
            } else if(event.getCode().equals(KeyCode.ESCAPE)) {
                System.out.println("Escape -> CANCEL");
                onCancelAction();
            }
        });
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        root.getStylesheets().add("style.css");
        root.setStyle("-fx-border-color: #060606;");

        showingProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue) {
                onOpenWindow();
            }
        });
    }

    protected void onOpenWindow() {}

    protected abstract void onConfirmAction();

    protected abstract void onCancelAction();

}
