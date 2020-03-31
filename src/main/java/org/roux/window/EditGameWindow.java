package org.roux.window;

import javafx.beans.property.StringPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.roux.game.Game;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.roux.utils.Utils.*;

public class EditGameWindow extends UndecoratedStage {

    private final static int WINDOW_WIDTH = 480;
    private final static int WINDOW_HEIGHT = 320;

    private VBox root;
    private Stage main;
    private Game game;

    // Name
    private final TextField gameName;
    private StringPropertyBase gameNameProperty;

    // Path
    private final Map<Game, String> gameToPath;
    private TextField gamePath;
    private Button gameSelectFile;

    // Keywords
    private ListView<String> keywordView;
    private List<String> keywords;
    private HBox keywordButtons;

    // Confirm or cancel
    private HBox confirmOrCancelButtons;

    public EditGameWindow(Stage owner, Button confirmButton, Button cancelButton) {
        this.main = owner;
        this.gameToPath = new HashMap<>();

        this.gameName = buildGameNameField();
        HBox pathOptions = buildGamePathOptions();
        this.keywordView = buildKeywordView();
        this.keywordButtons = buildKeywordButtons();
        this.confirmOrCancelButtons = buildConfirmOrCancelButtons();

        confirmButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.gameToPath.forEach((game, path) -> {
                game.setExecutablePath(Paths.get(path));
            });
            this.gameToPath.clear();
        });

        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.gameToPath.clear();
        });

        this.root = buildRoot(
                new Label("Name"),
                gameName,
                new Label("Path"),
                pathOptions,
                new Label("Keywords"),
                keywordView,
                keywordButtons,
                confirmOrCancelButtons);

        this.setOnShowing(event -> this.root.requestFocus());
        this.initOwner(owner);
        this.setRoot(this.root);
    }

    public void edit(Game game, StringPropertyBase nameProperty, List<String> tableKeywordsRef) {
        this.game = game;
        this.gameNameProperty = nameProperty;
        this.gameName.setText(nameProperty.get());

        this.gameToPath.computeIfAbsent(game, value -> game.getExecutablePath().toString());
        this.gamePath.setText(this.gameToPath.get(game));

        this.keywords = tableKeywordsRef;
        this.keywordView.getItems().setAll(tableKeywordsRef);
        this.show();
    }

    public VBox buildRoot(Node... nodes) {
        VBox root = new VBox(nodes);
        //        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

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
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.gameNameProperty.set(newValue);
        });

        return textField;
    }

    private HBox buildGamePathOptions() {
        this.gamePath = new TextField();
        this.gamePath.setPromptText("Select a valid path for the application's executable");
        this.gamePath.setPrefWidth(WINDOW_WIDTH);
        this.gamePath.setOnKeyReleased(t -> {
            if(t.getCode() == KeyCode.ENTER) {
                this.root.requestFocus();
                t.consume();
            }
        });
        this.gamePath.textProperty().addListener((observable, oldValue, newValue) -> {
            if(game != null)
                this.gameToPath.put(game, newValue);
        });

        FileChooser fileChooser = new FileChooser();
        this.gameSelectFile = makeTextButton("...", event -> {
            File currentFile = new File(this.gamePath.getText());
            if(currentFile.isFile()) {
                fileChooser.setInitialDirectory(currentFile.getParentFile());
                File chosenFile = fileChooser.showOpenDialog(this);
                if(chosenFile != null && chosenFile.exists())
                    this.gamePath.setText(chosenFile.getAbsolutePath());
            }
        });

        HBox hBox = new HBox(this.gamePath, this.gameSelectFile);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
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
