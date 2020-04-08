package org.roux.gui.tab;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.roux.application.Application;
import org.roux.gui.window.SearchWindow;
import org.roux.gui.window.WindowLayout;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.roux.utils.Utils.*;

public class BlacklistTab extends CustomTab {
    private final DirectoryChooser directoryChooser;
    private final FileChooser fileChooser;

    private final ObservableList<String> blacklist;
    private final ObservableList<Application> applications;
    private final ListView<String> blacklistView;

    public BlacklistTab(final Stage sourceWindow, final String name,
                        final ObservableList<String> blacklist,
                        final ObservableList<Application> applications) {
        super(sourceWindow, name);
        this.blacklist = blacklist;
        this.applications = applications;
        directoryChooser = new DirectoryChooser();
        fileChooser = new FileChooser();

        blacklistView = buildBlacklistView();
        blacklistView.getStyleClass().add("alternating-row-colors");
        final HBox blacklistButtons = buildBlacklistButtons();

        final VBox root = new VBox(new Label(""), // Pour ajouter un retour a la ligne
                                   new Label("Blacklist"),
                                   blacklistView,
                                   blacklistButtons
        );
        root.setSpacing(5);
        setRoot(root);
    }

    private static ListView<String> buildView(final ObservableList<String> observableList) {
        final ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(observableList);
        listView.setPrefHeight(WindowLayout.WINDOW_MAXIMUM_HEIGHT);

        return listView;
    }

    public ListView<String> buildBlacklistView() {
        return buildView(blacklist);
    }

    private Button buildRemoveButton(final ListView<String> listView,
                                     final ObservableList<String> observableList) {
        return makeGraphicButton("remove-icon.png", SearchWindow.BUTTON_SIZE - 12, event -> {
            final List<String> selectedItems = listView.getSelectionModel().getSelectedItems();
            for(final String path : selectedItems) {
                final Application application = getApplicationFromPath(path);
                if(application != null) application.setBlacklisted(false);
            }
            observableList.removeAll(selectedItems);
            looseFocus();
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
                blacklist.add(selectedDirectory.getAbsolutePath());
                final List<Application> applications
                        = getApplicationsFromPath(selectedDirectory.getAbsolutePath());
                applications.forEach(app -> app.setBlacklisted(true));
            }
            looseFocus();
        });

        final Button addFile = makeTextButton("Add file", event -> {
            final File selectedFile = fileChooser.showOpenDialog(sourceWindow);
            if(selectedFile != null) {
                blacklist.add(selectedFile.getAbsolutePath());
                final Application application =
                        getApplicationFromPath(selectedFile.getAbsolutePath());
                if(application != null) application.setBlacklisted(true);
            }
            looseFocus();
        });

        final Button remove = buildRemoveButton(blacklistView, blacklist);

        return buildButtonBox(addFolder, makeVerticalSeparator(),
                              addFile, makeVerticalSeparator(),
                              remove);
    }

    private Application getApplicationFromPath(final String path) {
        return applications.stream()
                .filter(app -> app.getExecutablePath().toString().equals(path))
                .findFirst()
                .orElse(null);
    }

    private List<Application> getApplicationsFromPath(final String path) {
        return applications.stream()
                .filter(app -> app.getExecutablePath().startsWith(path))
                .collect(Collectors.toList());
    }
}
