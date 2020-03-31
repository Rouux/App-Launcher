package org.roux.application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Application {

    private Path executablePath;
    private String name;
    private List<String> keywords;

    public Application() {}

    public static void main(String[] args) {
        System.out.println(beautifyName("DOOM Eternal"));
        System.out.println(beautifyName("Worms.W.M.D"));
        System.out.println(beautifyName("WorldWarZ"));
        System.out.println(beautifyName("Getting.Over.It.with.Bennett.Foddy.v1.5762"));
        System.out.println(beautifyName("C:\\Games\\DOOM Eternal\\DOOMEternalx64vk.exe"));
    }

    private static String beautifyName(String name) {
        return name.replaceAll(".exe$", "")
                .replaceAll("(.*)(\\\\)(.*)", "$3")
                .replaceAll("([^A-Z])(\\.)([a-zA-Z])", "$1 $3")
                .replaceAll("([0-9a-z])([A-Z])", "$1 $2")
                .replaceAll("([0-9])([a-zA-Z])", "$1 $2")
                .replaceAll("_", " ");
    }

    public Application(Path path, String name, String... keywords) {
        this.executablePath = path;
        this.name = Application.beautifyName(name);
        this.keywords = new ArrayList<>();
        if(keywords != null && keywords.length > 0) {
            this.keywords.addAll(Arrays.asList(keywords));
        }
    }

    public Application(String path, String name, String... keywords) {
        this(Paths.get(path), name, keywords);
    }

    public static String computeName(Path path) {
        return path.getFileName().toString().split("\\.")[0];
    }

    public Path getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(Path executablePath) {
        this.executablePath = executablePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        Application application = (Application) o;
        return executablePath.equals(application.executablePath) && name.equals(application.name);
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
