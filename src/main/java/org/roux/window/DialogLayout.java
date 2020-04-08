package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static org.roux.utils.Utils.makeTextButton;

public abstract class DialogLayout extends WindowLayout {
    protected final Button confirmButton;
    protected final Button cancelButton;

    public DialogLayout(final Stage owner) {
        confirmButton = buildConfirmButton();
        cancelButton = buildCancelButton();
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
    }

    protected final VBox buildRoot(final double width, final double height, final Node... nodes) {
        final VBox root = new VBox(nodes);
        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        root.getChildren().add(confirmOrCancel);
        root.setSpacing(20);
        root.setPadding(new Insets(20, 10, 10, 10));
        root.setPrefSize(width, height);
        setRoot(root);
        return root;
    }

    private Button buildConfirmButton() {
        return makeTextButton("    OK    ", event -> onConfirmAction());
    }


    private Button buildCancelButton() {
        return makeTextButton(" Cancel ", event -> onCancelAction());
    }

    protected final void looseFocus() {
        getScene().getRoot().requestFocus();
    }
}
