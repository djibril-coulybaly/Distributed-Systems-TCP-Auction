/**
* Student Name: Djibril Coulybaly
* Student Number: C18423664
* Module: Distributed Systems
* 
* ServerThread.java
**/

// Importing packages 
import java.io.*;
import java.net.*;


public class ServerThread extends Thread {  	
	// Initializing the server, clients and data streams
	private Server server = null;
  	private Socket socket = null;
	private Thread thread;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private int ID = -1;


    // Constructor for creating an instance of the class ServerThread
   	public ServerThread(Server Server, Socket Socket) {
	  	super();
      	server = Server;
      	socket = Socket;
      	ID = socket.getPort();
   	}
	 
	   
	// Getter to retrive the ID of the client in the auction
	public int getID() {
		return ID;
	}

   	
	// Method to send messages to the clients
   	public void send(String msg) {
	   	try {
		    out.writeUTF(msg);
         	out.flush();
       	} catch(IOException ioe) {
		  	System.out.println("An error occured in sending a message to the ID: " + ID + ". See error message for more details - " + ioe.getMessage());
          	server.remove(ID);
          	thread=null;
       	}
   	}
	   
	   
   	// Method to initiate the data input and output streams 
   	public void open() throws IOException {
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
		
		
	// Method to stop and close all server threads
	public void close() throws IOException {
		if (socket != null) {
			socket.close();
		} if (in != null) {
			in.close();
		} if (out != null) {
			out.close();
		}
	}


	// Method to run and process the server thread
	public void run() {
	// The running server thread is displayed to the server   
		System.out.println("Server Thread with ID: " + ID + " is running...");
		thread = new Thread(this);
		
		while (true) {
			try {
			server.broadcast(ID, in.readUTF());

			// Giving the other threads a chance to connect
			int pause = (int)(Math.random()*3000);
			Thread.sleep(pause);
		} catch (InterruptedException e) {
			System.out.println(e);
		} catch(IOException ioe) {
			server.remove(ID);
			thread = null;
		}
		}
	}
}