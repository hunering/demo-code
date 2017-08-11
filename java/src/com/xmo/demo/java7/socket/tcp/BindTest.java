package com.xmo.demo.java7.socket.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BindTest {

	public static void main(String[] args) {
		Socket kkSocket = new Socket();
		Socket kkSocket2 = new Socket();
		try {
			kkSocket.bind(new InetSocketAddress("127.0.0.1", 1234));
			kkSocket2.bind(new InetSocketAddress("127.0.0.1", 1234));
			System.out.println("binded");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
