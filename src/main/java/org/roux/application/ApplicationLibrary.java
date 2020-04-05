package org.roux.application;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;
import org.roux.utils.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationLibrary {

    private static final String[] EXTENSIONS = {"exe"};

    private final ObservableList<Application> library = FXCollections.observableArrayList();

    public static boolean isExtensionAllowed(final String file) {
        return FilenameUtils.isExtension(file, ApplicationLibrary.EXTENSIONS);
    }

    public ApplicationLibrary() {
        // Check if they are applications in the data file
        final List<Map<String, Object>> data = FileManager.getApplications();
        if(data != null) {
            data.forEach(map -> {
                final Application application = convertMapToApplication(map);
                library.add(application);
            });
        }
        library.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    }

    public void updateLibrary(final List<Path> filesFromFolders) {
        final List<Application> newApplications = new ArrayList<>();
        final Map<Path, String> executables = mergeExecutables(filesFromFolders);
        for(final Map.Entry<Path, String> entry : executables.entrySet()) {
            final Path path = entry.getKey();
            final String name = entry.getValue();
            Application application;
            if((application = findSamePathApplication(path)) == null) {
                application = new Application(path, name);
                application.setBlacklisted(isBlacklisted(application.getExecutablePath()));
            }
            newApplications.add(application);
        }
        // Coucou moi du futur, si je veux plus tard ouvrir une fenetre après scan
        // scan retourner la liste newApplications
        newApplications.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        library.setAll(newApplications);
        //        return library;
    }

    private Map<Path, String> mergeExecutables(final List<Path> filesFromFolders) {
        final Map<Path, String> results = new HashMap<>();
        filesFromFolders.forEach(path -> results.put(path, deductName(path)));
        FileManager.getExecutables()
                .forEach(executable -> results.put(Paths.get(executable),
                                                   FilenameUtils.removeExtension(executable)));
        return results;
    }

    private static String deductName(final Path path) {
        final String firstFolderPath = FileManager.getFolders().stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(path.getRoot().toString());
        return Paths.get(firstFolderPath).relativize(path).getName(0).toString();
    }

    private Application findSamePathApplication(final Path path) {
        return library.stream()
                .filter(app -> app.getExecutablePath().equals(path))
                .findFirst()
                .orElse(null);
    }

    public List<String> filter(final SortedSet<String> entries, final String inputText) {
        final List<String> filteredEntries = new ArrayList<>();
        filteredEntries.addAll(
                library.stream()
                        .filter(application -> application.getKeywords().contains(inputText))
                        .map(Application::getName)
                        .collect(Collectors.toList())
        );
        filteredEntries.addAll(
                entries.stream()
                        .filter(entry -> !filteredEntries.contains(entry))
                        .filter(entry -> entry.toLowerCase().startsWith(inputText.toLowerCase()))
                        .collect(Collectors.toList())
        );
        filteredEntries.addAll(
                entries.stream()
                        .filter(entry -> !filteredEntries.contains(entry))
                        .filter(entry -> entry.toLowerCase().contains(inputText.toLowerCase()))
                        .collect(Collectors.toList())
        );
        return filteredEntries;
    }

    public List<Map<String, Object>> getLibraryAsJsonFriendly() {
        return library.stream()
                .map(ApplicationLibrary::convertApplicationToMap)
                .collect(Collectors.toList());
    }

    public static Map<String, Object> convertApplicationToMap(final Application application) {
        final Map<String, Object> map = new HashMap<>();
        map.put("name", application.getName());
        map.put("path", application.getExecutablePath().toString());
        map.put("keywords", application.getKeywords());
        map.put("blacklisted", application.isBlacklisted());

        return map;
    }

    public static Application convertMapToApplication(final Map<String, Object> map) {
        final String name = map.getOrDefault("name", "").toString();
        final String path = map.getOrDefault("path", "").toString();
        final Application application = new Application(path, name);
        final boolean blacklisted = (boolean) map.getOrDefault("blacklisted", false);
        application.setBlacklisted(blacklisted);
        final List<String> keywords =
                (List<String>) map.getOrDefault("keywords", new ArrayList<String>());
        application.setKeywords(keywords);

        return application;
    }

    public static boolean isBlacklisted(final String path) {
        return FileManager.getBlacklist().stream().anyMatch(path::startsWith);
    }

    public static boolean isBlacklisted(final Path path) {
        return isBlacklisted(path.toString());
    }

    public Application getApplication(final String name) {
        return library.stream()
                .filter(application -> application.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<String> getNames(final boolean seeBlacklisted) {
        return library.stream()
                .filter(application -> (seeBlacklisted || !application.isBlacklisted()))
                .map(Application::getName)
                .collect(Collectors.toList());
    }

    public void addListener(final ListChangeListener<Application> listener) {
        library.addListener(listener);
    }

    /**
     * A way to make the application safer
     *
     * @return a list made of copies of the application !
     */
    public List<Application> getLibraryCopies() {
        return library.stream()
                .map(Application::copy)
                .collect(Collectors.toList());
    }

    public void setLibrary(final ObservableList<Application> library) {
        this.library.setAll(library);
    }
}
