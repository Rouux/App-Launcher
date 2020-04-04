package org.roux.utils;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/*
 * Thanks to : Alexander Berg on
 *  https://stackoverflow.com/questions/19455059/allow-user-to-resize-an-undecorated-stage
 *
 *  And well my own work (Roux) to merge move and resize together
 */
public abstract class UndecoratedWindowHelper {

    private static final double THRESHOLD = 5;

    public static void addResizeListener(final Stage stage) {
        final ResizeListener moveListener = new ResizeListener(stage);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, moveListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, moveListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, moveListener);
    }

    static class ResizeListener implements EventHandler<MouseEvent> {

        private final Stage stage;

        private Cursor cursorEvent = Cursor.DEFAULT;
        private double startX = 0;
        private double startY = 0;

        public ResizeListener(final Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(final MouseEvent mouseEvent) {
            final EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            final Scene scene = stage.getScene();

            final double mouseEventX = mouseEvent.getSceneX();
            final double mouseEventY = mouseEvent.getSceneY();
            final double sceneWidth = scene.getWidth();
            final double sceneHeight = scene.getHeight();

            if(MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
                if(sceneWidth - mouseEventX < THRESHOLD && sceneHeight - mouseEventY < THRESHOLD) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if(sceneHeight - mouseEventY < THRESHOLD
                        && mouseEventX < THRESHOLD) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if(mouseEventX < THRESHOLD) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if(sceneWidth - mouseEventX < THRESHOLD) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if(sceneHeight - mouseEventY < THRESHOLD) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }
                scene.setCursor(cursorEvent);
            } else if(MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;
            } else if(MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
                if(!Cursor.DEFAULT.equals(cursorEvent)) {
                    if(!Cursor.W_RESIZE.equals(cursorEvent)
                            && !Cursor.E_RESIZE.equals(cursorEvent)) {
                        if(stage.getHeight() > stage.getMinHeight()
                                || mouseEventY + startY - stage.getHeight() > 0) {
                            stage.setHeight(mouseEventY + startY);
                        }
                    }
                    if(!Cursor.N_RESIZE.equals(cursorEvent)
                            && !Cursor.S_RESIZE.equals(cursorEvent)) {
                        if(Cursor.W_RESIZE.equals(cursorEvent)
                                || Cursor.SW_RESIZE.equals(cursorEvent)) {
                            if(stage.getWidth() > stage.getMinHeight()
                                    || mouseEventX < 0) {
                                stage.setWidth(
                                        stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                                stage.setX(mouseEvent.getScreenX());
                            }
                        } else {
                            if(stage.getWidth() > stage.getMinHeight()
                                    || (mouseEventX + startX) - stage.getWidth() > 0) {
                                stage.setWidth(mouseEventX + startX);
                            }
                        }
                    }
                }
            }
        }
    }
}
