package org.roux.utils;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.roux.application.ApplicationLibrary;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ScannerTool extends Task<String> {

    private final Predicate<Path> visitPredicate = path -> {
        return !folderContainsBanWord(path);
    };

    private final Predicate<Path> filePredicate = path -> {
        return ApplicationLibrary.isExtensionAllowed(path.toString())
                && path.toFile().canExecute() && !executableContainsBanWord(path);
    };

    private final LongProperty longProperty;
    private final StringProperty stringProperty;
    private RecursiveConfig recursiveConfig;

    private long fileCount = 0;
    private List<Path> files = new ArrayList<>();

    public ScannerTool() {
        longProperty = new SimpleLongProperty();
        stringProperty = new SimpleStringProperty();
    }

    @Override
    protected String call() {
        recursiveConfig =
                new RecursiveConfig(visitPredicate, filePredicate, longProperty, stringProperty);
        fileCount = countFiles(recursiveConfig);
        longProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue != null)
                updateProgress(newValue.longValue(), fileCount);
        });
        stringProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue != null)
                updateValue(stringProperty.get());
        });
        files = scan(recursiveConfig);
        return null;
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        recursiveConfig.cancel();
    }

    public List<Path> scan(final RecursiveConfig recursiveConfig) {
        recursiveConfig.setScanType(ScanType.GATHER_FILES);
        final List<Path> folders = FileManager.getFolders().stream()
                .map(folder -> Paths.get(folder))
                .filter(path -> path.toFile().isDirectory())
                .collect(Collectors.toList());
        for(final Path folder : folders) {
            walk(folder, recursiveConfig);
        }
        return recursiveConfig.getFiles();
    }

    public long countFiles(final RecursiveConfig recursiveConfig) {
        recursiveConfig.setScanType(ScanType.COUNT_FILES);
        final List<Path> folders = FileManager.getFolders().stream()
                .map(folder -> Paths.get(folder))
                .filter(path -> path.toFile().isDirectory())
                .collect(Collectors.toList());

        for(final Path folder : folders) {
            walk(folder, recursiveConfig);
        }
        return recursiveConfig.getFilesCount();
    }

    private void walk(final Path start,
                      final RecursiveConfig recursiveConfig) {
        try {
            Files.walkFileTree(start, recursiveConfig);
        } catch(final IOException exception) {
            exception.printStackTrace();
        }
    }

    public List<Path> getFiles() {
        if(recursiveConfig.isCancelled) return null;
        return files;
    }

    public long getFilesCount() {
        return fileCount;
    }

    private static boolean folderContainsBanWord(final Path folder) {
        return FileManager.getBanWordFolders().parallelStream()
                .anyMatch(s -> folder.toString().contains(s));
    }

    private static boolean executableContainsBanWord(final Path executable) {
        return FileManager.getBanWordExecutables().parallelStream()
                .anyMatch(s -> executable.getFileName().toString().contains(s));
    }

    private static class RecursiveConfig implements FileVisitor<Path> {

        private final LongProperty longProperty;
        private final StringProperty stringProperty;
        private final Predicate<Path> visitFolderPredicate;
        private final Predicate<Path> visitFilePredicate;
        private ScanType scanType = ScanType.COUNT_FILES;

        private final List<Path> files = new ArrayList<>();
        private long filesCount = 0L;
        private boolean isCancelled = false;

        public RecursiveConfig(final Predicate<Path> visitFolderPredicate,
                               final Predicate<Path> visitFilePredicate,
                               final LongProperty longProperty,
                               final StringProperty stringProperty) {
            this.visitFolderPredicate = visitFolderPredicate;
            this.visitFilePredicate = visitFilePredicate;
            this.longProperty = longProperty;
            this.stringProperty = stringProperty;
        }

        public void init() {
            files.clear();
            filesCount = 0;
        }

        public void cancel() {
            isCancelled = true;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
            if(isCancelled) return FileVisitResult.TERMINATE;

            if(visitFolderPredicate.test(dir))
                return FileVisitResult.CONTINUE;
            return FileVisitResult.SKIP_SUBTREE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
            if(isCancelled) return FileVisitResult.TERMINATE;

            if(scanType.equals(ScanType.GATHER_FILES)) {
                stringProperty.set(file.toString());
                longProperty.set(longProperty.get() + 1);
                if(visitFilePredicate.test(file)) {
                    files.add(file);
                }
            } else if(scanType.equals(ScanType.COUNT_FILES)) {
                filesCount++;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException exc) {
            if(isCancelled) return FileVisitResult.TERMINATE;

            if(scanType.equals(ScanType.GATHER_FILES))
                System.out.println("failed : " + file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) {
            if(isCancelled) return FileVisitResult.TERMINATE;

            return FileVisitResult.CONTINUE;
        }

        public void setScanType(final ScanType scanType) {
            this.scanType = scanType;
        }

        public List<Path> getFiles() {
            return files;
        }

        public long getFilesCount() {
            return filesCount;
        }
    }

    enum ScanType {
        COUNT_FILES, GATHER_FILES
    }
}