package com.xmo.demo.java7.nio.asynchronous;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchronousComm {
	
	// List<AsynchronousSocketChannel> _sockets = Collections.synchronizedList(new LinkedList<AsynchronousSocketChannel>());
	
	List<IMyProtocol> _protocols = Collections.synchronizedList(new LinkedList<IMyProtocol>());
	
	public void registerSocket(String type, AsynchronousSocketChannel socket) {
		IMyProtocol protocol = IMyProtocol.getNewServerSideInstance(type, socket);
		_protocols.add(protocol);
		protocol.start();
	}
	
	public void removeSocket(AsynchronousSocketChannel socket) {
		//_sockets.remove(socket);
	}
	
		
	public class CommThread implements Runnable {
		
		
		public CommThread() {
			
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
