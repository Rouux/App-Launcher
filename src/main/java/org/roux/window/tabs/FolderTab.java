package org.roux.window.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.roux.FileManager;
import org.roux.window.MainWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class FolderTab extends Tab {

    private final ObservableList<String> folders = FXCollections.observableList(new ArrayList<>());
    private final List<String> foldersLastUpdate = new ArrayList<>();
    private final DirectoryChooser directoryChooser;

    private final Stage sourceWindow;

    private ListView<String> folderView;
    private HBox folderViewButtons;

    public FolderTab(Stage sourceWindow) {
        this.sourceWindow = sourceWindow;
        this.directoryChooser = new DirectoryChooser();
        this.folderView = buildFolderView();
        this.folderViewButtons = buildFolderViewButtons();
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
            File selectedDirectory = this.directoryChooser.showDialog(this.sourceWindow);
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
}
