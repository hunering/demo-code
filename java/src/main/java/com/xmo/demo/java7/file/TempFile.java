package com.xmo.demo.java7.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class TempFile {
    private void createTempFile() throws IOException {
        Path tempFile = Files.createTempFile("tempfiles", ".tmp");
        List<String> lines = Arrays.asList("Line1", "Line2");
        Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);

        System.out.printf("Wrote text to temporary file %s%n", tempFile.toString());
    }

    private void createTempFileWithDir() throws IOException {
        Path tempDir = Files.createTempDirectory("tempfiles");

        Path tempFile = Files.createTempFile(tempDir, "tempfiles", ".tmp");
        List<String> lines = Arrays.asList("Line1", "Line2");
        Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);

        System.out.printf("Wrote text to temporary file %s%n", tempFile.toString());
    }

    private void createTempFileShutdownHook() throws IOException {
        final Path tempFile = Files.createTempFile("tempfiles-shutdown-hook", ".tmp");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Files.delete(tempFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        List<String> lines = Arrays.asList("Line1", "Line2");
        Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);

        System.out.printf("Wrote text to temporary file %s%n", tempFile.toString());
    }

    private void createTempFileDeleteOnExit() throws IOException {
        Path tempFile = Files.createTempFile("tempfiles-delete-on-exit", ".tmp");
        tempFile.toFile().deleteOnExit();
        List<String> lines = Arrays.asList("Line1", "Line2");
        Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);

        System.out.printf("Wrote text to temporary file %s%n", tempFile.toString());
    }

    private void createTempFileDeleteOnClose() throws IOException {
        Path tempFile = Files.createTempFile("tempfiles-delete-on-close", ".tmp");
        List<String> lines = Arrays.asList("Line1", "Line2");
        Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE,
                StandardOpenOption.DELETE_ON_CLOSE);

        System.out.printf("Wrote text to temporary file %s%n", tempFile.toString());
    }
}
