package com.xmo.demo.java7.nio.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

public class NioDatagramClient {
	
	public NioDatagramClient(Selector selector) {
		
	}
	
	public void start() {
		try {

			Selector selector = Selector.open();

			DatagramChannel channel = DatagramChannel.open();

			// channel.configureBlocking(true);
			channel.configureBlocking(false);

			channel.register(selector, SelectionKey.OP_READ
					| SelectionKey.OP_WRITE, new Integer(0));

			Thread selectorThread = new Thread(new SelectWorker(selector));
			selectorThread.start();
			
			InetSocketAddress address = new InetSocketAddress("127.0.0.1",
					6233);

			channel.connect(address);
			System.out.println("-Client- channel connected: " + channel.isConnected());
			
			//ByteBuffer buffer = ByteBuffer.allocate(512);
			//int bytesWrites = channel.write(buffer);
			//System.out.println("in main thread Bytes writed: " + bytesWrites);
			//selectorThread.stop();
			
			// Thread.sleep(1000);

			//channel.close();			
			// selectorThread.join();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public class SelectWorker implements Runnable {

		Selector _selector;

		public SelectWorker(Selector selector) {
			_selector = selector;
		}

		@Override
		public void run() {
			while (true) {
				int i;
				try {
					
					Thread.sleep(1000*5);
					
					i = _selector.select();
					System.out.println("-Client- select thread");
					SocketAddress remoteAddress = null;
					ByteBuffer buffer = ByteBuffer.allocate(512);
					if (i > 0) {
						Set<SelectionKey> keys = _selector.selectedKeys();
						for (SelectionKey key : keys) {
							keys.remove(key);
							System.out.println("-Client- Channel: " + key.attachment()
									+ " Selectted");
							DatagramChannel channel = (DatagramChannel) key.channel();
							if (key.isReadable()) {
								System.out.println("-Client- readable");
								remoteAddress = channel.receive(buffer);
								System.out.println("-Client- Bytes read: " + remoteAddress + ";" + buffer.limit());
								//int byteReads = channel.read(buffer);
								//System.out.println("-Client- Bytes read: " + byteReads);

							} 
							
							buffer.rewind();
							if (key.isWritable()) {
								System.out.println("-Client- writable");
								if(remoteAddress == null) {
									remoteAddress = new InetSocketAddress("127.0.0.1",
											6233);
								}
								int bytesWrites = channel.send(buffer, remoteAddress);
								
								//int bytesWrites = channel.write(buffer);
								System.out.println("-Client- Bytes writed: " + bytesWrites);

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
