package org.roux.window.tabs;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.roux.window.MainWindow;

import java.io.File;
import java.util.List;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class FolderTab extends CustomTab {
    private final DirectoryChooser directoryChooser;
    private final FileChooser fileChooser;

    private final ObservableList<String> folders;

    private final ListView<String> folderView;

    private final ObservableList<String> files;

    private final ListView<String> fileView;

    public FolderTab(final Stage sourceWindow, final String name,
                     final ObservableList<String> sourceFolders,
                     final ObservableList<String> sourceFiles) {
        super(sourceWindow, name);
        folders = sourceFolders;
        files = sourceFiles;
        directoryChooser = new DirectoryChooser();
        fileChooser = new FileChooser();

        folderView = buildFolderView();
        final HBox folderViewButtons = buildFolderViewButtons();

        fileView = buildFileView();
        final HBox fileViewButtons = buildFileViewButtons();

        final VBox root = new VBox(
                new Label(""), // Pour ajouter un retour à la ligne
                new Label("Folders"), folderView, folderViewButtons,
                new Label("Files"), fileView, fileViewButtons);
        root.setSpacing(5);
        setRoot(sourceWindow, root);
    }

    private static ListView<String> buildView(final ObservableList<String> observableList) {
        final ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(observableList);

        return listView;
    }

    public ListView<String> buildFolderView() {
        return buildView(folders);
    }

    private static Button buildAddButton(final EventHandler<MouseEvent> event) {
        return makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 12, event);
    }

    private static Button buildRemoveButton(final ListView<String> listView,
                                            final ObservableList<String> observableList) {
        return makeGraphicButton("remove-icon.png", MainWindow.BUTTON_SIZE - 12, event -> {
            final List<String> selectedItems =
                    listView.getSelectionModel().getSelectedItems();
            observableList.removeAll(selectedItems);
        });
    }

    private static HBox buildButtonBox(final Node... nodes) {
        final HBox buttons = new HBox(nodes);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }

    public HBox buildFolderViewButtons() {
        final Button add = buildAddButton(event -> {
            final File selectedDirectory = directoryChooser.showDialog(sourceWindow);
            if(selectedDirectory != null) {
                folders.add(selectedDirectory.getAbsolutePath());
            }
        });
        final Button remove = buildRemoveButton(folderView, folders);

        return buildButtonBox(add, makeVerticalSeparator(), remove);
    }

    public ListView<String> buildFileView() {
        return buildView(files);
    }

    public HBox buildFileViewButtons() {
        final Button add = buildAddButton(event -> {
            final File selectedExecutable = fileChooser.showOpenDialog(sourceWindow);
            if(selectedExecutable != null && selectedExecutable.canExecute()) {
                files.add(selectedExecutable.getAbsolutePath());
            }
        });
        final Button remove = buildRemoveButton(fileView, files);

        return buildButtonBox(add, makeVerticalSeparator(), remove);

    }
}
