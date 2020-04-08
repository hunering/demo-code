package com.xmo.demo.java.grammar;



class Super {
	String s = "Super";
	public String getS() {
		return s;
	}
}

class Sub extends Super {
	String s = "Sub";
}



public class FieldMask {
	public static void main(String[] args) {
		Sub c1 = new Sub();
		System.out.println(c1.s);
		System.out.println(c1.getS());
		
		Super c2 = new Sub();
		System.out.println(c2.s);
		System.out.println(c2.getS());
	}
}
