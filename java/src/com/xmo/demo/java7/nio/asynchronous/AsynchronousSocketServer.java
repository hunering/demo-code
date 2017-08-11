package com.xmo.demo.java7.nio.asynchronous;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsynchronousSocketServer {
	Selector _selector;
	AsynchronousComm comm = new AsynchronousComm();

	public void start() {
		AsynchronousServerSocketChannel server;
		try {
			AsynchronousChannelGroup tenThreadGroup = AsynchronousChannelGroup
					.withFixedThreadPool(10, Executors.defaultThreadFactory());
			server = AsynchronousServerSocketChannel.open(tenThreadGroup);
			// server = AsynchronousServerSocketChannel.open();
			InetSocketAddress address = new InetSocketAddress("127.0.0.1", 1205);
			server.bind(address);

			Thread selectorThread = new Thread(new AcceptThread(server));
			selectorThread.start();

			// selectorThread.join();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class AcceptThread implements Runnable {

		AsynchronousServerSocketChannel _server;

		public AcceptThread(AsynchronousServerSocketChannel server) {
			_server = server;
		}

		@Override
		public void run() {
			while (true) {
				Future<AsynchronousSocketChannel> future = _server.accept();

				if (future.isCancelled()) {
					return;
				} else {
					try {
						AsynchronousSocketChannel commSocket = future.get();
						comm.registerSocket("serverside", commSocket);
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
