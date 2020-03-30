package org.roux.window.tabs;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
        TableView<Game> table = new TableView<>(gameLibrary.getLibrary());
        table.setEditable(false);
        table.setStyle("-fx-font-size: 12");
        table.setRowFactory(tv -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty() && row.getItem() != null) {
                    this.editKeywordsWindow.edit(gameView, this.gameToKeywords.get(row.getItem()));
                }
            });
            return row;
        });
        table.getItems().addListener((Observable observable) -> {
            autoResizeColumns(table);
        });

        TableColumn<Game, String> name = buildNameColumn();
        TableColumn<Game, String> keywords = buildKeywordsColumn();
        table.getColumns().setAll(name, keywords);
        return table;
    }

    public static void autoResizeColumns(TableView<?> table) {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach((column) -> {
            Text t = new Text(column.getText());
            double max = t.getLayoutBounds().getWidth();
            for(int i = 0; i < table.getItems().size(); i++) {
                if(column.getCellData(i) != null) {
                    t = new Text(column.getCellData(i).toString());
                    double calcwidth = t.getLayoutBounds().getWidth() * 1.1;
                    if(calcwidth > max) {
                        max = calcwidth;
                    }
                }
            }
            column.setMinWidth(max + 10.0d);
        });
    }

    public TableColumn<Game, String> buildNameColumn() {
        TableColumn<Game, String> column = new TableColumn<>("Name");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));

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
