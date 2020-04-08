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
import javafx.stage.Stage;
import org.roux.window.SearchWindow;
import org.roux.window.TextFieldDialog;

import java.util.List;
import java.util.Optional;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class BanWordTab extends CustomTab {
    private final ObservableList<String> banWordFolders;
    private final ListView<String> folderView;

    private final ObservableList<String> banWordExecutables;
    private final ListView<String> fileView;

    private final TextFieldDialog addBanWordDialog;

    public BanWordTab(final Stage sourceWindow, final String name,
                      final ObservableList<String> banWordFolders,
                      final ObservableList<String> banWordExecutables) {
        super(sourceWindow, name);
        this.banWordFolders = banWordFolders;
        this.banWordExecutables = banWordExecutables;
        addBanWordDialog = new TextFieldDialog(sourceWindow);

        folderView = buildFolderView();
        folderView.getStyleClass().add("alternating-row-colors");
        final HBox folderViewButtons = buildFolderViewButtons();

        fileView = buildFileView();
        fileView.getStyleClass().add("alternating-row-colors");
        final HBox fileViewButtons = buildFileViewButtons();

        final VBox root = new VBox(
                new Label(""), // Pour ajouter un retour à la ligne
                new Label("Folders"), folderView, folderViewButtons,
                new Label("Files"), fileView, fileViewButtons);
        root.setSpacing(5);
        setRoot(root);
    }

    private ListView<String> buildView(final ObservableList<String> observableList) {
        final ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(observableList);

        return listView;
    }

    private ListView<String> buildFolderView() {
        return buildView(banWordFolders);
    }

    private Button buildAddButton(final EventHandler<MouseEvent> event) {
        return makeGraphicButton("add-icon.png", SearchWindow.BUTTON_SIZE - 12, event);
    }

    private Button buildRemoveButton(final ListView<String> listView,
                                     final ObservableList<String> observableList) {
        return makeGraphicButton("remove-icon.png", SearchWindow.BUTTON_SIZE - 12, event -> {
            final List<String> selectedItems =
                    listView.getSelectionModel().getSelectedItems();
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

    private HBox buildFolderViewButtons() {
        final Button add = buildAddButton(event -> {
            final Optional<String> result = addBanWordDialog.openDialog();
            result.ifPresent(banWordFolders::add);
            looseFocus();
        });
        final Button remove = buildRemoveButton(folderView, banWordFolders);

        return buildButtonBox(add, makeVerticalSeparator(), remove);
    }

    private ListView<String> buildFileView() {
        return buildView(banWordExecutables);
    }

    private HBox buildFileViewButtons() {
        final Button add = buildAddButton(event -> {
            final Optional<String> result = addBanWordDialog.openDialog();
            result.ifPresent(banWordExecutables::add);
            looseFocus();
        });
        final Button remove = buildRemoveButton(fileView, banWordExecutables);

        return buildButtonBox(add, makeVerticalSeparator(), remove);
    }
}
