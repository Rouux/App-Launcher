package org.roux.window.tabs;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.fxmisc.easybind.EasyBind;
import org.roux.application.Application;
import org.roux.utils.Utils;
import org.roux.window.EditApplicationWindow;
import org.roux.window.WindowLayout;

import static org.roux.utils.Utils.makeTextButton;
import static org.roux.utils.Utils.makeVerticalSeparator;

public class ApplicationTab extends CustomTab {

    private final ObservableList<Application> applications;
    private final ObservableList<String> blacklist;
    private final EditApplicationWindow editApplicationWindow;

    private final BooleanProperty seeBlacklistedProperty = new SimpleBooleanProperty(false);

    private TableView<Application> applicationView;

    public ApplicationTab(final Stage sourceWindow, final String name,
                          final ObservableList<Application> applications,
                          final ObservableList<String> blacklist) {
        super(sourceWindow, name);
        this.applications = applications;
        this.blacklist = blacklist;
        editApplicationWindow = new EditApplicationWindow(sourceWindow, blacklist);
        editApplicationWindow.setOnHidden(event -> applicationView.refresh());

        applicationView = buildApplicationView();
        applicationView.getStyleClass().add("alternating-row-colors");
        final HBox applicationButtons = buildApplicationButtons();

        final VBox root = new VBox(new Label(""), new Label("Applications"),
                                   applicationView, applicationButtons);
        root.setSpacing(5);
        setRoot(sourceWindow, root);
        setOnSelectionChanged(event -> Utils.autoResizeColumns(applicationView));
    }

    public TableView<Application> buildApplicationView() {
        final TableView<Application> table = new TableView<>(applications);
        table.setPrefHeight(WindowLayout.WINDOW_MAXIMUM_HEIGHT);
        table.setEditable(false);
        table.setStyle("-fx-font-size: 12");
        table.setRowFactory(tv -> {
            final TableRow<Application> row = new TableRow<>();
            EasyBind.select(row.itemProperty())
                    .selectObject(Application::isBlacklistedProperty)
                    .addListener((observable, oldValue, newValue) -> {
                        if(newValue == null) return;
                        if(newValue) {
                            // To ensure only one subsist
                            row.getStyleClass().remove("table-row-blacklisted");
                            row.getStyleClass().add("table-row-blacklisted");
                        } else {
                            row.getStyleClass().remove("table-row-blacklisted");
                        }
                    });
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty() && row.getItem() != null) {
                    final Application application = row.getItem();
                    editApplicationWindow.edit(application);
                }
            });

            return row;
        });
        blacklist.addListener(this::invalidated);
        seeBlacklistedProperty.addListener(this::invalidated);

        final TableColumn<Application, String> name = buildNameColumn();
        final TableColumn<Application, String> executable = buildExecutableColumn();
        final TableColumn<Application, String> keywords = buildKeywordsColumn();
        table.getColumns().setAll(name, executable, keywords);
        return table;
    }

    public TableColumn<Application, String> buildNameColumn() {
        final TableColumn<Application, String> column = new TableColumn<>("Name");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        column.setMinWidth(80.0d);
        column.setSortable(false);

        return column;
    }

    public TableColumn<Application, String> buildExecutableColumn() {
        final TableColumn<Application, String> column = new TableColumn<>("Executable");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(
                data -> {
                    final String filename = FilenameUtils.removeExtension(
                            data.getValue().getExecutablePath().getFileName().toString());
                    return new ReadOnlyStringWrapper(filename);
                });
        column.setMinWidth(80.0d);
        column.setSortable(false);

        return column;
    }

    public TableColumn<Application, String> buildKeywordsColumn() {
        final TableColumn<Application, String> column = new TableColumn<>("Keywords");
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setCellValueFactory(
                data -> new ReadOnlyStringWrapper(data.getValue().getKeywords().toString()));
        column.setMinWidth(80.0d);
        column.setSortable(false);

        return column;
    }

    public HBox buildApplicationButtons() {
        final Button edit = makeTextButton("Edit", event -> {
            final Application application = applicationView.getSelectionModel().getSelectedItem();
            if(application != null) {
                editApplicationWindow.edit(application);
            }
        });

        final Button remove = makeTextButton("Remove", event -> {
            final Application application = applicationView.getSelectionModel().getSelectedItem();
            if(application != null) {
                applications.remove(application);
            }
        });

        final CheckBox checkBox = new CheckBox();
        seeBlacklistedProperty.bind(checkBox.selectedProperty());
        checkBox.setSelected(false);

        final HBox buttons = new HBox(edit, makeVerticalSeparator(),
                                      remove, makeVerticalSeparator(),
                                      new Label("See blacklisted"), checkBox);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        return buttons;
    }

    private void invalidated(final Observable observable) {
        if(!seeBlacklistedProperty.get())
            applicationView.setItems(applications.filtered(item -> !item.isBlacklisted()));
        else applicationView.setItems(applications);
    }
}
