package org.roux.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.roux.utils.FileManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.roux.utils.Utils.makeTextButton;

public class ScanResultDialog extends DialogLayout {

    private final static int WINDOW_WIDTH = 720;
    private final static int WINDOW_HEIGHT = 480;

    private final ObservableList<Path> keepListObservable = FXCollections.observableArrayList();
    private final ObservableList<Path> blacklistObservable = FXCollections.observableArrayList();
    private final ListView<Path> keepView;
    private final ListView<Path> blacklistView;

    public ScanResultDialog(final Stage owner) {
        super(owner);
        keepView = buildResultView();
        blacklistView = buildBlacklistView();
        final HBox resultButtons = buildResultButtons();
        buildRoot(WINDOW_WIDTH, WINDOW_HEIGHT,
                  new Label("Keep"),
                  keepView, resultButtons,
                  new Label("Discard (blacklist)"),
                  blacklistView);
    }

    @Override
    protected void onConfirmAction() {
        blacklistObservable.forEach(path -> {
            if(!FileManager.getBlacklist().contains(path.toString()))
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

    public Map<Path, Boolean> seeResultDialog(final List<Path> result) {
        keepListObservable.setAll(result);
        blacklistObservable.clear();
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
        final Button blacklist = makeTextButton("V", event -> {
            final List<Path> selectedItems
                    = keepView.getSelectionModel().getSelectedItems();
            blacklistObservable.addAll(selectedItems);
            keepListObservable.removeAll(selectedItems);
            looseFocus();
        });

        final Button keep = makeTextButton("^", event -> {
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
