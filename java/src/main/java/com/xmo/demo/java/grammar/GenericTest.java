package com.xmo.demo.java.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenericTest {

	class InnerClass {
		ArrayList<String> list;
		public InnerClass() {
			
		}
		
		public <T extends String> void getNumber(List<T> param1) {
			 System.out.println(param1);
		}
		
		public void getNumber2(List<? extends String> param1) {
			System.out.println(param1);
		}
	}
	public static void main(String[] args) {
		GenericTest test = new GenericTest();
		InnerClass i1 = test.new InnerClass();
		i1.getNumber(Arrays.asList("d"));
		i1.getNumber2(Arrays.asList("d"));
	}

}
