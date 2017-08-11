package com.xmo.demo.java7.concurrent.wait;

public class ProducerConsumerExample {
	public static void main(String... args) throws InterruptedException {

		Drop drop = new Drop();
		
		Producer p = new Producer();
		p.drop = drop;
		
		Consumer s = new Consumer();
		s.drop = drop;
		
		Thread tp = new Thread(p);
		Thread ts = new Thread(s);
		
		tp.start();
		ts.start();
		
		tp.join();
		//drop.put("dd");
		//drop.take();
	}
	
	static class Producer implements Runnable{
		public Drop drop;
		
		@Override
		public void run() {
			int i = 0;
			while (true) {
				String msg = "" + i;
				
				drop.put(msg);
				i++;
			}
			
			
		}
		
	}
	
	static class Consumer implements Runnable{
		public Drop drop;
		
		@Override
		public void run() {
			int i = 0;
			while (true) {
				String msg = drop.take();
				
			}
			
			
		}
		
	}
}
