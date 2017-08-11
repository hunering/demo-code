package com.xmo.demo.java7.nio.asynchronous;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerSideProtocol extends IMyProtocol{

	AsynchronousSocketChannel _socket;
	ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

	public ServerSideProtocol(AsynchronousSocketChannel socket) {
		_socket = socket;
	}
	
	public void start() {
		_socket.read(readBuffer, this, new ReadHandler());
	}
	
	class ReadHandler implements CompletionHandler<Integer, ServerSideProtocol> {
		@Override
		public void completed(Integer result, ServerSideProtocol attachment) {
			System.out.println("Server Side, read done, result:" + result);
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
		public void failed(Throwable exc, ServerSideProtocol attachment) {
			System.out.println("Server Side, read failed, result:" + exc);
			
		}
	}
	
	class WriteHandler implements CompletionHandler<Integer, ServerSideProtocol> {

		@Override
		public void completed(Integer result, ServerSideProtocol attachment) {
			System.out.println("Server Side, write done, result:" + result);
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
		public void failed(Throwable exc, ServerSideProtocol attachment) {
			System.out.println("Server Side, write failed, result:" + exc);
			
		}
	
	}
}
