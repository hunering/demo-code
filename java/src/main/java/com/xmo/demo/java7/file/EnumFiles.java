package com.xmo.demo.java7.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EnumFiles {

	public static void main(String[] args) {
		Path path = Paths.get("C:\\Allen\\Dropbox\\git\\samples\\Java7\\src\\com\\intel\\java7\\nio\\asynchronous");

		List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path pathItem : directoryStream) {
            	if(!Files.isDirectory(pathItem)) {
            		fileNames.add(pathItem.toString());
            	}
            }
        } catch (IOException ex) {}
        
        
        System.out.println(fileNames);
        
		
	}

}
