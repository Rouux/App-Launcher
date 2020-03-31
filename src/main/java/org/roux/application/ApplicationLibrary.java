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
        Map<String, JSONObject> applicationsJson = getApplicationsJson();
        if(applicationsJson != null && !applicationsJson.isEmpty()) {
            applicationsJson.forEach((name, jsonObject) -> {
                String path = jsonObject.get("path").toString();
                String[] keywords = (String[]) ((JSONArray) jsonObject.get("keywords")).toArray(new String[0]);
                Application application = new Application(path, name, keywords);
                library.add(application);
            });
        }
    }

    public Map<String, Path> gatherExecutables() {
        Map<String, List<Path>> files = FileManager.getFilesInFolders(path -> {
            String filename = path.getFileName().toString();
            for(String extension : EXTENSIONS) {
                if(filename.endsWith(extension)) return true;
            }
            return false;
        });
        Map<String, Path> results = new HashMap<>();
        for(Map.Entry<String, List<Path>> entry : files.entrySet()) {
            entry.getValue().stream()
                    .filter(path -> !path.getFileName().toString().contains("redist"))
                    .filter(path -> !path.getFileName().toString().contains("dxsetup"))
                    .filter(path -> !path.toString().contains("Steamworks Shared"))
                    .filter(path -> !path.toString().contains("Resources"))
                    .findFirst()
                    .ifPresent(singlePath -> results.put(entry.getKey(), singlePath));
        }
        FileManager.getExecutables().forEach(executable -> results.put(executable, Paths.get(executable)));
        return results;
    }

    //@Todo rework, obviously
    public ObservableList<Application> scan() {
        final Map<String, JSONObject> applicationsJson = getApplicationsJson();
        final List<Application> newApplications = new ArrayList<>();
        Map<String, Path> executables = gatherExecutables();
        for(Map.Entry<String, Path> entry : executables.entrySet()) {
            Application application = new Application(entry.getValue(), entry.getKey());
            JSONObject oldApplication;
            if(applicationsJson != null && (oldApplication = applicationsJson.get(application.getName())) != null) {
                application.getKeywords().addAll(((JSONArray) oldApplication.get("keywords")));
            }
            newApplications.add(application);
        }
        this.library.setAll(newApplications);
        return this.library;
    }

    public List<String> filter(final SortedSet<String> entries, String inputText) {
        final List<String> filteredEntries = new ArrayList<>();
        filteredEntries.addAll(
                library.stream()
                        .filter(application -> application.getKeywords().contains(inputText.toLowerCase()))
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
        JSONArray jsonArray = FileManager.getJsonArray("applications");
        if(jsonArray != null) {
            Iterator<JSONObject> iterator = jsonArray.iterator();
            iterator.forEachRemaining(jsonObject -> nameToObjectMap.put(jsonObject.get("name").toString(), jsonObject));
            return nameToObjectMap;
        }
        return null;
    }

    public JSONArray getLibraryAsJsonArray() {
        JSONArray applicationArray = new JSONArray();
        for(Application application : library) {
            JSONObject appJson = new JSONObject();
            appJson.put("path", application.getExecutablePath().toString());
            appJson.put("name", application.getName());
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(application.getKeywords());
            appJson.put("keywords", jsonArray);
            applicationArray.add(appJson);
        }
        return applicationArray;
    }

    public Application getApplication(String name) {
        return library.stream()
                .filter(application -> application.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public ObservableList<Application> getLibrary() {
        return library;
    }
}
