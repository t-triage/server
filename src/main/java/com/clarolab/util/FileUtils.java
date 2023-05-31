package com.clarolab.util;

import com.google.common.collect.Maps;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.logging.Level;

@Log
public class FileUtils {

    public static File rename(File origin, String newName) throws IOException {
        Path originalFile = Paths.get(origin.getAbsolutePath());
        Files.move(originalFile, originalFile.resolveSibling(newName));
        return new File(originalFile.getParent()+"/"+newName);
    }

    public static Map<String, String> getFiles(File file) throws IOException {
        return getFiles(file.getAbsolutePath());
    }

    public static Map<String, String> getFiles(File file, String dir) throws IOException {
        return getFiles(file.getAbsolutePath()+dir);
    }

    public static String getFile(File file, String dir, String fileName) throws IOException {
        return getFiles(file.getAbsolutePath()+dir).get(fileName);
    }

    public static Map<String, String> getFiles(String dir) throws IOException {
        Map<String, String> files = Maps.newHashMap();
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!Files.isDirectory(file)) {
                    //String content = new String(Files.readAllBytes(Paths.get(file.toAbsolutePath().toString())));
                    //files.put(file.getFileName().toString(), content);
                    String content = org.apache.commons.io.FileUtils.readFileToString(new File(file.toAbsolutePath().toString()), "UTF-8");
                    files.put(file.getFileName().toString(), content);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    public static boolean itContainsFolder(File file, String folder) throws IOException {
        return Files.walk(Paths.get(file.getAbsolutePath())).filter(Files::isDirectory).filter(directory -> directory.getFileName().toString().equals(folder)).findFirst().orElse(null) != null;
    }

    public static void delete(File file) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            log.log(Level.WARNING, String.format("[delete] : %s could not be removed.",file.getName()), e);
        }
    }

    public static void deleteOnExit(File file) {
        try {
            org.apache.commons.io.FileUtils.forceDeleteOnExit(file);
        } catch (IOException e) {
            log.log(Level.WARNING, String.format("[deleteOnExit] : %s could not be removed.",file.getName()), e);
        }
    }

}
