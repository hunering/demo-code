package com.xmo.demo.java.designpattern;

public class DesignPatternText {

	public static void main(String[] args) {
		SingleTon st = SingleTon.INSTANCE;
		st.setValue(10);
		System.out.println(st.getValue());
		
		SingleTon st2 = SingleTon.INSTANCE;
		System.out.println(st.getValue());
	}

}
