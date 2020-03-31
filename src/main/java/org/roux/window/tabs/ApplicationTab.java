package org.roux.window.tabs;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.roux.application.Application;
import org.roux.application.ApplicationLibrary;
import org.roux.utils.Utils;
import org.roux.window.EditApplicationWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.roux.utils.Utils.makeTextButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class ApplicationTab extends CustomTab {

    private final ApplicationLibrary applicationLibrary;
    private final EditApplicationWindow editApplicationWindow;

    private TableView<Application> applicationView;
    private HBox applicationButtons;

    private final Map<Application, StringPropertyBase> appToName = new HashMap<>();
    private final Map<Application, List<String>> appToKeywords = new HashMap<>();

    public ApplicationTab(Stage sourceWindow, String name, Button confirmButton, Button cancelButton,
                          ApplicationLibrary applicationLibrary) {
        super(sourceWindow, name, confirmButton, cancelButton);
        this.applicationLibrary = applicationLibrary;
        this.editApplicationWindow = new EditApplicationWindow(sourceWindow, confirmButton, cancelButton);
        this.editApplicationWindow.setOnHidden(event -> {
            applicationView.refresh();
        });

        this.applicationView = buildApplicationView();
        this.applicationButtons = buildApplicationButtons();

        addConfirmButtonEvent(event -> {
            appToKeywords.forEach(Application::setKeywords);
            appToName.forEach((application, stringPropertyBase) -> application.setName(stringPropertyBase.get()));
            applicationView.refresh();
        });

        addCancelButtonEvent(event -> {
            applicationLibrary.getLibrary().forEach(
                    application -> appToName.put(application, new SimpleStringProperty(application.getName())));
            applicationLibrary.getLibrary().forEach(
                    application -> appToKeywords.put(application, new ArrayList<>(application.getKeywords())));
            applicationView.refresh();
        });

        VBox root = new VBox(applicationView, applicationButtons);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setRoot(sourceWindow, root);
    }

    public TableView<Application> buildApplicationView() {
        TableView<Application> table = new TableView<>(applicationLibrary.getLibrary());
        table.setEditable(false);
        table.setStyle("-fx-font-size: 12");
        table.setRowFactory(tv -> {
            TableRow<Application> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty() && row.getItem() != null) {
                    Application application = row.getItem();
                    this.editApplicationWindow.edit(application,
                                                    this.appToName.get(application),
                                                    this.appToKeywords.get(application));
                }
            });
            return row;
        });
        table.getItems().addListener((Observable observable) -> {
            Utils.autoResizeColumns(table);
        });
        applicationLibrary.getLibrary().addListener((Observable observable) -> {
            Utils.autoResizeColumns(table);
        });

        TableColumn<Application, String> name = buildNameColumn();
        TableColumn<Application, String> keywords = buildKeywordsColumn();
        table.getColumns().setAll(name, keywords);
        Utils.autoResizeColumns(table);
        return table;
    }

    public TableColumn<Application, String> buildNameColumn() {
        TableColumn<Application, String> column = new TableColumn<>("Name");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> {
            appToName.computeIfAbsent(data.getValue(), k -> new SimpleStringProperty(data.getValue().getName()));
            return new SimpleStringProperty(appToName.get(data.getValue()).get());
        });

        return column;
    }

    public TableColumn<Application, String> buildKeywordsColumn() {
        TableColumn<Application, String> column = new TableColumn<>("Keywords");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> {
            appToKeywords.computeIfAbsent(data.getValue(), k -> new ArrayList<>(data.getValue().getKeywords()));
            return new SimpleStringProperty(appToKeywords.get(data.getValue()).toString());
        });

        return column;
    }

    public HBox buildApplicationButtons() {
        Button edit = makeTextButton("Edit", event -> {
            Application application = this.applicationView.getSelectionModel().getSelectedItem();
            if(application != null) {
                this.editApplicationWindow.edit(application,
                                                this.appToName.get(application),
                                                this.appToKeywords.get(application));
            }
        });

        Button remove = makeTextButton("Remove", event -> {

        });

        Button blacklist = makeTextButton("Add to blacklist", event -> {

        });

        HBox buttons = new HBox(edit, makeVerticalSeparator(), remove, makeVerticalSeparator(), blacklist);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }
}
