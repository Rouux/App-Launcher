package org.roux.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.roux.game.GameLibrary;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileManager {

    private static final List<String> banned = new ArrayList<>();
    private static final List<String> folders = new ArrayList<>();

    public static Integer DEFAULT_MAX_ENTRIES = 10;
    public static Integer MAX_ENTRIES;

    private static JSONObject root;

    static {
        parse();
    }

    public static JSONArray getJsonArray(String key) {
        Object result = root.get(key);
        return result == null ? null : (JSONArray) result;
    }

    public static void parse() {
        try(Reader reader = new FileReader("config.json")) {
            JSONParser parser = new JSONParser();
            root = (JSONObject) parser.parse(reader);

            Object maxEntries = root.get("maxEntries");
            if(maxEntries != null) {
                MAX_ENTRIES = ((Long) maxEntries).intValue();
            } else {
                MAX_ENTRIES = DEFAULT_MAX_ENTRIES;
            }

            JSONArray banned = getJsonArray("banned");
            if(banned != null) {
                banned.forEach(filename -> FileManager.banned.add(filename.toString()));
            }

            JSONArray folders = getJsonArray("folders");
            if(folders != null) {
                folders.forEach(folder -> FileManager.folders.add(folder.toString()));
            }
        } catch(IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, List<Path>> getFilesInFolders(Predicate<Path> pathPredicate) {
        final Map<String, List<Path>> files = new HashMap<>();
        getFolders().forEach(folderName -> {
            try {
                Path folderPath = Paths.get(folderName);
                if(folderPath.toFile().exists()) {
                    if(folderPath.toFile().isFile()) {
                        String name = folderPath.getFileName().toString();
                        files.computeIfAbsent(name, k -> new ArrayList<>());
                        files.get(name).add(folderPath);
                    } else {
                        int index = folderPath.getNameCount();
                        List<Path> list = Files.walk(folderPath)
                                .filter(path -> path.toFile().isFile())
                                .filter(path -> path.toFile().canExecute())
                                .filter(path -> !banned.contains(path.getFileName().toString()))
                                .filter(pathPredicate)
                                .collect(Collectors.toList());
                        for(Path p : list) {
                            String name = p.getName(index).toString();
                            files.computeIfAbsent(name, k -> new ArrayList<>());
                            files.get(name).add(p);
                        }
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        return files;
    }

    public static void save(GameLibrary gameLibrary) {
        Map<String, Object> data = new HashMap<>();
        data.put("maxEntries", MAX_ENTRIES);
        data.put("folders", folders);
        data.put("banned", banned);
        data.put("games", gameLibrary.getLibraryAsJsonArray());
        JSONObject jsonObject = new JSONObject(data);
        try(FileWriter file = new FileWriter("config.json")) {
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFolders() {
        return FileManager.folders;
    }

    public static void updateFolders(Collection<String> newFolders) {
        FileManager.folders.clear();
        FileManager.folders.addAll(newFolders);
    }

    public static List<String> getBanned() {
        return FileManager.banned;
    }

    public static void updateBanned(Collection<String> newBanned) {
        FileManager.banned.clear();
        FileManager.banned.addAll(newBanned);
    }
}
