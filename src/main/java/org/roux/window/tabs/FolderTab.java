package org.roux.window.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.roux.utils.FileManager;
import org.roux.window.MainWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class FolderTab extends CustomTab {

    private final ObservableList<String> folders = FXCollections.observableList(new ArrayList<>());
    private final List<String> foldersLastUpdate = new ArrayList<>();
    private final DirectoryChooser directoryChooser;

    private ListView<String> folderView;
    private HBox folderViewButtons;

    public FolderTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton) {
        super(sourceWindow, name, confirmButton, cancelButton);
        this.directoryChooser = new DirectoryChooser();
        this.folderView = buildFolderView();
        this.folderViewButtons = buildFolderViewButtons();

        addConfirmButtonEvent(event -> {
            FileManager.updateFolders(this.folders);
            this.foldersLastUpdate.clear();
            this.foldersLastUpdate.addAll(this.folders);
        });

        addCancelButtonEvent(event -> {
            this.folders.setAll(this.foldersLastUpdate);
        });

        VBox root = new VBox(folderView, folderViewButtons);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setContent(root);
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
