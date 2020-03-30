package org.roux.window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.roux.game.Game;

import java.util.List;

import static org.roux.utils.Utils.*;

//@todo fix cette merde pas super utile de toute façon mais bon faut fix
public class EditKeywordsWindow extends Stage {

    private Stage main;
    private Scene scene;
    private VBox root;

    private TableView<Game> gameView;
    private List<String> keywords;
    private ListView<String> keywordView = new ListView<>();
    private HBox keywordButtons;
    private Button confirmButton;
    private Button cancelButton;
    private HBox confirmOrCancelButtons;

    public EditKeywordsWindow(Stage owner) {
        this.main = owner;

        keywordView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        keywordView.setEditable(true);
        keywordView.setCellFactory(TextFieldListCell.forListView());
        keywordView.setOnEditCommit(t -> {
            if(t.getNewValue() == null || t.getNewValue().trim().equals("")) {
                keywordView.getItems().remove(t.getIndex());
            } else {
                keywordView.getItems().set(t.getIndex(), t.getNewValue());
            }
            System.out.println("setOnEditCommit");
        });
        keywordView.setOnEditCancel(t -> System.out.println("setOnEditCancel"));

        this.keywordButtons = buildKeywordButtons();
        this.confirmOrCancelButtons = buildConfirmOrCancelButtons();

        this.root = new VBox(this.keywordView, this.keywordButtons, confirmOrCancelButtons);
        this.root.setAlignment(Pos.CENTER);
        this.root.setSpacing(5);
        this.root.setPadding(new Insets(10));
        this.root.setPrefSize(360, 240);

        this.scene = new Scene(this.root);
        JMetro jMetro = new JMetro(scene, Style.DARK);
        jMetro.setAutomaticallyColorPanes(true);

        this.initOwner(owner);
        this.setScene(this.scene);
        this.initStyle(StageStyle.UNDECORATED);
    }

    public void edit(TableView<Game> gameView, List<String> keywords) {
        this.gameView = gameView;
        this.keywords = keywords;
        this.keywordView.getItems().setAll(keywords);
        this.show();
    }

    public HBox buildKeywordButtons() {
        Button add = makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            this.keywordView.getItems().add("[Enter your value here]");
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

    public HBox buildConfirmOrCancelButtons() {
        this.confirmButton = makeTextButton("    OK    ", event -> {
            this.keywords.clear();
            this.keywords.addAll(this.keywordView.getItems());
            gameView.refresh();
            this.close();
        });

        this.cancelButton = makeTextButton(" Cancel ", event -> {
            // Revert changes ???
            gameView.refresh();
            this.close();
        });

        HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }
}
