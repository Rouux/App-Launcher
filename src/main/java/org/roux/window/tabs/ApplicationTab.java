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

    private final Map<Application, StringPropertyBase> appToName = new HashMap<>();
    private final Map<Application, List<String>> appToKeywords = new HashMap<>();

    public ApplicationTab(final Stage sourceWindow, final String name, final Button confirmButton,
                          final Button cancelButton,
                          final ApplicationLibrary applicationLibrary) {
        super(sourceWindow, name, confirmButton, cancelButton);
        this.applicationLibrary = applicationLibrary;
        editApplicationWindow =
                new EditApplicationWindow(sourceWindow, confirmButton, cancelButton);
        editApplicationWindow.setOnHidden(event -> applicationView.refresh());

        applicationView = buildApplicationView();
        final HBox applicationButtons = buildApplicationButtons();

        addConfirmButtonEvent(event -> {
            appToKeywords.forEach(Application::setKeywords);
            appToName.forEach((application, stringPropertyBase)
                                      -> application.setName(stringPropertyBase.get()));
            applicationView.refresh();
        });

        addCancelButtonEvent(event -> {
            applicationLibrary.getLibrary().forEach(
                    application -> appToName.put(application,
                                                 new SimpleStringProperty(application.getName())));
            applicationLibrary.getLibrary().forEach(
                    application -> appToKeywords.put(application,
                                                     new ArrayList<>(application.getKeywords())));
            applicationView.refresh();
        });

        final VBox root = new VBox(applicationView, applicationButtons);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        setRoot(sourceWindow, root);
    }

    public TableView<Application> buildApplicationView() {
        final TableView<Application> table = new TableView<>(applicationLibrary.getLibrary());
        table.setEditable(false);
        table.setStyle("-fx-font-size: 12");
        table.setRowFactory(tv -> {
            final TableRow<Application> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty() && row.getItem() != null) {
                    final Application application = row.getItem();
                    editApplicationWindow.edit(application,
                                               appToName.get(application),
                                               appToKeywords.get(application));
                }
            });
            return row;
        });
        table.getItems().addListener((Observable observable) -> Utils.autoResizeColumns(table));
        applicationLibrary.getLibrary()
                .addListener((Observable observable) -> Utils.autoResizeColumns(table));

        final TableColumn<Application, String> name = buildNameColumn();
        final TableColumn<Application, String> keywords = buildKeywordsColumn();
        table.getColumns().setAll(name, keywords);
        Utils.autoResizeColumns(table);
        return table;
    }

    public TableColumn<Application, String> buildNameColumn() {
        final TableColumn<Application, String> column = new TableColumn<>("Name");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> {
            appToName.computeIfAbsent(data.getValue(),
                                      k -> new SimpleStringProperty(data.getValue().getName()));
            return new SimpleStringProperty(appToName.get(data.getValue()).get());
        });

        return column;
    }

    public TableColumn<Application, String> buildKeywordsColumn() {
        final TableColumn<Application, String> column = new TableColumn<>("Keywords");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> {
            appToKeywords.computeIfAbsent(data.getValue(),
                                          k -> new ArrayList<>(data.getValue().getKeywords()));
            return new SimpleStringProperty(appToKeywords.get(data.getValue()).toString());
        });

        return column;
    }

    public HBox buildApplicationButtons() {
        final Button edit = makeTextButton("Edit", event -> {
            final Application application = applicationView.getSelectionModel().getSelectedItem();
            if(application != null) {
                editApplicationWindow.edit(application,
                                           appToName.get(application),
                                           appToKeywords.get(application));
            }
        });

        final Button remove = makeTextButton("Remove", event -> {

        });

        final Button blacklist = makeTextButton("Add to blacklist", event -> {

        });

        final HBox buttons = new HBox(edit, makeVerticalSeparator(),
                                      remove, makeVerticalSeparator(),
                                      blacklist);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }
}
