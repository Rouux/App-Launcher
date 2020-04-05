package org.roux.window;

import javafx.collections.FXCollections;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.roux.utils.FileManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.roux.utils.Utils.makeTextButton;

public class ScanResultDialog extends WindowLayout {

    private final static int WINDOW_WIDTH = 720;
    private final static int WINDOW_HEIGHT = 480;

    private final VBox root;
    private final ObservableList<Path> keepListObservable = FXCollections.observableArrayList();
    private final ObservableList<Path> blacklistObservable = FXCollections.observableArrayList();
    private final ListView<Path> keepView;
    private final ListView<Path> blacklistView;

    private Button confirmButton;
    private Button cancelButton;

    public ScanResultDialog(final Stage owner) {
        keepView = buildResultView();
        blacklistView = buildBlacklistView();
        final HBox resultButtons = buildResultButtons();
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();
        root = buildRoot(new Label("Executables you want to keep"),
                         keepView, resultButtons,
                         new Label("Executables you want to blacklist"),
                         blacklistView,
                         confirmOrCancelButtons);

        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setRoot(root);
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

    private VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setSpacing(10);
        root.setPadding(new Insets(20, 10, 10, 10));
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        return root;
    }

    private HBox buildConfirmOrCancelButtons() {
        confirmButton = makeTextButton("    OK    ", event -> onConfirmAction());
        cancelButton = makeTextButton(" Cancel ", event -> onCancelAction());

        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }

    protected void looseFocus() {
        getScene().getRoot().requestFocus();
    }
}
