package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorWindow extends UndecoratedStage {

    public ErrorWindow(String message) {
        super();
        Label label = new Label(message);
        label.setWrapText(true);
        label.setPrefWidth(360);
        label.setPadding(new Insets(10, 5, 5, 10));
        label.setFont(Font.font(14));

        Button copyButton = buildCopyButton(message);

        Button confirm = new Button("   Ok   ");
        confirm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.close();
        });
        HBox buttons = new HBox(copyButton, confirm);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setSpacing(10);

        VBox vBox = new VBox(label, buttons);
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

        Label text = new Label("An error has occurred");
        text.setFont(Font.font(14));

        label.setWrapText(true);
        label.setPadding(new Insets(5, 5, 5, 10));
        ScrollPane scrollPane = new ScrollPane(label);
        scrollPane.setPrefViewportHeight(400);

        Button saveButton = buildSaveButton(errorMsg.toString());
        Button copyButton = buildCopyButton(errorMsg.toString());

        Button confirm = new Button("   Ok   ");
        confirm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.close();
        });
        HBox buttons = new HBox(saveButton, copyButton, confirm);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setSpacing(10);

        VBox vBox = new VBox(text, scrollPane, buttons);
        vBox.setPadding(new Insets(10));
        vBox.setPrefSize(360, 320);
        vBox.setSpacing(20);

        this.setRoot(vBox);
        this.setAlwaysOnTop(true);
        getScene().getStylesheets().add("style.css");
        this.show();
    }

    private Button buildCopyButton(String error) {
        Button copyButton = new Button("Copy to clipboard");
        copyButton.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(error);
            clipboard.setContent(content);
            copyButton.setText("Copied !");
        });

        return copyButton;
    }

    private Button buildSaveButton(String error) {
        Button saveButton = new Button("Save as file");
        saveButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
            fileChooser.setInitialFileName("log_" + dtf.format(now));

            //Show save file dialog
            File file = fileChooser.showSaveDialog(this);
            if(file != null) {
                saveErrorToFile(error, file);
                this.close();
            }
        });
        return saveButton;
    }

    private static void saveErrorToFile(String content, File file) {
        try(PrintWriter writer = new PrintWriter(file)) {
            writer.println(content);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

}
