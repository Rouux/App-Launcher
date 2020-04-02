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

public class ErrorWindow extends WindowLayout {

    public ErrorWindow(final String message) {
        super();
        final Label label = new Label(message);
        label.setWrapText(true);
        label.setPrefWidth(360);
        label.setPadding(new Insets(10, 5, 5, 10));
        label.setFont(Font.font(14));

        final Button copyButton = buildCopyButton(message);

        final Button confirm = new Button("   Ok   ");
        confirm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> close());
        final HBox buttons = new HBox(copyButton, confirm);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setSpacing(10);

        final VBox vBox = new VBox(label, buttons);
        vBox.setPadding(new Insets(10));
        vBox.setMaxSize(360, 240);
        vBox.setSpacing(20);

        setRoot(vBox);
        setAlwaysOnTop(true);
        getScene().getStylesheets().add("style.css");
        show();
    }

    public ErrorWindow(final Throwable throwable) {
        final StringWriter errorMsg = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errorMsg));
        final Label label = new Label(errorMsg.toString());

        final Label text = new Label("An error has occurred");
        text.setFont(Font.font(14));

        label.setWrapText(true);
        label.setPadding(new Insets(5, 5, 5, 10));
        final ScrollPane scrollPane = new ScrollPane(label);
        scrollPane.setPrefViewportHeight(400);

        final Button saveButton = buildSaveButton(errorMsg.toString());
        final Button copyButton = buildCopyButton(errorMsg.toString());

        final Button confirm = new Button("   Ok   ");
        confirm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> close());
        final HBox buttons = new HBox(saveButton, copyButton, confirm);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setSpacing(10);

        final VBox vBox = new VBox(text, scrollPane, buttons);
        vBox.setPadding(new Insets(10));
        vBox.setPrefSize(360, 320);
        vBox.setSpacing(20);

        setRoot(vBox);
        setAlwaysOnTop(true);
        getScene().getStylesheets().add("style.css");
        show();
    }

    @Override
    protected void onConfirmAction() {
        //@todo see if there's anything logical by pushing ENTER here
    }

    @Override
    protected void onCancelAction() {
        //@todo see if there's anything logical by pushing ESCAPE here
    }

    private Button buildCopyButton(final String error) {
        final Button copyButton = new Button("Copy to clipboard");
        copyButton.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(error);
            clipboard.setContent(content);
            copyButton.setText("Copied !");
        });

        return copyButton;
    }

    private Button buildSaveButton(final String error) {
        final Button saveButton = new Button("Save as file");
        saveButton.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            final FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
            final LocalDateTime now = LocalDateTime.now();
            fileChooser.setInitialFileName("log_" + dtf.format(now));

            //Show save file dialog
            final File file = fileChooser.showSaveDialog(this);
            if(file != null) {
                saveErrorToFile(error, file);
                close();
            }
        });
        return saveButton;
    }

    private static void saveErrorToFile(final String content, final File file) {
        try(final PrintWriter writer = new PrintWriter(file)) {
            writer.println(content);
        } catch(final IOException ex) {
            ex.printStackTrace();
        }
    }

}
