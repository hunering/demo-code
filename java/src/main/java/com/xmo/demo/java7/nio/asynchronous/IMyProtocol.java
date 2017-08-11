package com.xmo.demo.java7.nio.asynchronous;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class IMyProtocol {
	static public IMyProtocol getNewServerSideInstance(String type, AsynchronousSocketChannel socket) {
		if(type.equals("serverside")) {
			return new ServerSideProtocol(socket);
		} else if(type.equals("clientside")) {
			return new ClientSideProtocol(socket);
		} else {
			System.out.println("unknown protocol type");
			return null;
		}
	}
	
	
	abstract public void start();
}
