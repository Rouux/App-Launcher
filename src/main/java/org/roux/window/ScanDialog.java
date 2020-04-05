package org.roux.window;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.roux.utils.ScannerTool;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.roux.utils.Utils.makeTextButton;

public class ScanDialog extends WindowLayout {

    private final static int WINDOW_WIDTH = 560;
    private final static int WINDOW_HEIGHT = 150;
    private ScannerTool scannerTool;

    private final VBox root;
    private final Label fileScanningStatus;

    private final ProgressBar progressBar;
    private final Label progressPercent = new Label("");
    private Button confirmButton;
    private Button cancelButton;

    public ScanDialog(final Stage owner) {
        fileScanningStatus = new Label();
        progressBar = new ProgressBar();
        progressBar.prefWidthProperty().bind(Bindings.subtract(widthProperty(), 100));
        final HBox progress = new HBox(10, progressBar, progressPercent);
        progress.setPadding(new Insets(0, 10, 0, 10));
        progress.setPrefHeight(10);
        progress.setAlignment(Pos.CENTER_LEFT);
        final HBox confirmOrCancelButtons = buildConfirmOrCancelButtons();

        root = buildRoot(fileScanningStatus, progress, confirmOrCancelButtons);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setRoot(root);
    }

    @Override
    protected void onConfirmAction() {
        close();
    }

    @Override
    protected void onCancelAction() {
        scannerTool.cancel();
        close();
    }

    public Optional<List<Path>> openDialog() {
        scannerTool = new ScannerTool();
        bindToWorker(scannerTool);
        final Thread backgroundThread = new Thread(scannerTool);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
        showAndWait();
        final List<Path> list = scannerTool.getFiles();
        return Optional.ofNullable(list);
    }

    public void bindToWorker(final Worker<String> worker) {
        // Bind Labels to the properties of the worker
        titleProperty().bind(worker.titleProperty());

        fileScanningStatus.textProperty().bind(
                new When(worker.totalWorkProperty().isEqualTo(-1))
                        .then("Checking data . . .").otherwise(
                        new When(worker.workDoneProperty().isEqualTo(worker.totalWorkProperty()))
                                .then("Done").otherwise(worker.valueProperty().asString()))
        );

        progressBar.progressProperty().bind(worker.progressProperty());
        progressPercent.textProperty().bind(
                new When(worker.progressProperty().isEqualTo(-1))
                        .then("0.0%")
                        .otherwise(worker.progressProperty().multiply(100.0).asString("%.2f%%")));

        confirmButton.disableProperty().bind(worker.runningProperty());
        cancelButton.disableProperty().bind(worker.runningProperty().not());
    }

    private VBox buildRoot(final Node... nodes) {
        final VBox root = new VBox(nodes);
        root.setSpacing(20);
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

}
