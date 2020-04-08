package org.roux.gui.dialog;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.roux.gui.window.DialogLayout;

import java.util.Optional;

public class TextFieldDialog extends DialogLayout {

    private final static int WINDOW_WIDTH = 280;
    private final static int WINDOW_HEIGHT = 120;

    private final VBox root;
    private final TextField textField;

    public TextFieldDialog(final Stage owner, final String title, final String prompt) {
        this(owner, title);
        textField.setPromptText(prompt);
    }

    public TextFieldDialog(final Stage owner, final String title) {
        this(owner);
        setTitle(title);
    }

    public TextFieldDialog(final Stage owner) {
        super(owner);
        textField = buildTextField();
        root = buildRoot(WINDOW_WIDTH, WINDOW_HEIGHT, textField);
        setOnShowing(event -> textField.requestFocus());
    }

    @Override
    protected void onConfirmAction() {
        close();
    }

    @Override
    protected void onCancelAction() {
        textField.setText("");
        close();
    }

    public Optional<String> openDialog() {
        textField.setText("");
        showAndWait();
        final String result = textField.getText();
        return Optional.ofNullable(result.isBlank() ? null : result);
    }

    private TextField buildTextField() {
        final TextField textField = new TextField();
        textField.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                looseFocus();
                keyEvent.consume();
            }
        });
        return textField;
    }

    public void setPrompt(final String prompt) {
        textField.setPromptText(prompt);
    }

}
