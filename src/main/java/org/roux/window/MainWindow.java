package org.roux.window;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.roux.game.Game;
import org.roux.game.GameLibrary;
import org.roux.utils.AutoCompleteTextField;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class MainWindow extends UndecoratedStage {

    public static final int FIELD_WIDTH = 280;
    public static final int APP_HEIGHT = FIELD_WIDTH / 12;
    public static final int BUTTON_SIZE = APP_HEIGHT;

    private final GameLibrary gameLibrary;

    private OptionWindow optionWindow;
    private Parent root;

    private Button updateButton;
    private AutoCompleteTextField textField;
    private Button optionButton;

    public MainWindow(GameLibrary gameLibrary) {
        this.gameLibrary = gameLibrary;
        this.root = buildRoot();

        this.setRoot(this.root);
        this.scene.setFill(Color.TRANSPARENT);
        this.scene.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ESCAPE) {
                this.close();
                Platform.exit();
            }
        });
        this.setAlwaysOnTop(true);
        this.focusedProperty().addListener((observableValue, node, t1) -> {
            //            System.out.println("Focus changed to -> " + t1);
        });
        this.setOnShowing(event -> this.textField.requestFocus());
    }

    public void launchGame(String name) {
        Game game = this.gameLibrary.getGame(name);
        if(game != null) {
            Path path = game.getExecutablePath();
            if(path.toFile().canExecute()) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", path.toString());
                    processBuilder.start();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                this.close();
            } else {
                // error, missing executable
            }
        }
        //@todo On verra si on met un truc ici pour dire que y'a erreur
    }

    public void scan() {
        ObservableList<Game> games = this.gameLibrary.scan();
        this.textField.getEntries().clear();
        this.textField.getEntries().addAll(games.stream()
                                                   .map(Game::getName)
                                                   .collect(Collectors.toList())
        );
    }

    public Parent buildRoot() {
        this.textField = makeField();
        this.updateButton = makeGraphicButton("update-icon.png", MainWindow.BUTTON_SIZE, event -> {
            this.scan();
            System.out.println("Scanning done");
            event.consume();
        });
        this.optionButton = makeGraphicButton("option-icon.png", MainWindow.BUTTON_SIZE, event -> {
            if(this.optionWindow == null)
                this.optionWindow = new OptionWindow(this, gameLibrary);
            this.optionWindow.show();
            this.setOpacity(0);
            event.consume();
        });

        HBox root = new HBox(updateButton, makeVerticalSeparator(), textField, makeVerticalSeparator(), optionButton);
        root.setPadding(new Insets(2));
        root.setBorder(Border.EMPTY);
        root.setAlignment(Pos.CENTER);

        return root;
    }

    public AutoCompleteTextField makeField() {
        AutoCompleteTextField textField = new AutoCompleteTextField(this, gameLibrary);
        textField.setPromptText("Find my game");
        textField.setPrefSize(FIELD_WIDTH, APP_HEIGHT);
        textField.getEntries().addAll(
                gameLibrary.getLibrary()
                        .stream()
                        .map(Game::getName)
                        .collect(Collectors.toList())
        );
        return textField;
    }

}