package com.xmo.demo.java7.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ReadWriteBinary {

    public static void main(String[] args) {
       

    }
    
    public void  useFileRead() {
        File file = new File("inputfile.txt");
        FileInputStream fin = null;
        try {
            // create FileInputStream object
            fin = new FileInputStream(file);

            byte fileContent[] = new byte[(int) file.length()];

            // Reads up to certain bytes of data from this input stream into an
            // array of bytes.
            fin.read(fileContent);
            // create string from byte array
            String s = new String(fileContent);
            System.out.println("File content: " + s);
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }
    }
    
    public void useFilesRead() {
        String filePath = "inputfile.txt";
        Path path = Paths.get(filePath);
        try {
            InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ);
            final byte[] buffer = new byte[4096];
            while (inputStream.read(buffer) != -1) {
                // Do something with the data;
            }
        } catch (IOException e) {

        }
    }

}
