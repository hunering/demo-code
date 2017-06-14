package com.mw.java7.nio.asynchronous;

public class AsynchronousTest {
	public static void main(String[] args) {
		
		AsynchronousSocketServer server = new AsynchronousSocketServer();
		AsynchronousSocketClient client = new AsynchronousSocketClient();
		
		server.start();
		client.start();
	}
}
