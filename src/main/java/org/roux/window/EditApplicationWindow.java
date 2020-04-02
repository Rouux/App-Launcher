package org.roux.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.roux.application.Application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.roux.utils.Utils.*;

public class EditApplicationWindow extends WindowLayout {

    private final static int WINDOW_WIDTH = 420;
    private final static int WINDOW_HEIGHT = 360;

    private final VBox root;
    private Application application;
    private final ObservableList<String> blacklist;

    private final TextField nameField;
    private TextField pathField;
    private final ListView<String> keywordView;
    private final AddKeywordWindow addKeywordWindow;
    private final CheckBox blacklistCheckbox;

    public EditApplicationWindow(final Stage owner, final ObservableList<String> blacklist) {
        this.blacklist = blacklist;
        addKeywordWindow = new AddKeywordWindow(this);
        nameField = buildNameField();
        final HBox pathOptions = buildPathOptions();
        keywordView = buildKeywordView();
        keywordView.getStyleClass().add("alternating-row-colors");
        blacklistCheckbox = buildBlacklistCheckbox();
        final HBox keywordButtons = buildKeywordButtons();
        // Confirm or cancel
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();

        root = buildRoot(new Label("Name"), nameField,
                         new Label("Path"), pathOptions,
                         new Label("Keywords"), keywordView, keywordButtons,
                         blacklistCheckbox,
                         confirmOrCancelButtons);

        setOnShowing(event -> root.requestFocus());
        initOwner(owner);
        setRoot(root);
    }

    @Override
    protected void onConfirmAction() {
        application.setName(nameField.getText());
        application.setExecutablePath(pathField.getText());
        application.setBlacklisted(blacklistCheckbox.isSelected());
        if(application.isBlacklisted()) {
            if(!blacklist.contains(application.getExecutablePath().toString()))
                blacklist.add(application.getExecutablePath().toString());
        } else {
            blacklist.remove(application.getExecutablePath().toString());
        }
        application.setKeywords(keywordView.getItems());
        close();
    }

    @Override
    protected void onCancelAction() {
        close();
    }

    public void edit(final Application application) {
        this.application = application;
        nameField.setText(application.getName());
        pathField.setText(application.getExecutablePath().toString());
        blacklistCheckbox.setSelected(application.isBlacklisted());
        final List<String> dontDeleteItIsNeeded = new ArrayList<>(application.getKeywords());
        keywordView.setItems(FXCollections.observableList(dontDeleteItIsNeeded));

        show();
    }

    public VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        return root;
    }

    private TextField buildNameField() {
        final TextField textField = new TextField();
        textField.setPromptText("Enter the application name");
        textField.setOnKeyReleased(t -> {
            if(t.getCode() == KeyCode.ENTER) {
                root.requestFocus();
                t.consume();
            }
        });
        return textField;
    }

    private HBox buildPathOptions() {
        pathField = new TextField();
        pathField.setPromptText("Select a valid path for the application's executable");
        pathField.setPrefWidth(WINDOW_WIDTH);
        pathField.setOnKeyReleased(t -> {
            if(t.getCode() == KeyCode.ENTER) {
                root.requestFocus();
                t.consume();
            }
        });
        final FileChooser fileChooser = new FileChooser();
        final Button applicationSelectFile = makeTextButton("...", event -> {
            final File currentFile = new File(pathField.getText());
            if(currentFile.isFile()) {
                fileChooser.setInitialDirectory(currentFile.getParentFile());
                final File chosenFile = fileChooser.showOpenDialog(this);
                if(chosenFile != null && chosenFile.exists())
                    pathField.setText(chosenFile.getAbsolutePath());
            }
            root.requestFocus();
        });

        final HBox hBox = new HBox(pathField, applicationSelectFile);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    private CheckBox buildBlacklistCheckbox() {
        final CheckBox checkBox = new CheckBox(" Blacklist this application");
        checkBox.setSelected(false);

        return checkBox;
    }

    private ListView<String> buildKeywordView() {
        final ListView<String> keywordView = new ListView<>();
        keywordView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        keywordView.setEditable(true);
        keywordView.setCellFactory(TextFieldListCell.forListView());
        keywordView.setOnEditCommit(t -> {
            if(t.getNewValue() == null || t.getNewValue().trim().equals("")) {
                keywordView.getItems().remove(t.getIndex());
            } else {
                keywordView.getItems().set(t.getIndex(), t.getNewValue());
            }
        });

        return keywordView;
    }

    private HBox buildKeywordButtons() {
        final Button add =
                makeGraphicButton("add-icon.png", SearchWindow.BUTTON_SIZE - 8, event -> {
                    addKeywordWindow.open(keywordView);
                    root.requestFocus();
                });
        final Button remove =
                makeGraphicButton("remove-icon.png", SearchWindow.BUTTON_SIZE - 8, event -> {
                    final List<String> items = keywordView.getSelectionModel().getSelectedItems();
                    keywordView.getItems().removeAll(items);
                });

        final HBox buttons = new HBox(add, makeVerticalSeparator(), remove);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }

    private HBox buildConfirmOrCancelButtons() {
        final Button confirmButton = makeTextButton("    OK    ", event -> onConfirmAction());
        final Button cancelButton = makeTextButton(" Cancel ", event -> onCancelAction());
        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }
}
