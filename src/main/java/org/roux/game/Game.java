package org.roux.game;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

    private Path executablePath;
    private String name;
    private List<String> keywords;

    public Game() {}

    public Game(Path path, String name, String... keywords) {
        this.executablePath = path;
        this.name = name;
        this.keywords = new ArrayList<>();
        if(keywords != null && keywords.length > 0) {
            this.keywords.addAll(Arrays.asList(keywords));
        }
    }

    public Game(String path, String name, String... keywords) {
        this(Paths.get(path), name, keywords);
    }

    public Game(Path path) {
        this(path, computeName(path));
    }

    public Game(String path) {
        this(Paths.get(path));
    }

    public static String computeName(Path path) {
        return path.getFileName().toString().split("\\.")[0];
    }

    public Path getExecutablePath() {
        return executablePath;
    }

    public String getName() {
        return name;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "[" + name + "] -> " + executablePath + ", keywords = " + keywords;
    }
}
