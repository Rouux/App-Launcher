package org.roux.game;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Game {

    private Path executablePath;
    private String name;
    private List<String> keywords;

    public Game() {}

    private static String beautifyName(String name) {
        return name.replaceAll("([0-9a-z])([A-Z])", "$1 $2")
                .replaceAll("([0-9])([a-zA-Z])", "$1 $2")
                .replaceAll("_", " ");
    }

    public Game(Path path, String name, String... keywords) {
        this.executablePath = path;
        this.name = Game.beautifyName(name);
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
        System.out.println();
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
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return executablePath.equals(game.executablePath) && name.equals(game.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executablePath, name);
    }

    @Override
    public String toString() {
        return "[" + name + "] -> " + executablePath + ", keywords = " + keywords;
    }
}
