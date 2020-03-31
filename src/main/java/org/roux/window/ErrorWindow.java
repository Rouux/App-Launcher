package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorWindow extends UndecoratedStage {

    public ErrorWindow(String message) {
        super();
        Label label = new Label(message);
        label.setWrapText(true);
        label.setPrefWidth(360);
        label.setPadding(new Insets(10, 5, 5, 10));
        label.setFont(Font.font(14));

        Button confirm = new Button("Ok");
        confirm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.close();
        });
        HBox confirmBox = new HBox(confirm);
        confirmBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(label, confirmBox);
        vBox.setPadding(new Insets(10));
        vBox.setMaxSize(360, 240);
        vBox.setSpacing(20);

        this.setRoot(vBox);
        this.setAlwaysOnTop(true);
        getScene().getStylesheets().add("style.css");
        this.show();
    }

    public ErrorWindow(Throwable throwable) {
        StringWriter errorMsg = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errorMsg));
        Label label = new Label(errorMsg.toString());

        Label text = new Label("Ah, ça a crash ?");
        text.setFont(Font.font(14));

        label.setWrapText(true);
        label.setPadding(new Insets(5, 5, 5, 10));
        ScrollPane scrollPane = new ScrollPane(label);
        scrollPane.setPrefViewportHeight(400);

        Button confirm = new Button("Ok");
        confirm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.close();
        });
        HBox confirmBox = new HBox(confirm);
        confirmBox.setAlignment(Pos.CENTER_RIGHT);

        VBox vBox = new VBox(text, scrollPane, confirmBox);
        vBox.setPadding(new Insets(10));
        vBox.setPrefSize(360, 320);
        vBox.setSpacing(20);

        this.setRoot(vBox);
        this.setAlwaysOnTop(true);
        getScene().getStylesheets().add("style.css");
        this.show();
    }

}
