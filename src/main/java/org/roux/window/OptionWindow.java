package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;
import org.roux.game.GameLibrary;
import org.roux.window.tabs.FolderTab;
import org.roux.window.tabs.GameTab;
import org.roux.window.tabs.ParameterTab;

import static org.roux.utils.Utils.makeTextButton;

public class OptionWindow extends Stage {

    private Stage main;
    private Scene scene;
    private VBox root;

    private Button confirmButton;
    private Button cancelButton;
    private HBox confirmOrCancelButtons;

    public OptionWindow(Stage owner, GameLibrary gameLibrary) {
        this.main = owner;
        this.confirmOrCancelButtons = buildConfirmOrCancelButtons();

        TabPane tabPane = new TabPane(
                new FolderTab(this, "Folders", this.confirmButton, this.cancelButton),
                new GameTab(this, "Games", this.confirmButton, this.cancelButton, gameLibrary),
                new ParameterTab(this, "Other", this.confirmButton, this.cancelButton)
        );

        this.root = new VBox(tabPane, confirmOrCancelButtons);
        this.root.setAlignment(Pos.CENTER);
        this.root.setSpacing(5);
        this.root.setPadding(new Insets(10));
        this.root.setPrefSize(360, 480);
        this.root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.scene = new Scene(root);

        JMetro jMetro = new JMetro();
        jMetro.setScene(scene);
        jMetro.setStyle(Style.DARK);

        this.setScene(this.scene);
        this.initOwner(owner);
        this.setTitle("Options");
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
