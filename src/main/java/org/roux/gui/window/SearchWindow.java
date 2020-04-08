package org.roux.gui.window;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.roux.application.Application;
import org.roux.application.ApplicationLibrary;
import org.roux.gui.dialog.ScanDialog;
import org.roux.gui.dialog.ScanResultDialog;
import org.roux.gui.utils.AutoCompleteTextField;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class SearchWindow extends UndecoratedWindow {

    public static final int FIELD_WIDTH = 280;
    public static final int APP_HEIGHT = FIELD_WIDTH / 12;
    public static final int BUTTON_SIZE = APP_HEIGHT;

    private final ApplicationLibrary applicationLibrary;
    private OptionWindow optionWindow;
    private AutoCompleteTextField textField;

    public SearchWindow(final ApplicationLibrary applicationLibrary) {
        this.applicationLibrary = applicationLibrary;
        final Parent root = buildRoot();
        setRoot(root);

        scene.setFill(Color.TRANSPARENT);
        setAlwaysOnTop(true);
        setOnShowing(event -> textField.requestFocus());
    }

    @Override
    protected void onConfirmAction() {
        //@todo see if there's anything logical by pushing ENTER here
    }

    @Override
    protected void onCancelAction() {
        close();
        Platform.exit();
    }

    public void launchApplication(final String name) {
        final Application application = applicationLibrary.getApplication(name);
        if(application != null) {
            final Path path = application.getExecutablePath();
            if(path.toFile().canExecute()) {
                try {
                    final ProcessBuilder processBuilder =
                            new ProcessBuilder("cmd", "/c", path.toString());
                    processBuilder.start();
                } catch(final IOException e) {
                    e.printStackTrace();
                }
                close();
            } else {
                // error, missing executable
            }
        }
        //@todo Mettre un truc ici avec errorWindow !
    }

    private void scan() {
        final ScanDialog scanDialog = new ScanDialog(this);
        final ScanResultDialog scanResultDialog = new ScanResultDialog(this);
        final Optional<List<Path>> result = scanDialog.openDialog();
        if(result.isPresent()) {
            final List<Path> scanResult = result.get();
            final List<Path> filteredResult = scanResult
                    .stream()
                    .filter(path -> applicationLibrary.findSamePathApplication(path) == null)
                    .filter(path -> !ApplicationLibrary.isBlacklisted(path))
                    .collect(Collectors.toList());
            final List<Path> filteredBlacklistedResult = scanResult
                    .stream()
                    .filter(path -> applicationLibrary.findSamePathApplication(path) == null)
                    .filter(ApplicationLibrary::isBlacklisted)
                    .collect(Collectors.toList());
            final Map<Path, Boolean> isBlacklistedByExecutable =
                    scanResultDialog.seeResultDialog(filteredResult, filteredBlacklistedResult);
            scanResult.removeIf(path -> applicationLibrary.findSamePathApplication(path) == null);
            for(final Path path : scanResult) {
                isBlacklistedByExecutable.computeIfAbsent(
                        path,
                        isKept -> applicationLibrary.findSamePathApplication(path).isBlacklisted());
            }
            applicationLibrary.updateLibrary(isBlacklistedByExecutable);
            textField.getEntries().clear();
            textField.getEntries().addAll(applicationLibrary.getNames(false));
        }
    }

    private Parent buildRoot() {
        textField = makeField();
        final Button updateButton =
                makeGraphicButton("update-icon.png", SearchWindow.BUTTON_SIZE, event -> {
                    scan();
                    event.consume();
                });
        final Button optionButton =
                makeGraphicButton("option-icon.png", SearchWindow.BUTTON_SIZE, event -> {
                    if(optionWindow == null)
                        optionWindow = new OptionWindow(this, applicationLibrary);
                    optionWindow.show();
                    setOpacity(0);
                    event.consume();
                });

        final HBox root = new HBox(updateButton, makeVerticalSeparator(),
                                   textField, makeVerticalSeparator(),
                                   optionButton);
        root.setPadding(new Insets(2));
        root.setBorder(Border.EMPTY);
        root.setAlignment(Pos.CENTER);

        return root;
    }

    private AutoCompleteTextField makeField() {
        final AutoCompleteTextField textField =
                new AutoCompleteTextField(this, applicationLibrary);
        textField.setPromptText("Find an app");
        textField.setPrefSize(FIELD_WIDTH, APP_HEIGHT);
        textField.getEntries().addAll(applicationLibrary.getNames(false));
        applicationLibrary.addListener(c -> {
            textField.getEntries().clear();
            textField.getEntries().addAll(applicationLibrary.getNames(false));
        });
        return textField;
    }

}