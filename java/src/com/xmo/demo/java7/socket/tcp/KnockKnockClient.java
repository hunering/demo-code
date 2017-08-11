package com.xmo.demo.java7.socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class KnockKnockClient {

	public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 4444;

		if (args.length == 2) {
	        hostName = args[0];
	        portNumber = Integer.parseInt(args[1]);
        }
 
		System.out.println("Starting knock client");
 
        try (
            Socket kkSocket = new Socket(hostName, portNumber);
        		
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        ) {
        	
    		System.out.println("remote port: " + kkSocket.getPort() + "; local port: " + kkSocket.getLocalPort());
    		System.out.println("remote addr: " + kkSocket.getRemoteSocketAddress() + 
    				"; local addr: " + kkSocket.getLocalAddress());
    		
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
 
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
                 
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }

	}

}
