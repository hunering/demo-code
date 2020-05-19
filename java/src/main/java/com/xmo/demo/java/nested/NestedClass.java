package com.xmo.demo.java.nested;

public class NestedClass {

	private int i_InOutter = 0;
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
			//System.out.println("StaticNestedClass" + i_InOutter);
		}
	}
	
	class InnerClass {
		private int i_InInner = 2;
		public InnerClass(){
			System.out.println("InnerClass, i_InOutter:" + i_InOutter + ", i_InInner:" + i_InInner);
			System.out.println("InnerClass, NestedClass.this.i_InOutter:" + NestedClass.this.i_InOutter 
					+ ", this.i_InInner:" + this.i_InInner);
		}
	}
}
