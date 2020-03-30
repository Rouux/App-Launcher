package org.roux.window.tabs;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.roux.game.Game;
import org.roux.game.GameLibrary;
import org.roux.window.EditKeywordsWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.roux.utils.Utils.makeTextButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class GameTab extends CustomTab {

    private final GameLibrary gameLibrary;
    private final EditKeywordsWindow editKeywordsWindow;

    private TableView<Game> gameView;
    private HBox gameViewButtons;

    private final Map<Game, List<String>> gameToKeywords = new HashMap<>();

    public GameTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton,
                   GameLibrary gameLibrary) {
        super(sourceWindow, name, confirmButton, cancelButton);
        this.gameLibrary = gameLibrary;
        this.editKeywordsWindow = new EditKeywordsWindow(sourceWindow);

        this.gameView = buildGameView();
        this.gameViewButtons = buildGameViewButtons();

        addConfirmButtonEvent(event -> {
            gameToKeywords.forEach(Game::setKeywords);
            gameView.refresh();
        });

        addCancelButtonEvent(event -> {
            gameLibrary.getLibrary().forEach(game -> gameToKeywords.put(game, new ArrayList<>(game.getKeywords())));
            gameView.refresh();
        });

        VBox root = new VBox(gameView, gameViewButtons);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setContent(root);
    }

    public TableView<Game> buildGameView() {
        TableView<Game> games = new TableView<>(gameLibrary.getLibrary());
        games.setEditable(false);
        games.setStyle("-fx-font-size: 12");

        TableColumn<Game, String> name = new TableColumn<>("Name");
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));

        TableColumn<Game, String> keywords = new TableColumn<>("Keywords");
        keywords.setCellFactory(TextFieldTableCell.forTableColumn());
        keywords.setCellValueFactory(data -> {
            gameToKeywords.computeIfAbsent(data.getValue(), k -> new ArrayList<>(data.getValue().getKeywords()));
            return new SimpleStringProperty(gameToKeywords.get(data.getValue()).toString());
        });

        games.getColumns().setAll(name, keywords);
        return games;
    }

    public HBox buildGameViewButtons() {
        Button edit = makeTextButton("Edit keywords...", event -> {
            Game game = this.gameView.getSelectionModel().getSelectedItem();
            if(game != null) {
                this.editKeywordsWindow.edit(gameView, this.gameToKeywords.get(game));
            }
        });
        Button blacklist = makeTextButton("Add to blacklist", event -> {

        });

        HBox buttons = new HBox(edit, makeVerticalSeparator(), blacklist);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }
}
