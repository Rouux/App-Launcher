package org.roux.application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Application {

    private Path executablePath;
    private String name;
    private List<String> keywords = new ArrayList<>();
    private final BooleanProperty isBlacklisted = new SimpleBooleanProperty(false);

    private static String beautifyName(final String name) {
        return name.replaceAll(".exe$", "")
                .replaceAll("(.*)(\\\\)(.*)", "$3")
                .replaceAll("([^A-Z])(\\.)([a-zA-Z])", "$1 $3")
                .replaceAll("([0-9a-z])([A-Z])", "$1 $2")
                .replaceAll("([0-9])([a-zA-Z])", "$1 $2")
                .replaceAll("_", " ");
    }

    public static Application copy(final Application source) {
        final Application copy = new Application(source.getExecutablePath().toString(),
                                                 source.getName());
        copy.getKeywords().addAll(source.getKeywords());
        copy.setBlacklisted(source.isBlacklisted());

        return copy;
    }

    public Application(final Path path, final String name, final String... keywords) {
        executablePath = path;
        this.name = Application.beautifyName(name);
        if(keywords != null && keywords.length > 0) {
            this.keywords.addAll(Arrays.asList(keywords));
        }
    }

    public Application(final String path, final String name, final String... keywords) {
        this(Paths.get(path), name, keywords);
    }

    public Path getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(final String executablePath) {
        setExecutablePath(Paths.get(executablePath));
    }

    public void setExecutablePath(final Path executablePath) {
        this.executablePath = executablePath;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(final List<String> keywords) {
        this.keywords = keywords;
    }

    public BooleanProperty isBlacklistedProperty() {
        return isBlacklisted;
    }

    public boolean isBlacklisted() {
        return isBlacklisted.get();
    }

    public void setBlacklisted(final boolean isBlacklisted) {
        this.isBlacklisted.set(isBlacklisted);
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final Application application = (Application) o;
        return executablePath.equals(application.executablePath)
                && name.equals(application.name)
                && isBlacklisted == application.isBlacklisted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(executablePath, name, isBlacklisted);
    }

    @Override
    public String toString() {
        final String blacklisted = isBlacklisted() ? "[BLACKLISTED] " : "";
        return blacklisted + "'" + name + "' -> '" + executablePath + "' : keywords = " + keywords;
    }
}
