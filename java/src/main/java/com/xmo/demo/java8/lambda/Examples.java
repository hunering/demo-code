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
		Runnable r3 = () -> {
			System.out.println("Hello world Three!");
			// The following line case problem.
			//System.out.println(this);
			
			System.out.println("access R2 in R3, R2:" + r2 + ", 'this' is not accessble in a static main");
		};
		// Run em!
		r1.run();
		r2.run();
		r3.run();
		
		ArrayList<String> strList = new ArrayList<String>();
		strList.add("a");
		strList.add("c");
		strList.add("b");
		
		Collections.sort(strList, (String p1, String p2) -> p1.compareTo(p2));
		// the param type for lambda could be calculated by Java
		Collections.sort(strList, (p1, p2) -> p1.compareTo(p2));
		
		for(String p:strList) {
			System.out.println(p);
		}

		
	}

		
}
