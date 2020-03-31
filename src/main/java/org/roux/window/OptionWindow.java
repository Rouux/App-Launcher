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

    private Stage main;
    private Button confirmButton;
    private Button cancelButton;

    public OptionWindow(Stage owner, ApplicationLibrary applicationLibrary) {
        this.main = owner;
        HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();

        // AFTER buildConfirmOrCancelButtons !! VERY IMPORTANT YO !!
        TabPane tabPane = new TabPane(
                new FolderTab(this, "Sources", this.confirmButton, this.cancelButton),
                new ApplicationTab(this, "Apps", this.confirmButton, this.cancelButton, applicationLibrary),
                new ParameterTab(this, "Other", this.confirmButton, this.cancelButton)
        );

        VBox root = buildRoot(tabPane, confirmOrCancelButtons);

        this.initOwner(owner);
        this.setRoot(root);
    }

    private VBox buildRoot(Node... nodes) {
        VBox root = new VBox(nodes);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(420, 480);
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        return root;
    }

    public HBox buildConfirmOrCancelButtons() {
        this.confirmButton = makeTextButton("    OK    ", event -> this.close());

        this.cancelButton = makeTextButton(" Cancel ", event -> this.close());

        HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }

    @Override
    public void hide() {
        super.hide();
        this.main.setOpacity(1);
    }
}
