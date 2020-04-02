package org.roux.window;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import org.roux.utils.UndecoratedWindowHelper;

public abstract class WindowLayout extends UndecoratedWindow {

    private static final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    public static final double WINDOW_MINIMUM_WIDTH = 120.0;
    public static final double WINDOW_MINIMUM_HEIGHT = 120.0d;
    public static final double WINDOW_DEFAULT_WIDTH = 480.0d;
    public static final double WINDOW_DEFAULT_HEIGHT = 480.0d;
    public static final double WINDOW_MAXIMUM_WIDTH = screenBounds.getWidth();
    public static final double WINDOW_MAXIMUM_HEIGHT = screenBounds.getHeight();
    public static final double BUTTON_SIZE = 16.0d;

    private final HBox header;
    private final BorderPane layout;

    private boolean isMaximized = false;
    private boolean isMinimized = false;
    private double xOffset = 0;
    private double yOffset = 0;

    public WindowLayout() {
        this(WINDOW_MINIMUM_WIDTH, WINDOW_MINIMUM_HEIGHT
                , WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT
                , WINDOW_MAXIMUM_WIDTH, WINDOW_MAXIMUM_HEIGHT);

    }

    public WindowLayout(final double minWidth, final double minHeight,
                        final double prefWidth, final double prefHeight,
                        final double maxWidth, final double maxHeight) {
        super();
        header = buildHeader();
        layout = buildLayout();
        layout.setMinSize(minWidth, minHeight);
        layout.setPrefSize(prefWidth, prefHeight);
        layout.setMaxSize(maxWidth, maxHeight);
        setMinWidth(minWidth + 20);
        setMinHeight(minHeight + 20);
        setMaxWidth(maxWidth - 20);
        setMaxHeight(maxHeight - 20);
    }

    public void setRoot(final Region content) {
        super.setRoot(layout);
        layout.setTop(header);
        setPrefSize(content.getPrefWidth(), content.getPrefHeight());
        layout.setCenter(content);

        // Permet de move a l'aide du header
        header.setOnMousePressed(event -> {
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
        });
        header.setOnMouseDragged(event -> {
            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);
        });
        // Ou de resize sa fenêtre comme le fun le veux
        UndecoratedWindowHelper.addResizeListener(this);
    }

    public void setMinSize(final double minWidth, final double minHeight) {
        layout.setMinSize(minWidth, minHeight);
    }

    public void setPrefSize(final double prefWidth, final double prefHeight) {
        layout.setPrefSize(prefWidth, prefHeight);
    }

    public void setMaxSize(final double maxWidth, final double maxHeight) {
        layout.setMaxSize(maxWidth, maxHeight);
    }

    private Button makeGraphicButton(final String name, final double size,
                                     final EventHandler<ActionEvent> event) {
        final Button button = new Button();
        final Image optionIcon = new Image(name);
        final ImageView imageView = new ImageView(optionIcon);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);
        button.setOnAction(event);
        button.setPrefSize(size, size);
        button.setGraphic(imageView);

        return button;
    }

    private HBox buildHeader() {
        final HBox hBox = new HBox();
        hBox.setPadding(new Insets(0, 0, 0, 10));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getStyleClass().add("window-header");

        final Text title = new Text("Ceci est un titre");
        title.setFont(Font.font(14));

        final Region gapFiller = new Region();
        gapFiller.setPrefWidth(WINDOW_MAXIMUM_WIDTH * 2);

        //        final Button minimize = makeGraphicButton("minimize.png", BUTTON_SIZE, event -> {
        //            setIconified(!isIconified());
        //        });
        //        minimize.getStyleClass().add("minimize-button");
        final Button maximize = makeGraphicButton("maximize.png", BUTTON_SIZE, event -> {
            setMaximized(!isMaximized());
        });
        maximize.getStyleClass().add("maximize-button");
        final Button exit = makeGraphicButton("close.png", BUTTON_SIZE, event -> {
            Platform.exit();
        });
        exit.getStyleClass().add("close-button");

        hBox.getChildren().addAll(title, gapFiller,
                //                                  minimize,
                                  maximize, exit);
        return hBox;
    }

    private BorderPane buildLayout() {
        final BorderPane borderPane = new BorderPane();

        return borderPane;
    }

    protected void onOpenWindow() {}

    protected abstract void onConfirmAction();

    protected abstract void onCancelAction();

}
