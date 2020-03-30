package org.roux;

import javafx.application.Application;
import javafx.stage.Stage;
import org.roux.game.GameLibrary;
import org.roux.utils.FileManager;
import org.roux.window.MainWindow;

public class App extends Application {

    private final GameLibrary gameLibrary = new GameLibrary();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow(this.gameLibrary);
        mainWindow.show();
    }

    @Override
    public void stop() throws Exception {
        FileManager.save(this.gameLibrary);
        super.stop();
    }
}
