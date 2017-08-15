package com.xmo.demo.java7.buffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ReaderTest {
	public static void main(String... args) {

		Charset charset = Charset.forName("US-ASCII");
		try {
			BufferedReader reader = Files.newBufferedReader(
					Paths.get("src\\com\\intel\\java7\\buffer\\ByteBufferExample.java"),
					charset);
			InputStream inputStream = Files.newInputStream(
					Paths.get("src\\com\\intel\\java7\\buffer\\ByteBufferExample.java"),
					StandardOpenOption.READ);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
