package org.roux.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import org.roux.window.tabs.ApplicationTab;
import org.roux.window.tabs.BlacklistTab;
import org.roux.window.tabs.FolderTab;
import org.roux.window.tabs.ParameterTab;

import static org.roux.utils.Utils.makeTextButton;

public class OptionWindow extends UndecoratedStage {

    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 540;

    private final Stage owner;
    private ApplicationLibrary applicationLibrary;

    // Kinda local thingy
    /**
     * 'applications' ne manipule que des copies ! Pas les originaux !
     */
    private final ObservableList<Application> applications = FXCollections.observableArrayList();
    private final ObservableList<String> sourceFolders = FXCollections.observableArrayList();
    private final ObservableList<String> sourceFiles = FXCollections.observableArrayList();
    private final ObservableList<String> blacklist = FXCollections.observableArrayList();

    private final EventHandler<ActionEvent> confirmationButtonEvent = event -> {
        // Don't forget to fill when action has to occur on tabs
        FileManager.setFolders(sourceFolders);
        FileManager.setExecutables(sourceFiles);
        FileManager.setBlacklist(blacklist);
        applicationLibrary.setLibrary(applications);
        close();
    };

    private final EventHandler<ActionEvent> cancelButtonEvent = event -> {
        // Don't forget to fill when action has to occur on tabs
        setAll();
        close();
    };

    public OptionWindow(final Stage owner, final ApplicationLibrary applicationLibrary) {
        this.owner = owner;
        this.applicationLibrary = applicationLibrary;
        setAll();

        final TabPane tabPane = new TabPane(
                new FolderTab(this, "Sources", sourceFolders, sourceFiles),
                new ApplicationTab(this, "Apps", applications, blacklist),
                new BlacklistTab(this, "Blacklist", blacklist, applications),
                new ParameterTab(this, "Other")
        );
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();
        final VBox root = buildRoot(tabPane, confirmOrCancelButtons);

        initOwner(owner);
        setRoot(root);
        getScene().getStylesheets().add("style.css");
    }

    @Override
    protected void onOpenWindow() {
        setAll();
    }

    @Override
    protected void onConfirmAction() {
        //@todo see if there's anything logical by pushing ENTER here
    }

    @Override
    protected void onCancelAction() {
        //@todo see if there's anything logical by pushing ESCAPE here
    }

    private VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(10));
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        return root;
    }

    private HBox buildConfirmOrCancelButtons() {
        final Button confirmButton = makeTextButton("    OK    ", event -> close());
        confirmButton.setOnAction(confirmationButtonEvent);

        final Button cancelButton = makeTextButton(" Cancel ", event -> close());
        cancelButton.setOnAction(cancelButtonEvent);

        final HBox confirmOrCancel = new HBox(confirmButton, cancelButton);
        confirmOrCancel.setAlignment(Pos.CENTER_RIGHT);
        confirmOrCancel.setSpacing(10);
        return confirmOrCancel;
    }

    private void setAll() {
        applications.setAll(applicationLibrary.getLibraryCopies());
        sourceFolders.setAll(FileManager.getFolders());
        sourceFiles.setAll(FileManager.getExecutables());
        blacklist.setAll(FileManager.getBlacklist());
    }

    @Override
    public void hide() {
        super.hide();
        owner.setOpacity(1);
    }
}
