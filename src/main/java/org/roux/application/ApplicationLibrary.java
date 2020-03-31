package org.roux.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.roux.utils.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationLibrary {

    private static final String[] EXTENSIONS = {
            ".exe"
    };

    private final ObservableList<Application> library = FXCollections.observableArrayList();

    public ApplicationLibrary() {
        // Check if they are applications already to put in library
        final Map<String, JSONObject> applicationsJson = getApplicationsJson();
        if(applicationsJson != null && !applicationsJson.isEmpty()) {
            applicationsJson.forEach((name, jsonObject) -> {
                final String path = jsonObject.get("path").toString();
                final JSONArray jsonArray = (JSONArray) jsonObject.get("keywords");
                final String[] keywords = (String[]) jsonArray.toArray(new String[0]);
                final Application application = new Application(path, name, keywords);
                library.add(application);
            });
        }
    }

    public Map<String, Path> gatherExecutables() {
        final Map<String, List<Path>> files = FileManager.getFilesInFolders(path -> {
            final String filename = path.getFileName().toString();
            for(final String extension : EXTENSIONS) {
                if(filename.endsWith(extension)) return true;
            }
            return false;
        });
        final Map<String, Path> results = new HashMap<>();
        for(final Map.Entry<String, List<Path>> entry : files.entrySet()) {
            entry.getValue().stream()
                    .filter(path -> !path.getFileName().toString().contains("redist"))
                    .filter(path -> !path.getFileName().toString().contains("dxsetup"))
                    .filter(path -> !path.toString().contains("Steamworks Shared"))
                    .filter(path -> !path.toString().contains("Resources"))
                    .findFirst()
                    .ifPresent(singlePath -> results.put(entry.getKey(), singlePath));
        }
        FileManager.getExecutables().forEach(
                executable -> results.put(executable, Paths.get(executable)));
        return results;
    }

    //@Todo rework, obviously
    public ObservableList<Application> scan() {
        final Map<String, JSONObject> applicationsJson = getApplicationsJson();
        final List<Application> newApplications = new ArrayList<>();
        final Map<String, Path> executables = gatherExecutables();
        for(final Map.Entry<String, Path> entry : executables.entrySet()) {
            final Application application = new Application(entry.getValue(), entry.getKey());
            final JSONObject oldApplication = applicationsJson.get(application.getName());
            if(oldApplication != null) {
                application.getKeywords().addAll(((JSONArray) oldApplication.get("keywords")));
            }
            newApplications.add(application);
        }
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

    public Map<String, JSONObject> getApplicationsJson() {
        final Map<String, JSONObject> nameToObjectMap = new HashMap<>();
        final JSONArray jsonArray = FileManager.getJsonArray("applications");
        if(jsonArray != null) {
            final Iterator<JSONObject> iterator = jsonArray.iterator();
            iterator.forEachRemaining(
                    jsonObject -> nameToObjectMap.put(jsonObject.get("name").toString(),
                                                      jsonObject));
        }
        return nameToObjectMap;
    }

    public JSONArray getLibraryAsJsonArray() {
        final JSONArray applicationArray = new JSONArray();
        for(final Application application : library) {
            final JSONObject appJson = new JSONObject();
            appJson.put("path", application.getExecutablePath().toString());
            appJson.put("name", application.getName());
            final JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(application.getKeywords());
            appJson.put("keywords", jsonArray);
            applicationArray.add(appJson);
        }
        return applicationArray;
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
