package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.roux.game.Game;

import java.util.List;

import static org.roux.utils.Utils.*;

public class EditGameWindow extends UndecoratedStage {

    private VBox root;
    private Stage main;
    private Game game;

    // Name
    private final TextField gameName;

    // Path
    private final TextField gamePath;

    // Keywords
    private ListView<String> keywordView;
    private List<String> keywords;
    private HBox keywordButtons;

    // Confirm or cancel
    private HBox confirmOrCancelButtons;

    public EditGameWindow(Stage owner, Button confirmButton, Button cancelButton) {
        this.main = owner;

        this.gameName = buildGameNameField();
        this.gamePath = buildGamePathField();
        this.keywordView = buildKeywordView();
        this.keywordButtons = buildKeywordButtons();
        this.confirmOrCancelButtons = buildConfirmOrCancelButtons();

        confirmButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
        });
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
        });

        this.root = buildRoot(
                new Label("Name"),
                gameName,
                new Label("Path"),
                gamePath,
                new Label("Keywords"),
                keywordView,
                keywordButtons,
                confirmOrCancelButtons);

        this.setOnShowing(event -> this.root.requestFocus());
        this.initOwner(owner);
        this.setRoot(this.root);
    }

    public void edit(Game game, List<String> tableKeywordsRef) {
        this.game = game;

        this.gameName.setText(game.getName());
        this.gamePath.setText(game.getExecutablePath().toString());
        this.keywords = tableKeywordsRef;
        this.keywordView.getItems().setAll(tableKeywordsRef);
        this.show();
    }

    public VBox buildRoot(Node... nodes) {
        VBox root = new VBox(nodes);
        //        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(480, 320);

        return root;
    }

    private TextField buildGameNameField() {
        TextField textField = new TextField();
        textField.setPromptText("Enter the game name");
        textField.setOnKeyReleased(t -> {
            if(t.getCode() == KeyCode.ENTER) {
                this.root.requestFocus();
                t.consume();
            }
        });

        return textField;
    }

    private TextField buildGamePathField() {
        TextField textField = new TextField();
        textField.setPromptText("Select a valid path for the executable");
        textField.setOnKeyReleased(t -> {
            if(t.getCode() == KeyCode.ENTER) {
                this.root.requestFocus();
                t.consume();
            }
        });

        return textField;
    }

    private ListView<String> buildKeywordView() {
        ListView<String> keywordView = new ListView<>();
        keywordView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        keywordView.setEditable(true);
        keywordView.setCellFactory(TextFieldListCell.forListView());
        keywordView.setOnEditCommit(t -> {
            if(t.getNewValue() == null || t.getNewValue().trim().equals("")) {
                keywordView.getItems().remove(t.getIndex());
            } else {
                keywordView.getItems().set(t.getIndex(), t.getNewValue());
            }
        });

        return keywordView;
    }

    private HBox buildKeywordButtons() {
        Button add = makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            this.keywordView.getItems().add("keyword");
        });
        Button remove = makeGraphicButton("remove-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            List<String> items = this.keywordView.getSelectionModel().getSelectedItems();
            this.keywordView.getItems().removeAll(items);
        });

        HBox buttons = new HBox(add, makeVerticalSeparator(), remove);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }

    private HBox buildConfirmOrCancelButtons() {
        Button confirmButton = makeTextButton("    OK    ", event -> {
            this.keywords.clear();
            this.keywords.addAll(this.keywordView.getItems());
            this.close();
        });

        Button cancelButton = makeTextButton(" Cancel ", event -> {
            this.close();
        });

        HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }
}
