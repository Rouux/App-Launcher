package org.roux.utils;

import javafx.geometry.Side;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.roux.application.ApplicationLibrary;
import org.roux.window.MainWindow;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied
 * list of entries.
 *
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField {

    private final SortedSet<String> entries;
    private List<String> filteredEntries;
    private final MaxSizedContextMenu entriesPopup;

    public AutoCompleteTextField(final MainWindow mainWindow,
                                 final ApplicationLibrary applicationLibrary) {
        super();
        entries = new TreeSet<>();
        entriesPopup = new MaxSizedContextMenu();
        entriesPopup.setOpacity(0.9);
        textProperty().addListener((observableValue, s, s2) -> {
            if(getText().length() == 0) {
                //                entriesPopup.hide();
                // !! CA MARCHE JE SAIS PAS POURQUOI MAIS TU TOUCHE PAS !!!
                entriesPopup.getItems().clear();
            } else {
                filteredEntries = applicationLibrary.filter(entries, getText());
                if(entries.size() > 0) {
                    populatePopup(filteredEntries);
                    if(!entriesPopup.isShowing()) {
                        entriesPopup.setMinWidth(getPrefWidth());
                        entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });
        setOnKeyPressed(ke -> {
            final List<String> list = getFilteredEntries();
            System.out.println(list);
            if(ke.getCode() == KeyCode.ENTER) {
                if(list != null && !list.isEmpty()) {
                    mainWindow.launchApplication(list.get(0));
                }
            } else if(ke.getCode() == KeyCode.TAB) {
                if(list != null && !list.isEmpty()) {
                    setText(list.get(0));
                }
                requestFocus();
                positionCaret(getText().length());
                entriesPopup.hide();
            }
        });
        focusedProperty().addListener((observable) -> entriesPopup.hide());
    }

    public SortedSet<String> getEntries() { return entries; }

    public List<String> getFilteredEntries() { return filteredEntries; }

    private void populatePopup(final List<String> searchResult) {
        final List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        final int count = Math.min(searchResult.size(), FileManager.MAX_ENTRIES);
        for(int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            final Label entryLabel = new Label(result);
            final CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(actionEvent -> {
                setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }
}