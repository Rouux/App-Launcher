package org.roux.window.tabs;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.roux.game.Game;
import org.roux.game.GameLibrary;
import org.roux.utils.Utils;
import org.roux.window.EditGameWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.roux.utils.Utils.makeTextButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class GameTab extends CustomTab {

    private final GameLibrary gameLibrary;
    private final EditGameWindow editGameWindow;

    private TableView<Game> gameView;
    private HBox gameViewButtons;

    private final Map<Game, StringPropertyBase> gameToName = new HashMap<>();
    private final Map<Game, List<String>> gameToKeywords = new HashMap<>();

    public GameTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton,
                   GameLibrary gameLibrary) {
        super(sourceWindow, name, confirmButton, cancelButton);
        this.gameLibrary = gameLibrary;
        this.editGameWindow = new EditGameWindow(sourceWindow, confirmButton, cancelButton);
        this.editGameWindow.setOnHidden(event -> {
            gameView.refresh();
        });

        this.gameView = buildGameView();
        this.gameViewButtons = buildGameViewButtons();

        addConfirmButtonEvent(event -> {
            gameToKeywords.forEach(Game::setKeywords);
            gameToName.forEach((game, stringPropertyBase) -> game.setName(stringPropertyBase.get()));
            gameView.refresh();
        });

        addCancelButtonEvent(event -> {
            gameLibrary.getLibrary().forEach(game -> gameToName.put(game, new SimpleStringProperty(game.getName())));
            gameLibrary.getLibrary().forEach(game -> gameToKeywords.put(game, new ArrayList<>(game.getKeywords())));
            gameView.refresh();
        });

        VBox root = new VBox(gameView, gameViewButtons);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setRoot(sourceWindow, root);
    }

    public TableView<Game> buildGameView() {
        TableView<Game> table = new TableView<>();
        table.setEditable(false);
        table.setStyle("-fx-font-size: 12");
        table.setRowFactory(tv -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty() && row.getItem() != null) {
                    Game game = row.getItem();
                    this.editGameWindow.edit(game, this.gameToName.get(game), this.gameToKeywords.get(game));
                }
            });
            return row;
        });
        table.getItems().addListener((Observable observable) -> {
            Utils.autoResizeColumns(table);
        });
        table.getItems().setAll(gameLibrary.getLibrary());

        TableColumn<Game, String> name = buildNameColumn();
        TableColumn<Game, String> keywords = buildKeywordsColumn();
        table.getColumns().setAll(name, keywords);
        return table;
    }

    public TableColumn<Game, String> buildNameColumn() {
        TableColumn<Game, String> column = new TableColumn<>("Name");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> {
            gameToName.computeIfAbsent(data.getValue(), k -> new SimpleStringProperty(data.getValue().getName()));
            return new SimpleStringProperty(gameToName.get(data.getValue()).get());
        });

        return column;
    }

    public TableColumn<Game, String> buildKeywordsColumn() {
        TableColumn<Game, String> column = new TableColumn<>("Keywords");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> {
            gameToKeywords.computeIfAbsent(data.getValue(), k -> new ArrayList<>(data.getValue().getKeywords()));
            return new SimpleStringProperty(gameToKeywords.get(data.getValue()).toString());
        });

        return column;
    }

    public HBox buildGameViewButtons() {
        Button edit = makeTextButton("Edit game", event -> {
            Game game = this.gameView.getSelectionModel().getSelectedItem();
            if(game != null) {
                this.editGameWindow.edit(game, this.gameToName.get(game), this.gameToKeywords.get(game));
            }
        });
        Button remove = makeTextButton("Delete game", event -> {

        });

        Button blacklist = makeTextButton("Add to blacklist", event -> {

        });

        HBox buttons = new HBox(edit, makeVerticalSeparator(), remove, makeVerticalSeparator(), blacklist);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }
}
