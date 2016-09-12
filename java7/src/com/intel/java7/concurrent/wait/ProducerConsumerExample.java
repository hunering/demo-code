package com.intel.java7.concurrent.wait;

public class ProducerConsumerExample {
	public static void main(String... args) {
		Drop drop = new Drop();
		drop.put("dd");
		drop.take();
	}
}
