package com.intel.java7.nio.asynchronous;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchronousSocketClient {
	Selector _selector;
	
	public void start() {
		AsynchronousSocketChannel client;
		AsynchronousComm comm = new AsynchronousComm();
		try {
			client = AsynchronousSocketChannel.open();
			InetSocketAddress localAddress = new InetSocketAddress("127.0.0.1", 1206);
			client.bind(localAddress);
			
			InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 1205);
			Future<Void> future = client.connect(remoteAddress);
			
			if(future.isCancelled()){
				return;
			} else {
				try {
					if(future.get() == null) {
						// successful completion
						comm.registerSocket("clientside", client);
					} else {
						System.out.println("Connect fail for:" + future.get());
					}
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// selectorThread.join();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
