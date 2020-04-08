package com.xmo.demo.java7.concurrent.thread;

class Test {
	public static synchronized void static_method() {
		System.out.println("Inside the static method");
		try {
			for (int i = 0; i < 5; i++) {
				Thread.sleep(1000);
				System.out.println("-");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("outside the static method");
	}

	public synchronized void method() {
		System.out.println("inside the not static method");
		try {
			for (int i = 0; i < 5; i++) {
				Thread.sleep(1000);
				System.out.println("*");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("outside the not static method");
	}

}

class ThreadRunable implements Runnable {
	Test test;
	boolean static_access;

	public ThreadRunable(Test test, boolean static_access) {
		this.test = test;
		this.static_access = static_access;
	}

	@Override
	public void run() {
		System.out.println("in the ThreadRunable");
		if (static_access) {
			test.static_method();
		} else {
			test.method();
		}
	}

}

public class SynchronizedTest {

	public static void main(String[] args) {

		Test test = new Test();
		Thread t1 = new Thread(new ThreadRunable(test, false));
		Thread t2 = new Thread(new ThreadRunable(test, true));
		t1.start();
		t2.start();
	}

}
