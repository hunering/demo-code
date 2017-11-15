package com.xmo.demo.java7.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateDirectory {

    public static void main(String[] args) throws IOException {
        String target = "temp/html/filename";
        Path path = Paths.get(target);
        if (Files.deleteIfExists(path)) {
            System.out.println("Deleted existing file \"" + target + "\".");
        }
        System.out.println("Target file \"" + target + "\" will be created.");
        // the following line will create temp/html
        Files.createDirectories(path.getParent());
        File file = Files.createFile(path).toFile();
        file.getPath();
    }

}
