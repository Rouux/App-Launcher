package org.roux.window;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.roux.FileManager;
import org.roux.game.Game;
import org.roux.game.GameLibrary;
import org.roux.utils.AutoCompleteTextField;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.roux.utils.Utils.makeGraphicButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class MainWindow extends Application {

    public static final int APP_HEIGHT = 24;
    public static final int FIELD_WIDTH = 260;
    public static final int BUTTON_SIZE = APP_HEIGHT;

    private final GameLibrary gameLibrary = new GameLibrary();

    private Stage stage;
    private OptionWindow optionWindow;
    private Scene scene;
    private Parent root;

    private Button updateButton;
    private AutoCompleteTextField textField;
    private Button optionButton;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.root = buildRoot();
        this.scene = buildScene(root);

        JMetro jMetro = new JMetro(scene, Style.DARK);
        jMetro.setAutomaticallyColorPanes(true);

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        stage.focusedProperty().addListener((observableValue, node, t1) -> {
            System.out.println("Focus changed to -> " + t1);
        });
        this.textField.requestFocus();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Closing....");
        FileManager.save(this.gameLibrary);
        super.stop();
    }

    public void launchGame(String name) {
        Game game = this.gameLibrary.getGame(name);
        if(game != null) {
            Path path = game.getExecutablePath();
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", path.toString());
                processBuilder.start();
            } catch(IOException e) {
                e.printStackTrace();
            }
            this.stage.close();
        }
        //@todo On verra si on met un truc ici pour dire que y'a erreur
    }

    public void scan() {
        List<Game> games = this.gameLibrary.scan();
        this.textField.getEntries().clear();
        this.textField.getEntries().addAll(games.stream()
                                                   .map(Game::getName)
                                                   .collect(Collectors.toList())
        );
    }

    public Parent buildRoot() {
        this.textField = makeField();
        this.updateButton = makeGraphicButton("update-icon.png", MainWindow.BUTTON_SIZE, event -> {
            System.out.println("Scanning...");
            this.scan();
            event.consume();
        });
        this.optionButton = makeGraphicButton("option-icon.png", MainWindow.BUTTON_SIZE, event -> {
            System.out.println("Option -> open");
            if(this.optionWindow == null)
                this.optionWindow = new OptionWindow(stage, gameLibrary);
            this.optionWindow.show();
            this.stage.setOpacity(0);
            event.consume();
        });

        HBox root = new HBox(updateButton, makeVerticalSeparator(), textField, makeVerticalSeparator(), optionButton);
        root.setPadding(new Insets(2));
        root.setBorder(Border.EMPTY);
        root.setAlignment(Pos.CENTER);

        return root;
    }

    public Scene buildScene(Parent root) {
        Scene scene = new Scene(root, Color.TRANSPARENT);
        scene.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ESCAPE) {
                System.out.println("Key Pressed: " + ke.getCode());
                stage.close();
                Platform.exit();
            }
        });
        return scene;
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

    public static void main(String[] args) {
        launch();
    }

}