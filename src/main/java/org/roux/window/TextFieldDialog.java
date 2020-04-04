package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

import static org.roux.utils.Utils.makeTextButton;

public class TextFieldDialog extends WindowLayout {

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
        textField = buildTextField();
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();
        root = buildRoot(textField, confirmOrCancelButtons);

        setOnShowing(event -> textField.requestFocus());
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setRoot(root);
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

    private VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setSpacing(20);
        root.setPadding(new Insets(20, 10, 10, 10));
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        return root;
    }

    private TextField buildTextField() {
        final TextField textField = new TextField();
        textField.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                root.requestFocus();
                keyEvent.consume();
            }
        });
        return textField;
    }

    private HBox buildConfirmOrCancelButtons() {
        final Button confirmButton = makeTextButton("    OK    ", event -> onConfirmAction());
        final Button cancelButton = makeTextButton(" Cancel ", event -> onCancelAction());

        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }

    public void setPrompt(final String prompt) {
        textField.setPromptText(prompt);
    }

}
