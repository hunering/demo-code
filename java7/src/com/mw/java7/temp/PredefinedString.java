package com.mw.java7.temp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PredefinedString {

	public static void main(String[] args) throws IOException {
		Path file1 = Paths.get("c:\\temp\\consoledb\\auto20.txt");
		Path file2 = Paths.get("c:\\temp\\consoledb\\auto21.txt");
		Path fileSorted1 = Paths.get("c:\\temp\\consoledb\\auto20-s.txt");
		Path fileSorted2 = Paths.get("c:\\temp\\consoledb\\auto21-s.txt");
		
		ArrayList<String> fileCon1 = new ArrayList<String>();
		ArrayList<String> fileCon2 = new ArrayList<String>();
		BufferedReader reader = Files
				.newBufferedReader(file1);

		processCon(reader, fileCon1);
		
		reader = Files
				.newBufferedReader(file2);
		processCon(reader, fileCon2);

		BufferedWriter writer = Files.newBufferedWriter(fileSorted1);
		save2File(writer, fileCon1);
		
		writer = Files.newBufferedWriter(fileSorted2);
		save2File(writer, fileCon2);
		
	}
	
	static void processCon(BufferedReader reader, ArrayList<String> fileCon) throws IOException {
		String line = reader.readLine();
		while(line != null) {
			fileCon.add(line.substring(line.indexOf('	')));
			line = reader.readLine();
		}
		fileCon.sort(null);
	}
	
	static void save2File(BufferedWriter writer, ArrayList<String> fileCon) throws IOException {
		for(String str : fileCon) {
			writer.write(str);
			writer.newLine();
		}
		
		writer.flush();
	}

}
