package org.roux.window;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.roux.FileManager;
import org.roux.game.Game;
import org.roux.game.GameLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.roux.utils.Utils.*;

public class OptionWindow extends Stage {

    private final GameLibrary gameLibrary;
    private final EditKeywordsWindow editKeywordsWindow;

    private final ObservableList<String> folders = FXCollections.observableList(new ArrayList<>());
    private final List<String> foldersLastUpdate = new ArrayList<>();

    private final DirectoryChooser directoryChooser;
    private Stage main;
    private Scene scene;
    private VBox root;

    private ListView<String> folderView;
    private HBox folderViewButtons;
    private TableView<Game> gameView;
    private HBox gameViewButtons;
    private HBox confirmOrCancelButtons;

    public OptionWindow(Stage owner, GameLibrary gameLibrary) {
        System.out.println("hihihi");
        this.main = owner;
        this.gameLibrary = gameLibrary;
        this.editKeywordsWindow = new EditKeywordsWindow(this);
        this.directoryChooser = new DirectoryChooser();

        this.folderView = buildFolderView();
        this.folderViewButtons = buildFolderViewButtons();
        this.gameView = buildGameView();
        this.gameViewButtons = buildGameViewButtons();
        this.confirmOrCancelButtons = buildConfirmOrCancelButtons();

        this.root = new VBox(this.folderView,
                             this.folderViewButtons,
                             this.gameView,
                             this.gameViewButtons,
                             this.confirmOrCancelButtons);
        this.root.setAlignment(Pos.CENTER);
        this.root.setSpacing(5);
        this.root.setPadding(new Insets(10));
        this.root.setPrefSize(360, 480);

        this.scene = new Scene(this.root);
        JMetro jMetro = new JMetro(scene, Style.DARK);
        jMetro.setAutomaticallyColorPanes(true);

        this.setScene(this.scene);
        this.initOwner(owner);
        this.setTitle("Options");
    }

    public TableView<Game> buildGameView() {
        TableView<Game> games = new TableView<>(FXCollections.observableList(gameLibrary.getLibrary()));
        games.setEditable(false);
        games.setStyle("-fx-font-size: 11");
        TableColumn<Game, String> name = new TableColumn<>("Name");
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        TableColumn<Game, String> keywords = new TableColumn<>("Keywords");
        keywords.setCellFactory(TextFieldTableCell.forTableColumn());
        keywords.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getKeywords().toString()));
        games.getColumns().addAll(name, keywords);

        return games;
    }

    public HBox buildGameViewButtons() {
        Button edit = makeTextButton("Edit keywords...", event -> {
            List<Game> games = this.gameView.getSelectionModel().getSelectedItems();
            if(games != null && !games.isEmpty()) {
                this.editKeywordsWindow.edit(games.get(0));
            }
        });
        Button ban = makeTextButton("Ban application", event -> {

        });

        HBox buttons = new HBox(edit, makeVerticalSeparator(), ban);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }

    public ListView<String> buildFolderView() {
        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> strings = FileManager.getFolders();
        listView.setItems(this.folders);
        this.folders.addAll(strings);
        this.foldersLastUpdate.addAll(this.folders);

        return listView;
    }

    public HBox buildFolderViewButtons() {
        Button add = makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            File selectedDirectory = this.directoryChooser.showDialog(this);
            if(selectedDirectory != null) {
                this.folders.add(selectedDirectory.getAbsolutePath());
            }
        });
        Button remove = makeGraphicButton("remove-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            List<String> selectedItems = this.folderView.getSelectionModel().getSelectedItems();
            this.folders.removeAll(selectedItems);
        });

        HBox buttons = new HBox(add, makeVerticalSeparator(), remove);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }

    public HBox buildConfirmOrCancelButtons() {
        Button confirm = makeTextButton("    OK    ", event -> {
            FileManager.updateFolders(this.folders);
            this.clearAll();
            this.foldersLastUpdate.addAll(this.folders);
            this.close();
        });

        Button cancel = makeTextButton(" Cancel ", event -> {
            // Revert changes
            this.folders.setAll(this.foldersLastUpdate);
            this.clearAll();
            this.close();
        });

        HBox confirmOrCancel = new HBox(confirm, cancel);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }

    public void clearAll() {
        this.foldersLastUpdate.clear();
    }

    @Override
    public void hide() {
        System.out.println("ici alors ??");
        super.hide();
        this.main.setOpacity(1);
    }
}
