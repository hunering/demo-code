package com.xmo.demo.java7.innerclass;

class Hello {
	public Runnable r = new Runnable() {
		public void run() {
			System.out.println(this);
			System.out.println(toString());
			
			System.out.println(Hello.this);
			System.out.println(Hello.this.toString());
		}
		
		public String toString() {
			return "Inner toString";
		}
	};

	    
	public String toString() {
		return "Hello's custom toString()";
	}
}
