package org.roux.window;

import javafx.beans.property.StringPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.roux.application.Application;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.roux.utils.Utils.*;

public class EditApplicationWindow extends UndecoratedStage {

    private final static int WINDOW_WIDTH = 480;
    private final static int WINDOW_HEIGHT = 320;

    private VBox root;
    private Stage main;
    private Application application;

    // Name
    private final TextField applicationName;
    private String initialApplicationName;
    private StringPropertyBase applicationNameProperty;

    // Path
    private final Map<Application, String> applicationToPath;
    private TextField applicationPath;
    private Button applicationSelectFile;

    // Keywords
    private ListView<String> keywordView;
    private List<String> keywords;
    private HBox keywordButtons;

    // Confirm or cancel
    private HBox confirmOrCancelButtons;

    public EditApplicationWindow(Stage owner, Button confirmButton, Button cancelButton) {
        this.main = owner;
        this.applicationToPath = new HashMap<>();

        this.applicationName = buildNameField();
        HBox pathOptions = buildPathOptions();
        this.keywordView = buildKeywordView();
        this.keywordButtons = buildKeywordButtons();
        this.confirmOrCancelButtons = buildConfirmOrCancelButtons();

        confirmButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.applicationToPath.forEach((application, path) -> {
                application.setExecutablePath(Paths.get(path));
            });
            this.applicationToPath.clear();
        });

        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.applicationToPath.clear();
        });

        this.root = buildRoot(
                new Label("Name"),
                applicationName,
                new Label("Path"),
                pathOptions,
                new Label("Keywords"),
                keywordView,
                keywordButtons,
                confirmOrCancelButtons);

        this.setOnShowing(event -> this.root.requestFocus());
        this.initOwner(owner);
        this.setRoot(this.root);
    }

    public void edit(Application application, StringPropertyBase nameProperty, List<String> tableKeywordsRef) {
        this.application = application;
        this.applicationNameProperty = nameProperty;
        this.initialApplicationName = nameProperty.get();
        this.applicationName.setText(nameProperty.get());

        this.applicationToPath.computeIfAbsent(application, value -> application.getExecutablePath().toString());
        this.applicationPath.setText(this.applicationToPath.get(application));

        this.keywords = tableKeywordsRef;
        this.keywordView.getItems().setAll(tableKeywordsRef);
        this.show();
    }

    public VBox buildRoot(Node... nodes) {
        VBox root = new VBox(nodes);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        return root;
    }

    private TextField buildNameField() {
        TextField textField = new TextField();
        textField.setPromptText("Enter the application name");
        textField.setOnKeyReleased(t -> {
            if(t.getCode() == KeyCode.ENTER) {
                this.root.requestFocus();
                t.consume();
            }
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.applicationNameProperty.set(newValue);
        });

        return textField;
    }

    private HBox buildPathOptions() {
        this.applicationPath = new TextField();
        this.applicationPath.setPromptText("Select a valid path for the application's executable");
        this.applicationPath.setPrefWidth(WINDOW_WIDTH);
        this.applicationPath.setOnKeyReleased(t -> {
            if(t.getCode() == KeyCode.ENTER) {
                this.root.requestFocus();
                t.consume();
            }
        });
        this.applicationPath.textProperty().addListener((observable, oldValue, newValue) -> {
            if(application != null)
                this.applicationToPath.put(application, newValue);
        });

        FileChooser fileChooser = new FileChooser();
        this.applicationSelectFile = makeTextButton("...", event -> {
            File currentFile = new File(this.applicationPath.getText());
            if(currentFile.isFile()) {
                fileChooser.setInitialDirectory(currentFile.getParentFile());
                File chosenFile = fileChooser.showOpenDialog(this);
                if(chosenFile != null && chosenFile.exists())
                    this.applicationPath.setText(chosenFile.getAbsolutePath());
            }
        });

        HBox hBox = new HBox(this.applicationPath, this.applicationSelectFile);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    private ListView<String> buildKeywordView() {
        ListView<String> keywordView = new ListView<>();
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
        Button add = makeGraphicButton("add-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            this.keywordView.getItems().add("keyword");
        });
        Button remove = makeGraphicButton("remove-icon.png", MainWindow.BUTTON_SIZE - 8, event -> {
            List<String> items = this.keywordView.getSelectionModel().getSelectedItems();
            this.keywordView.getItems().removeAll(items);
        });

        HBox buttons = new HBox(add, makeVerticalSeparator(), remove);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }

    private HBox buildConfirmOrCancelButtons() {
        Button confirmButton = makeTextButton("    OK    ", event -> {
            this.keywords.clear();
            this.keywords.addAll(this.keywordView.getItems());
            this.close();
        });

        Button cancelButton = makeTextButton(" Cancel ", event -> {
            this.applicationNameProperty.set(this.initialApplicationName);
            this.close();
        });

        HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }
}
