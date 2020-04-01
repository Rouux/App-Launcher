package org.roux.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;
import org.roux.utils.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationLibrary {

    private static final String[] EXTENSIONS = {"exe"};

    public static boolean isExtensionAllowed(final String file) {
        return FilenameUtils.isExtension(file, ApplicationLibrary.EXTENSIONS);
    }

    private final ObservableList<Application> library = FXCollections.observableArrayList();

    public ApplicationLibrary() {
        // Check if they are applications in the data file
        final List<Map<String, Object>> data = FileManager.getApplications();
        if(data != null) {
            data.forEach(map -> {
                final Application application = convertMapToApplication(map);
                library.add(application);
            });
        }
    }

    public Map<Path, String> getExecutables() {
        final List<Path> files = FileManager.getFiles();
        final Map<Path, String> results = new HashMap<>();
        files.stream() //@todo remplacer par des banwords btw
                .filter(path -> !path.getFileName().toString().contains("redist"))
                .filter(path -> !path.getFileName().toString().contains("dxsetup"))
                .filter(path -> !path.getFileName().toString().contains("unins"))
                .filter(path -> !(path.getFileName().toString().contains("crash")
                        && path.getFileName().toString().contains("report")))
                .filter(path -> !path.toString().contains("Steamworks Shared"))
                .filter(path -> !path.toString().contains("Resources"))
                .filter(path -> !path.toString().contains("resources"))
                .filter(path -> !path.toString().contains("lib"))
                .forEach(path -> results.put(path, deductName(path)));
        FileManager.getExecutables().forEach(
                executable -> results.put(Paths.get(executable),
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

    public ObservableList<Application> scan() {
        final List<Application> newApplications = new ArrayList<>();
        final Map<Path, String> executables = getExecutables();
        for(final Map.Entry<Path, String> entry : executables.entrySet()) {
            final Path path = entry.getKey();
            final String name = entry.getValue();
            Application application;
            if((application = findSamePathApplication(path)) == null) {
                application = new Application(path, name);
            }
            //@todo actuellement j'affiche juste pas, retravailler pour afficher quand même ?
            // Histoire d'eviter que le mec se demande pourquoi son app s'affiche pas
            application.setBlacklisted(isBlacklisted(application.getExecutablePath()));
            if(!application.isBlacklisted()) newApplications.add(application);
        }
        // Coucou moi du futur, si je veux plus tard ouvrir une fenetre après scan
        // scan retourner la liste newApplications
        library.setAll(newApplications);
        return library;
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

    public ObservableList<Application> getLibrary() {
        return library;
    }
}
