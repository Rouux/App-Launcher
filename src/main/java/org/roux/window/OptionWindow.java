package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.roux.application.ApplicationLibrary;
import org.roux.window.tabs.ApplicationTab;
import org.roux.window.tabs.FolderTab;
import org.roux.window.tabs.ParameterTab;

import static org.roux.utils.Utils.makeTextButton;

public class OptionWindow extends UndecoratedStage {

    private final Stage main;
    private Button confirmButton;
    private Button cancelButton;

    public OptionWindow(final Stage owner, final ApplicationLibrary applicationLibrary) {
        main = owner;
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();

        // AFTER buildConfirmOrCancelButtons !! VERY IMPORTANT YO !!
        final TabPane tabPane = new TabPane(
                new FolderTab(this, "Sources", confirmButton, cancelButton),
                new ApplicationTab(this, "Apps", confirmButton, cancelButton, applicationLibrary),
                new ParameterTab(this, "Other", confirmButton, cancelButton)
        );

        final VBox root = buildRoot(tabPane, confirmOrCancelButtons);

        initOwner(owner);
        setRoot(root);
    }

    private VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(420, 480);
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        return root;
    }

    public HBox buildConfirmOrCancelButtons() {
        confirmButton = makeTextButton("    OK    ", event -> close());

        cancelButton = makeTextButton(" Cancel ", event -> close());

        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }

    @Override
    public void hide() {
        super.hide();
        main.setOpacity(1);
    }
}
