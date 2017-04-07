package com.mw.java7.nio.datagram;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

public class NioDatagramTest {

	public static void main(String[] args) {
		NioDatagramClient client = new NioDatagramClient(null);
		NioDatagramServer server = new NioDatagramServer(null);
		
		client.start();
		server.start();
		
		try {
			Thread.sleep(1000*100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
