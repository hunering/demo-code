package com.xmo.demo.java.grammar;

public class SubClass implements Interface1, Interface2 {
	public static void main(String[] args) {
		SubClass sc = new SubClass();
		System.out.println(sc.sameName());
	}

	@Override
	public String sameName() {
		// TODO Auto-generated method stub
		return "SubClass";
	}

}
