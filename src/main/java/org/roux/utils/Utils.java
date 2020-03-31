package org.roux.utils;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class Utils {

    public static Separator makeVerticalSeparator() {
        final Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPadding(new Insets(0, 0, 0, 3));
        return separator;
    }

    public static void autoResizeColumns(final TableView<?> table) {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach((column) -> {
            final Text title = new Text(column.getText().toUpperCase());
            double max = title.getLayoutBounds().getWidth() * 1;
            for(int i = 0; i < table.getItems().size(); i++) {
                if(column.getCellData(i) != null) {
                    final Text content = new Text(column.getCellData(i).toString());
                    final double layoutWidth = content.getLayoutBounds().getWidth() * 1;
                    if(layoutWidth > max) max = layoutWidth;
                }
            }
            column.setPrefWidth(max + 45.0d);
        });
    }

    public static Separator makeHorizontalSeparator() {
        final Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        separator.setPadding(new Insets(0, 0, 3, 0));
        return separator;
    }

    public static Button makeTextButton(final String text, final EventHandler<MouseEvent> event) {
        final Button button = new Button(text);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event);

        return button;
    }

    public static Button makeGraphicButton(final String name, final int size,
                                           final EventHandler<MouseEvent> event) {
        final Button button = new Button();
        final Image optionIcon = new Image(name);
        final ImageView imageView = new ImageView(optionIcon);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event);
        button.setPrefSize(size, size);
        button.setGraphic(imageView);

        return button;
    }

}
