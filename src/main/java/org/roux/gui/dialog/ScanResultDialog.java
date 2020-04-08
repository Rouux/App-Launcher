package org.roux.gui.dialog;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.roux.application.ApplicationLibrary;
import org.roux.gui.window.DialogLayout;
import org.roux.gui.window.WindowLayout;
import org.roux.utils.FileManager;
import org.roux.utils.Utils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanResultDialog extends DialogLayout {

    private final static int WINDOW_WIDTH = 720;
    private final static int WINDOW_HEIGHT = 560;

    private final ObservableList<Path> keepListObservable = FXCollections.observableArrayList();
    private final ObservableList<Path> blacklistObservable = FXCollections.observableArrayList();
    private final ListView<Path> keepView;
    private final ListView<Path> blacklistView;
    private final Label warningIdenticalNames;

    public ScanResultDialog(final Stage owner) {
        super(owner);
        keepView = buildResultView();
        blacklistView = buildBlacklistView();
        final HBox resultButtons = buildResultButtons();
        warningIdenticalNames = new Label("");
        warningIdenticalNames.setVisible(false);
        warningIdenticalNames.setFont(Font.font(16));
        warningIdenticalNames.setStyle("-fx-text-fill: red;");
        final VBox root = buildRoot(WINDOW_WIDTH, WINDOW_HEIGHT,
                                    new Label("Keep"),
                                    keepView,
                                    warningIdenticalNames,
                                    resultButtons,
                                    new Label("Discard (blacklist)"),
                                    blacklistView);
        root.setSpacing(10);
    }

    @Override
    protected void onConfirmAction() {
        keepListObservable.forEach(path -> {
            if(ApplicationLibrary.isBlacklisted(path))
                FileManager.getBlacklist().remove(path.toString());
        });
        blacklistObservable.forEach(path -> {
            if(!ApplicationLibrary.isBlacklisted(path))
                FileManager.getBlacklist().add(path.toString());
        });
        close();
    }

    @Override
    protected void onCancelAction() {
        keepListObservable.clear();
        blacklistObservable.clear();
        close();
    }

    public Map<Path, Boolean> seeResultDialog(final List<Path> result,
                                              final List<Path> blacklistedResult) {
        keepListObservable.setAll(result);
        blacklistObservable.setAll(blacklistedResult);
        showAndWait();
        final Map<Path, Boolean> pathToValid = new HashMap<>();
        keepListObservable.forEach(path -> pathToValid.put(path, false));
        blacklistObservable.forEach(path -> pathToValid.put(path, true));
        // Possiblement empty
        return pathToValid;
    }

    private ListView<Path> buildResultView() {
        final ListView<Path> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(keepListObservable);
        listView.setPrefHeight(WindowLayout.WINDOW_MAXIMUM_HEIGHT);
        listView.getStyleClass().add("alternating-row-colors");
        keepListObservable.addListener((Observable newValue) -> {
            final List<Path> paths = (List<Path>) newValue;
            for(int i = 0; i < paths.size() - 1; i++) {
                final String name = ApplicationLibrary.deductName(paths.get(i));
                for(int j = i + 1; j < paths.size(); j++) {
                    if(ApplicationLibrary.deductName(paths.get(j)).equals(name)) {
                        warningIdenticalNames.setVisible(true);
                        warningIdenticalNames.setText(
                                "WARNING : 2 Applications will share the same names");
                        final Tooltip tooltip = new Tooltip(paths.get(i) + " & " + paths.get(j));
                        warningIdenticalNames.setTooltip(tooltip);
                        return;
                    }
                }
            }
            warningIdenticalNames.setText("");
            warningIdenticalNames.setVisible(false);
            warningIdenticalNames.setTooltip(null);
        });

        return listView;
    }

    private ListView<Path> buildBlacklistView() {
        final ListView<Path> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(blacklistObservable);
        listView.setPrefHeight(WindowLayout.WINDOW_MAXIMUM_HEIGHT);
        listView.getStyleClass().add("alternating-row-colors");

        return listView;
    }

    public HBox buildResultButtons() {
        final Button blacklist = Utils.makeGraphicButton("arrow-down.png", BUTTON_SIZE, event -> {
            final List<Path> selectedItems
                    = keepView.getSelectionModel().getSelectedItems();
            blacklistObservable.addAll(selectedItems);
            keepListObservable.removeAll(selectedItems);
            looseFocus();
        });

        final Button keep = Utils.makeGraphicButton("arrow-up.png", BUTTON_SIZE, event -> {
            final List<Path> selectedItems
                    = blacklistView.getSelectionModel().getSelectedItems();
            keepListObservable.addAll(selectedItems);
            blacklistObservable.removeAll(selectedItems);
            looseFocus();
        });

        final HBox buttons = new HBox(blacklist, keep);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }
}
