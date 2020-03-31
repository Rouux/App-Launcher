package org.roux;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.roux.application.ApplicationLibrary;
import org.roux.utils.FileManager;
import org.roux.window.ErrorWindow;
import org.roux.window.MainWindow;

public class App extends Application {

    private final ApplicationLibrary applicationLibrary = new ApplicationLibrary();

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(final Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler(App::showError);

        final MainWindow mainWindow = new MainWindow(applicationLibrary);
        mainWindow.show();
    }

    private static void showError(final Thread t, final Throwable e) {
        System.err.println("***Default exception handler***");
        if(Platform.isFxApplicationThread()) {
            new ErrorWindow(e);
        } else {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        FileManager.save(applicationLibrary);
        super.stop();
    }
}
