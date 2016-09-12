package com.intel.java7.nio.asynchronous;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ClientSideProtocol extends IMyProtocol{

	AsynchronousSocketChannel _socket;
	ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

	public ClientSideProtocol(AsynchronousSocketChannel socket) {
		_socket = socket;
	}
	
	public void start() {
		_socket.write(writeBuffer, this, new WriteHandler());
	}
	
	class ReadHandler implements CompletionHandler<Integer, ClientSideProtocol> {
		@Override
		public void completed(Integer result, ClientSideProtocol attachment) {
			System.out.println("Client Side, read done, result:" + result);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			readBuffer.clear();
			_socket.write(writeBuffer, attachment, new WriteHandler());
			
		}
	
		@Override
		public void failed(Throwable exc, ClientSideProtocol attachment) {
			System.out.println("Client Side, read failed, result:" + exc);
			
		}
	}
	
	class WriteHandler implements CompletionHandler<Integer, ClientSideProtocol> {

		@Override
		public void completed(Integer result, ClientSideProtocol attachment) {
			System.out.println("Client Side, write done, result:" + result);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writeBuffer.flip();
			_socket.read(readBuffer, attachment, new ReadHandler());		
		}

		@Override
		public void failed(Throwable exc, ClientSideProtocol attachment) {
			System.out.println("Client Side, write failed, result:" + exc);
			
		}
	
	}
}
