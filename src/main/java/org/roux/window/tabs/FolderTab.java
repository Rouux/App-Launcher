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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.roux.utils.FileManager;
import org.roux.window.MainWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class FolderTab extends CustomTab {
    private final DirectoryChooser directoryChooser;
    private final FileChooser fileChooser;

    private final ObservableList<String> folders = FXCollections.observableList(new ArrayList<>());
    private final List<String> startingFolders = new ArrayList<>();

    private ListView<String> folderView;
    private HBox folderViewButtons;

    private final ObservableList<String> files = FXCollections.observableList(new ArrayList<>());
    private final List<String> startingFiles = new ArrayList<>();

    private ListView<String> fileView;
    private HBox fileViewButtons;

    public FolderTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton) {
        super(sourceWindow, name, confirmButton, cancelButton);
        this.directoryChooser = new DirectoryChooser();
        this.fileChooser = new FileChooser();

        this.folderView = buildFolderView();
        this.folderViewButtons = buildFolderViewButtons();

        this.fileView = buildFileView();
        this.fileViewButtons = buildFileViewButtons();

        addConfirmButtonEvent(event -> {
            FileManager.setFolders(this.folders);
            this.startingFolders.clear();
            this.startingFolders.addAll(this.folders);

            FileManager.setExecutables(this.files);
            this.startingFiles.clear();
            this.startingFiles.addAll(this.files);
        });

        addCancelButtonEvent(event -> {
            this.folders.setAll(this.startingFolders);
            this.files.setAll(this.startingFiles);
        });

        VBox root = new VBox(folderView, folderViewButtons, fileView, fileViewButtons);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setRoot(sourceWindow, root);
    }

    public ListView<String> buildFolderView() {
        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> strings = FileManager.getFolders();
        this.startingFolders.addAll(strings);
        this.folders.addAll(strings);
        listView.setItems(this.folders);

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

    public ListView<String> buildFileView() {
        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> strings = FileManager.getExecutables();
        this.startingFiles.addAll(strings);
        this.files.addAll(strings);
        listView.setItems(this.files);

        return listView;
    }

    public HBox buildFileViewButtons() {
        Button add = makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            File selectedExecutable = this.fileChooser.showOpenDialog(this.sourceWindow);
            if(selectedExecutable != null && selectedExecutable.canExecute()) {
                this.files.add(selectedExecutable.getAbsolutePath());
            }
        });
        Button remove = makeGraphicButton("remove-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            List<String> selectedItems = this.folderView.getSelectionModel().getSelectedItems();
            this.files.removeAll(selectedItems);
        });

        HBox buttons = new HBox(add, makeVerticalSeparator(), remove);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }
}
