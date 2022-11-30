/**
* Student Name: Djibril Coulybaly
* Student Number: C18423664
* Module: Distributed Systems
* 
* Client.java
**/

// Importing packages 
import java.io.*;
import java.net.*;

public class Client implements Runnable {
	// Initializing the server, clients and data streams
	private Socket socket = null;
	private Thread thread = null;
	private BufferedReader console = null;
	private DataOutputStream output = null;
	private ClientThread client = null;
	private String username;

    // Constructor for creating an instance of the class Client
	public Client(String serverName, int serverPort, String name) {
        System.out.println("Connecting to server, please wait...");

	  	this.username = name;
		
		try {
		 	socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket); 
			System.out.println("\n--------------------------------------");            
			System.out.println("Welcome " + username + " to the 2021 Car e-Auction!");
			System.out.println("--------------------------------------\n");            

			start();
		} catch(UnknownHostException uhe) {
            System.out.println("Unfornunately, the host name is unknown. See error message for more details - " + uhe.getMessage());
	  	} catch(IOException ioe) {
			System.out.println("An unexpected exception has occured. See error message for more details - : " + ioe.getMessage());
	  	}
	}

	
	// Method to stop and close all streams
	public void stop() {
		try {  
			if (console != null) {
				console.close();
			}
			if (output != null) {
				output.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch(IOException ioe) {
			System.out.println("An Error has occured in closing the streams.  See error message for more details - " + ioe.getMessage());
		}
		client.close();
		thread = null;
	}

	
	// Method to initiate stream readers and buffers 
	public void start() throws IOException {
		// Setting up the input & output stream
		console = new BufferedReader(new InputStreamReader(System.in));
		output = new DataOutputStream(socket.getOutputStream());

		// Creating a new thread for the client if there isn't one already
		if (thread == null) {  
			client = new ClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}	
	}	
	
	
	// Method to relay messages
	public void run() {
		while (thread != null) {
			try {
				String message = console.readLine();
				
				if (isNumeric(message)) {
					output.writeUTF(message);
					output.flush();
				} else {
					System.out.println("The value you have entered is not valid. Please enter a valid numerical amount for the bid!");
				}	
			} catch(IOException ioe) {  
				System.out.println("An error has occured in sending the message. See error message for more details - " + ioe.getMessage());
				stop();
			}	
		}	
	}	
	
	
	// Method to check that the input sent by the user when bidding is a numeric value
	public boolean isNumeric(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}	
	}	


	// Method to take care of replies from the server
	public void handle(String readUTF) {  
		if (readUTF.equals(".exit")) { 
			stop();
			System.exit(0);
		} else {
			System.out.println(readUTF);
		}
	}


    // Main method perfors a check on command line arguments entered by the user
	public static void main(String args[]) {  
		Client client = null;
		if (args.length != 3) {
			System.out.println("Usage: java Client host port name");
		} else {
			client = new Client(args[0], Integer.parseInt(args[1]), args[2]);
		}
	}
}
