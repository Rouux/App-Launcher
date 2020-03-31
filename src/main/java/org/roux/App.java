package org.roux;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.roux.game.GameLibrary;
import org.roux.utils.FileManager;
import org.roux.window.ErrorWindow;
import org.roux.window.MainWindow;

public class App extends Application {

    private final GameLibrary gameLibrary = new GameLibrary();
    private MainWindow mainWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler(App::showError);

        this.mainWindow = new MainWindow(this.gameLibrary);
        this.mainWindow.show();
    }

    private static void showError(Thread t, Throwable e) {
        System.err.println("***Default exception handler***");
        if(Platform.isFxApplicationThread()) {
            new ErrorWindow(e);
        } else {
            System.err.println("An unexpected error occurred in " + t);
        }
    }

    @Override
    public void stop() throws Exception {
        FileManager.save(this.gameLibrary);
        super.stop();
    }
}
