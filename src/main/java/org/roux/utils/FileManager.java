package org.roux.utils;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.roux.application.ApplicationLibrary;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileManager {

    private static final List<String> BLACKLIST = new ArrayList<>();
    private static final List<String> FOLDERS = new ArrayList<>();
    private static final List<String> EXECUTABLES = new ArrayList<>();

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

    private static File loadData() throws IllegalArgumentException, IOException {
        File file = new File("data.json");
        if(file.exists()) {
            return file;
        }
        System.out.println("Premier lancement !");
        InputStream inputStream = FileManager.class.getClassLoader().getResourceAsStream("preset.json");
        if(inputStream == null) {
            throw new IllegalArgumentException("Preset file not found !");
        }
        FileUtils.copyInputStreamToFile(inputStream, file);
        return file;
    }

    public static void parse() {
        System.out.println("Parse...");
        try(BufferedReader reader = new BufferedReader(new FileReader(loadData()))) {
            JSONParser parser = new JSONParser();
            root = (JSONObject) parser.parse(reader);

            Object maxEntries = root.get("maxEntries");
            MAX_ENTRIES = maxEntries != null ? ((Long) maxEntries).intValue() : DEFAULT_MAX_ENTRIES;

            JSONArray blacklist = getJsonArray("blacklist");
            if(blacklist != null)
                blacklist.forEach(filename -> FileManager.BLACKLIST.add(filename.toString()));

            JSONArray folders = getJsonArray("folders");
            if(folders != null)
                folders.forEach(folder -> FileManager.FOLDERS.add(folder.toString()));

            JSONArray executables = getJsonArray("executables");
            if(executables != null)
                executables.forEach(executable -> FileManager.EXECUTABLES.add(executable.toString()));

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
                    int index = folderPath.getNameCount();
                    List<Path> list = Files.walk(folderPath)
                            .filter(path -> path.toFile().isFile())
                            .filter(path -> path.toFile().canExecute())
                            //                            .filter(path -> !BLACKLIST.contains(path.getFileName()
                            //                            .toString()))
                            .filter(pathPredicate)
                            .collect(Collectors.toList());
                    for(Path p : list) {
                        String name = p.getName(index).toString();
                        files.computeIfAbsent(name, k -> new ArrayList<>());
                        files.get(name).add(p);
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        return files;
    }

    public static void save(ApplicationLibrary applicationLibrary) {
        System.out.println("Saving...");
        Map<String, Object> data = new HashMap<>();
        data.put("maxEntries", MAX_ENTRIES);
        data.put("folders", FOLDERS);
        data.put("executables", EXECUTABLES);
        data.put("blacklist", BLACKLIST);
        data.put("applications", applicationLibrary.getLibraryAsJsonArray());
        JSONObject jsonObject = new JSONObject(data);
        try(PrintWriter writer = new PrintWriter(new File("data.json"))) {
            writer.print(jsonObject.toJSONString());
            writer.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFolders() { return FileManager.FOLDERS; }

    public static void setFolders(Collection<String> folders) {
        FileManager.FOLDERS.clear();
        FileManager.FOLDERS.addAll(folders);
    }

    public static List<String> getExecutables() { return FileManager.EXECUTABLES; }

    public static void setExecutables(Collection<String> executables) {
        FileManager.EXECUTABLES.clear();
        FileManager.EXECUTABLES.addAll(executables);
    }

    public static List<String> getBlacklist() {
        return FileManager.BLACKLIST;
    }

    public static void setBlacklist(Collection<String> blacklist) {
        FileManager.BLACKLIST.clear();
        FileManager.BLACKLIST.addAll(blacklist);
    }
}
