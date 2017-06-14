package com.mw.java7.nio.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

public class NioDatagramServer {
	
	Selector _selector;
	
	public NioDatagramServer(Selector selector) {
		//_selector = selector;
	}
	
	public void start() {
		DatagramChannel server;
		try {
			server = DatagramChannel.open();
			InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6233);
			server.bind(address);
			server.configureBlocking(false);
			
			_selector = Selector.open();
			server.register(_selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE, "Server channel");
			
			Thread selectorThread = new Thread(new ServerWorker(server));
			selectorThread.start();
			
			// selectorThread.join();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public class ServerWorker implements Runnable {
		
		DatagramChannel _serverChannel;
		
		public ServerWorker(DatagramChannel channel) {
			_serverChannel = channel;
		}
		
		
		@Override
		public void run() {
			while (true) {
				int i;
				try {
					Thread.sleep(1000*5);
					i = _selector.select();
					System.out.println("-Server- select thread");
					
					SocketAddress remoteAddress = null;
					ByteBuffer buffer = ByteBuffer.allocate(512);
					if (i > 0) {
						Set<SelectionKey> keys = _selector.selectedKeys();
						for (SelectionKey key : keys) {
							keys.remove(key);
							System.out.println("-Server- Channel: " + key.attachment()
									+ " Selectted");
							DatagramChannel channel = (DatagramChannel) key.channel();
							if (key.isReadable()) {
								System.out.println("-Server- readable");
								 remoteAddress = channel.receive(buffer);
								System.out.println("-Server- Bytes read: " + remoteAddress + ";" + buffer.limit());

							} 
							
							if (key.isWritable()) {
								System.out.println("-Server- writable");
								buffer.flip();
								if(remoteAddress != null) {
									int bytesWrites = channel.send(buffer, remoteAddress);
									System.out.println("-Server- Bytes writed: " + bytesWrites);
								} else {
									System.out.println("-Server- writable skipped");
								}

							} 
							
							if (key.isAcceptable()) {
								System.out.println("-Server- acceptable");
							} 
							
							if(key.isConnectable()) {
								System.out.println("-Server- connectable");
							}
						}
					}

				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		}
		
	}
}
