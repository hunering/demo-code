package com.mw.java7.socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class KnockKnockServer {

	public static void main(String[] args) {
		int portNumber = 4444;
		if (args.length == 1) {
			portNumber = Integer.parseInt(args[0]);;
        }        
 
		System.out.println("Starting knock server");
        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
    		System.out.println("remote port: " + clientSocket.getPort() + 
    				"; local port: " + clientSocket.getLocalPort());
    		System.out.println("remote addr: " + clientSocket.getRemoteSocketAddress() + 
    				"; local addr: " + clientSocket.getLocalAddress());

    		
            String inputLine, outputLine;
             
            // Initiate conversation with client
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            outputLine = kkp.processInput(null);
            out.println(outputLine);
 
            while ((inputLine = in.readLine()) != null) {
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye."))
                    break;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

	}

}
