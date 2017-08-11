package com.xmo.demo.java7.concurrent.thread;

public class ThreadTest {
	public static void main(String... args) {
		ThreadTest test = new ThreadTest();
		
		Thread thread = new Thread(test.new ThreadRunable());
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread thread2 = test.new SubThread();
		thread2.start();
		
		try {
			thread2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	class ThreadRunable implements Runnable {

		@Override
		public void run() {
			System.out.println("in the ThreadRunable");
			
		}
		
	}
	
	
	class SubThread extends Thread {
		@Override
		public void run() {
			System.out.println("in the SubThread");
		}
	}
}
