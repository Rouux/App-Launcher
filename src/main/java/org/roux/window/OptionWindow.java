package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.roux.game.GameLibrary;
import org.roux.window.tabs.FolderTab;
import org.roux.window.tabs.GameTab;
import org.roux.window.tabs.ParameterTab;

import static org.roux.utils.Utils.makeTextButton;

public class OptionWindow extends UndecoratedStage {

    private Stage main;
    private Scene scene;
    private VBox root;

    private Button confirmButton;
    private Button cancelButton;
    private HBox confirmOrCancelButtons;

    public OptionWindow(Stage owner, GameLibrary gameLibrary) {
        this.main = owner;
        this.confirmOrCancelButtons = buildConfirmOrCancelButtons();

        // AFTER buildConfirmOrCancelButtons !! VERY IMPORTANT YO !!
        TabPane tabPane = new TabPane(
                new FolderTab(this, "Sources", this.confirmButton, this.cancelButton),
                new GameTab(this, "Games", this.confirmButton, this.cancelButton, gameLibrary),
                new ParameterTab(this, "Other", this.confirmButton, this.cancelButton)
        );

        this.root = buildRoot(tabPane, buildConfirmOrCancelButtons());

        this.initOwner(owner);
        this.setRoot(this.root);
    }

    private VBox buildRoot(Node... nodes) {
        VBox root = new VBox(nodes);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(360, 480);
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        return root;
    }

    public HBox buildConfirmOrCancelButtons() {
        this.confirmButton = makeTextButton("    OK    ", event -> {
            this.close();
        });

        this.cancelButton = makeTextButton(" Cancel ", event -> {
            this.close();
        });

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
