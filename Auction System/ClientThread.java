/**
* Student Name: Djibril Coulybaly
* Student Number: C18423664
* Module: Distributed Systems
* 
* ClientThread.java
**/

// Importing packages 
import java.io.*;
import java.net.*;


public class ClientThread extends Thread { 
	// Initializing the server, clients and data streams 
	private Socket socket = null;
	private Client client = null;
	private DataInputStream streamIn = null;

    // Constructor for creating an instance of the class ClientThread
	public ClientThread(Client client, Socket socket) {  
		this.client   = client;
		this.socket   = socket;
		open();
		start();
	}


    // Method to create input streams between the client and the server
	public void open() {  
		try {
		  	streamIn = new DataInputStream(socket.getInputStream());
		} catch(IOException ioe) {
            System.out.println("An error has occured in retrieving the input stream. See error message for more details - " + ioe.getMessage());
			client.stop();
		}
	}


    // Method to close input streams between the client and the server
	public void close() {  
        try {  
			if (streamIn != null) {
                streamIn.close();
            }
		} catch(IOException ioe) {  
            System.out.println("An error has occured in closing the input stream. See error message for more details - " + ioe.getMessage());
		}
	}


    // Method to execute the threads and handle messages
	public void run() {
		while (true && client != null) {
		  	try {
				client.handle(streamIn.readUTF());
			} catch(IOException ioe) {
			  	client = null;
			  	System.out.println("A listening error has occured. See error message for more details - " + ioe.getMessage());
			}
		}
	}
}



