package org.roux.window.tabs;

import javafx.collections.FXCollections;
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
import org.roux.application.ApplicationLibrary;
import org.roux.utils.FileManager;
import org.roux.window.MainWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.roux.utils.Utils.*;

public class BlacklistTab extends CustomTab {
    private final ApplicationLibrary applicationLibrary;
    private final DirectoryChooser directoryChooser;
    private final FileChooser fileChooser;

    private final ObservableList<String> blacklistedFiles =
            FXCollections.observableList(new ArrayList<>());
    private final List<String> initialFiles = new ArrayList<>();

    private final ListView<String> blacklistView;

    private final ObservableList<String> bannedFiles = FXCollections.observableList(
            new ArrayList<>());
    private final List<String> startingFiles = new ArrayList<>();

    private final ListView<String> banView;

    public BlacklistTab(final Stage sourceWindow, final String name, final Button confirmButton,
                        final Button cancelButton, final ApplicationLibrary applicationLibrary) {
        super(sourceWindow, name, confirmButton, cancelButton);
        this.applicationLibrary = applicationLibrary;
        directoryChooser = new DirectoryChooser();
        fileChooser = new FileChooser();

        blacklistView = buildBlacklistView();
        final HBox blacklistButtons = buildBlacklistButtons();

        banView = buildFileView();
        final HBox fileViewButtons = buildFileViewButtons();

        onOptionConfirm(event -> {
            FileManager.setBlacklist(blacklistedFiles);
            initialFiles.clear();
            initialFiles.addAll(blacklistedFiles);

            //            FileManager.setExecutables(bannedFiles);
            //            startingFiles.clear();
            //            startingFiles.addAll(bannedFiles);
        });

        onOptionCancel(event -> {
            blacklistedFiles.setAll(initialFiles);
            //            bannedFiles.setAll(startingFiles);
        });

        final VBox root = new VBox(new Label(""), // Pour ajouter un retour a la ligne
                                   new Label("Blacklist"),
                                   blacklistView,
                                   blacklistButtons
                                   //                                   ,new Label("Banned"),
                                   //                                   banView,
                                   //                                   fileViewButtons
        );
        root.setSpacing(5);
        setRoot(sourceWindow, root);
    }

    private static ListView<String> buildView(final List<String> source,
                                              final List<String> initialList,
                                              final ObservableList<String> currentList) {
        final ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initialList.addAll(source);
        currentList.addAll(source);
        listView.setItems(currentList);

        return listView;
    }

    public ListView<String> buildBlacklistView() {
        return buildView(FileManager.getBlacklist(), initialFiles, blacklistedFiles);
    }

    private static Button buildAddButton(final EventHandler<MouseEvent> event) {
        return makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 12, event);
    }

    private static Button buildRemoveButton(final ListView<String> listView,
                                            final ObservableList<String> observableList) {
        return makeGraphicButton("remove-icon.png", MainWindow.BUTTON_SIZE - 12, event -> {
            final List<String> selectedItems = listView.getSelectionModel().getSelectedItems();
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

    public HBox buildBlacklistButtons() {
        final Button addFolder = makeTextButton("Add folder", event -> {
            final File selectedDirectory = directoryChooser.showDialog(sourceWindow);
            if(selectedDirectory != null) {
                blacklistedFiles.add(selectedDirectory.getAbsolutePath());
            }
        });

        final Button addFile = makeTextButton("Add file", event -> {
            final File selectedFile = fileChooser.showOpenDialog(sourceWindow);
            if(selectedFile != null) {
                blacklistedFiles.add(selectedFile.getAbsolutePath());
            }
        });

        final Button remove = buildRemoveButton(blacklistView, blacklistedFiles);

        return buildButtonBox(addFolder, makeVerticalSeparator(),
                              addFile, makeVerticalSeparator(),
                              remove);
    }

    public ListView<String> buildFileView() {
        return buildView(FileManager.getExecutables(), startingFiles, bannedFiles);
    }

    public HBox buildFileViewButtons() {
        final Button add = buildAddButton(event -> {
            final File selectedExecutable = fileChooser.showOpenDialog(sourceWindow);
            if(selectedExecutable != null && selectedExecutable.canExecute()) {
                bannedFiles.add(selectedExecutable.getAbsolutePath());
            }
        });
        final Button remove = buildRemoveButton(banView, bannedFiles);

        return buildButtonBox(add, makeVerticalSeparator(), remove);

    }
}
