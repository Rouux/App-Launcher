package org.roux.utils;

import javafx.geometry.Side;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.roux.FileManager;
import org.roux.game.GameLibrary;
import org.roux.window.MainWindow;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 *
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField {
    /** The existing autocomplete entries. */
    private final SortedSet<String> entries;

    private List<String> filteredEntries;
    /** The popup used to select an entry. */
    private MaxSizedContextMenu entriesPopup;

    /** Construct a new AutoCompleteTextField. */
    public AutoCompleteTextField(MainWindow mainWindow, GameLibrary gameLibrary) {
        super();
        entries = new TreeSet<>();
        entriesPopup = new MaxSizedContextMenu();
        entriesPopup.setOpacity(0.9);
        textProperty().addListener((observableValue, s, s2) -> {
            if(getText().length() == 0) {
                //                entriesPopup.hide();
                entriesPopup.getItems().clear(); // CA MARCHE JE SAIS PAS POURQUOI MAIS TU TOUCHE PAS !!!!
            } else {
                filteredEntries = gameLibrary.filter(entries, getText());
                if(entries.size() > 0) {
                    populatePopup(filteredEntries);
                    if(!entriesPopup.isShowing()) {
                        entriesPopup.setMinWidth(AutoCompleteTextField.this.getPrefWidth());
                        entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });
        setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ENTER) {
                List<String> list = getFilteredEntries();
                if(list != null && !list.isEmpty()) {
                    mainWindow.launchGame(list.get(0));
                }
                System.out.println(list);
            }
        });

        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> entriesPopup.hide());

    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() { return entries; }

    public List<String> getFilteredEntries() { return filteredEntries; }

    /**
     * Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        int count = Math.min(searchResult.size(), FileManager.MAX_ENTRIES);
        for(int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
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