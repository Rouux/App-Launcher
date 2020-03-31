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
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPadding(new Insets(0, 0, 0, 3));
        return separator;
    }

    public static void autoResizeColumns(TableView<?> table) {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach((column) -> {
            Text title = new Text(column.getText());
            double max = title.getLayoutBounds().getWidth() * 1.1;
            for(int i = 0; i < table.getItems().size(); i++) {
                if(column.getCellData(i) != null) {
                    Text content = new Text(column.getCellData(i).toString());
                    double layoutWidth = content.getLayoutBounds().getWidth() * 1;
                    if(layoutWidth > max) max = layoutWidth;
                }
            }
            column.setMinWidth(max + 50.0d);
        });
    }

    public static Separator makeHorizontalSeparator() {
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        separator.setPadding(new Insets(0, 0, 3, 0));
        return separator;
    }

    public static Button makeTextButton(String text, EventHandler<MouseEvent> event) {
        Button button = new Button(text);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event);

        return button;
    }

    public static Button makeGraphicButton(String name, int size, EventHandler<MouseEvent> event) {
        Button button = new Button();
        Image optionIcon = new Image(name);
        ImageView imageView = new ImageView(optionIcon);
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
