package com.xmo.demo.java8.lambda;

import java.util.ArrayList;
import java.util.Collections;

public class Examples {

	public static void main(String[] args) {
		System.out.println("=== RunnableTest ===");

		// Anonymous Runnable
		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				System.out.println("Hello world one!");
			}
		};
		// Lambda Runnable
		Runnable r2 = () -> System.out.println("Hello world two!");
		// Run em!
		r1.run();
		r2.run();

		ArrayList<String> strList = new ArrayList<String>();
		strList.add("a");
		strList.add("c");
		strList.add("b");
		Collections.sort(strList, (String p1, String p2) -> p1.compareTo(p2));
		
		for(String p:strList) {
			System.out.println(p);
		}

		
	}

		
}
