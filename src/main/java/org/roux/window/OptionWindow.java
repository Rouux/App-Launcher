package org.roux.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.roux.application.Application;
import org.roux.application.ApplicationLibrary;
import org.roux.utils.FileManager;
import org.roux.window.tabs.*;

import static org.roux.utils.Utils.makeTextButton;

public class OptionWindow extends WindowLayout {

    public static final double WINDOW_DEFAULT_WIDTH = 520.0d;
    public static final double WINDOW_DEFAULT_HEIGHT = 580.0d;

    private final Stage owner;
    private final ApplicationLibrary applicationLibrary;

    // Kinda local thingy
    /**
     * 'applications' ne manipule que des copies ! Pas les originaux !
     */
    private final ObservableList<Application> applications = FXCollections.observableArrayList();
    private final ObservableList<String> sourceFolders = FXCollections.observableArrayList();
    private final ObservableList<String> sourceFiles = FXCollections.observableArrayList();
    private final ObservableList<String> blacklist = FXCollections.observableArrayList();
    private final ObservableList<String> banWordFolders = FXCollections.observableArrayList();
    private final ObservableList<String> banWordExecutables = FXCollections.observableArrayList();

    public OptionWindow(final Stage owner, final ApplicationLibrary applicationLibrary) {
        this.owner = owner;
        this.applicationLibrary = applicationLibrary;
        setAll();

        final TabPane tabPane = new TabPane(
                new SourceTab(this, "Sources", sourceFolders, sourceFiles),
                new ApplicationTab(this, "Apps", applications, blacklist),
                new BlacklistTab(this, "Blacklist", blacklist, applications),
                new BanWordTab(this, "Ban words", banWordFolders, banWordExecutables),
                new ParameterTab(this, "Other")
        );
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();
        final VBox root = buildRoot(tabPane, confirmOrCancelButtons);

        initOwner(owner);
        setRoot(root);
        root.requestFocus();
    }

    @Override
    protected void onOpenWindow() {
        setAll();
    }

    @Override
    protected void onConfirmAction() {
        FileManager.setFolders(sourceFolders);
        FileManager.setExecutables(sourceFiles);
        FileManager.setBlacklist(blacklist);
        FileManager.setBanWordFolders(banWordFolders);
        FileManager.setBanWordExecutables(banWordExecutables);
        applicationLibrary.setLibrary(applications);
        close();
    }

    @Override
    protected void onCancelAction() {
        setAll();
        close();
    }

    private VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        return root;
    }

    private HBox buildConfirmOrCancelButtons() {
        final Button confirmButton = makeTextButton("    OK    ", event -> close());
        confirmButton.setOnAction(event -> onConfirmAction());

        final Button cancelButton = makeTextButton(" Cancel ", event -> close());
        cancelButton.setOnAction(event -> onCancelAction());

        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }

    private void setAll() {
        sourceFolders.setAll(FileManager.getFolders());
        sourceFiles.setAll(FileManager.getExecutables());
        blacklist.setAll(FileManager.getBlacklist());
        banWordFolders.setAll(FileManager.getBanWordFolders());
        banWordExecutables.setAll(FileManager.getBanWordExecutables());
        applications.setAll(applicationLibrary.getLibraryCopies());
    }

    @Override
    public void hide() {
        super.hide();
        owner.setOpacity(1);
    }
}
