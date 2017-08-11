package com.xmo.demo.java.nested;

public class NestedClass {

	public static void main(String[] args) {
		StaticNestedClass snc = new StaticNestedClass();
		
		// the following line is not allowed, because main is static
		// InnerClass ic = new InnerClass();
		
		// InnerClass is associated with the outterclass's instance
		NestedClass nct = new NestedClass();
		InnerClass ic = nct.new InnerClass();
		
		
		nct.function();
	}
	
	public void function() {
		StaticNestedClass snc2 = new StaticNestedClass();
		InnerClass ic = new InnerClass();
	}

	static class StaticNestedClass {
		public StaticNestedClass(){
			System.out.println("StaticNestedClass");
		}
	}
	
	class InnerClass {
		public InnerClass(){
			System.out.println("InnerClass");
		}
	}
}
