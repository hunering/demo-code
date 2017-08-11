package com.xmo.demo.java7.regex;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestHarness {
	public static void main(String[] args) {

		while (true) {

			StringBuilder sb = new StringBuilder();
			// Send all output to the Appendable object sb
			Formatter formatter = new Formatter(sb, Locale.US);

			Pattern pattern;
			try {
				pattern = Pattern.compile(readLine("%nEnter your regex: "));
				Matcher matcher = pattern
						.matcher(readLine("Enter input string to search: "));

				boolean found = false;
				while (matcher.find()) {

					formatter.format("I found the text"
							+ " \"%s\" starting at "
							+ "index %d and ending at index %d.%n",
							matcher.group(), matcher.start(), matcher.end());
					found = true;
				}
				if (!found) {
					formatter.format("No match found.%n");
				}
				System.out.print(sb);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private static String readLine(String format, Object... args)
			throws IOException {
		if (System.console() != null) {
			return System.console().readLine(format, args);
		}
		System.out.print(String.format(format, args));
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		return reader.readLine();
	}
}
