package com.intel.java7.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopyFiles {
	static String listfileName = "C:\\Temp\\language\\file_list.txt";
	static String destFoler = "C:\\Temp\\language";
	
	public static void main(String[] args) throws IOException {
		Path listFilePath = Paths.get(listfileName);
		BufferedReader reader = Files
				.newBufferedReader(listFilePath);
		String fileName = reader.readLine();
		while(fileName != null) {
			copyOneFile(fileName, destFoler);
			fileName = reader.readLine();
		}
	}
	
	public static void copyOneFile(String fileName, String destFolder) throws IOException {
		Path filePath = Paths.get(fileName);
		Path destFolderPath = Paths.get(destFolder);
		final int len = filePath.getNameCount();
		Path fileRealName = filePath.subpath(len-5, len);//.getFileName();
		Path destFileName = destFolderPath.resolve(fileRealName);
		Files.createDirectories(destFileName.getParent());
		Files.copy(filePath,destFileName);
	}
}
