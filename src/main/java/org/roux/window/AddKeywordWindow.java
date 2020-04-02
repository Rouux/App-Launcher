package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static org.roux.utils.Utils.makeTextButton;

public class AddKeywordWindow extends WindowLayout {

    private final static int WINDOW_WIDTH = 260;
    private final static int WINDOW_HEIGHT = 100;

    private final VBox root;
    private final TextField keywordField;
    private ListView<String> keywordView;

    public AddKeywordWindow(final Stage owner) {
        keywordField = buildNameField();
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();
        root = buildRoot(keywordField, confirmOrCancelButtons);

        setOnShowing(event -> keywordField.requestFocus());
        initOwner(owner);
        setRoot(root);
    }

    @Override
    protected void onConfirmAction() {
        if(keywordField.getText().trim().length() > 0)
            keywordView.getItems().add(keywordField.getText());
        close();
    }

    @Override
    protected void onCancelAction() {
        close();
    }

    public void open(final ListView<String> keywordView) {
        this.keywordView = keywordView;
        keywordField.setText("");
        show();
    }

    private VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setSpacing(20);
        root.setPadding(new Insets(20, 10, 10, 10));
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        return root;
    }

    private TextField buildNameField() {
        final TextField textField = new TextField();
        textField.setPromptText("Keyword (case sensitive)");
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
}
