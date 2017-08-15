package com.xmo.demo.java7.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplit {

	public static void main(String[] args) {
		String delimiters = "((?m)^\\d+\\.*)";
		
		String str = "open browser, and navigate to https://host:8743/VirtualKvmGatewayConsole\n"
				+"1.1 sub step\n"
				+ "2input the administrator username and password\n"
				+ "3.click Login";

		// analyzing the string
		System.out.println(str);
		
		str = str.replaceAll("((?m)^\\d+\\.*\\d+)", "?	");
		System.out.println(str);
		
		Pattern p = Pattern.compile(delimiters);
		Matcher m = p.matcher(str);
		while (m.find()) {			
			for(int i=0; i <m.groupCount(); i++) {
				System.out.println(m.group(i+1));
			}
		}
		
		String[] tokensVal = str.split(delimiters);

		// prints the number of tokens
		System.out.println("Count of tokens = " + tokensVal.length);

		for (String token : tokensVal) {
			System.out.print(token);
		}
	}

}
