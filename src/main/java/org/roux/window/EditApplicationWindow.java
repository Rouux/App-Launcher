package org.roux.window;

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
import java.util.List;

import static org.roux.utils.Utils.*;

public class EditApplicationWindow extends UndecoratedStage {

    private final static int WINDOW_WIDTH = 480;
    private final static int WINDOW_HEIGHT = 320;

    private final VBox root;
    private Application application;

    private final TextField nameField;
    private TextField pathField;
    private final ListView<String> keywordView;

    public EditApplicationWindow(final Stage owner) {
        nameField = buildNameField();
        final HBox pathOptions = buildPathOptions();
        keywordView = buildKeywordView();
        final HBox keywordButtons = buildKeywordButtons();
        // Confirm or cancel
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();

        root = buildRoot(new Label("Name"), nameField,
                         new Label("Path"), pathOptions,
                         new Label("Keywords"), keywordView, keywordButtons,
                         confirmOrCancelButtons);

        setOnShowing(event -> root.requestFocus());
        initOwner(owner);
        setRoot(root);
    }

    public void edit(final Application application) {
        this.application = application;
        nameField.setText(application.getName());
        pathField.setText(application.getExecutablePath().toString());
        keywordView.getItems().setAll(application.getKeywords());
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
        });

        final HBox hBox = new HBox(pathField, applicationSelectFile);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
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
                makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 8,
                                  event -> keywordView.getItems().add("keyword"));
        final Button remove =
                makeGraphicButton("remove-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
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
        final Button confirmButton = makeTextButton("    OK    ", event -> {
            application.setName(nameField.getText());
            application.setExecutablePath(pathField.getText());
            application.setKeywords(keywordView.getItems());
            close();
        });

        final Button cancelButton = makeTextButton(" Cancel ", event -> {
            close();
        });

        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }
}
