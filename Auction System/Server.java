/**
* Student Name: Djibril Coulybaly
* Student Number: C18423664
* Module: Distributed Systems
* 
* Server.java
**/

// Importing packages 
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server implements Runnable {  
	// Initializing the clients in the auction    
	private ServerThread clients[] = new ServerThread[50];
	private ServerSocket serverSocket = null;
	private Thread thread = null;
	private int clientCount = 0;

	// Initializing the cars up for auction 
	public ArrayList<Car> cars = new ArrayList<Car>();
	public Car car1 = new Car("Volkswagen Golf", 0, 4300, 0);
	public Car car2 = new Car("Renault Clio", 0, 5700, 1);
	public Car car3 = new Car("Nissan Micra", 0, 1299, 2);
	public Car car4 = new Car("Peugeot 208", 0, 7500, 3);
	public Car car5 = new Car("Ford Focus", 0, 11000, 4);
	public Car currentItem = car1;

	// Initializing the timer
	public int timeDefault = 60;
	public int time = timeDefault;

    // Constructor for creating an instance of the class Server
	public Server(int port) {
		try {
			// Starts the auction with a UI
			System.out.println("Binding to port " + port);
			serverSocket = new ServerSocket(port);
			System.out.println("The server has started at: " + serverSocket.getInetAddress());
			start();
			System.out.println("\nThe auction has started. Here are the cars:");
			
			cars.add(car1);
			cars.add(car2);
			cars.add(car3);
			cars.add(car4);
			cars.add(car5);
			System.out.println("---------------------------------------------");
			for(Car c: cars) {
				System.out.println(c);
			}
			

			timer();
			System.out.println("---------------------------------------------");
			System.out.println("Current " + currentItem);
		}
		catch(IOException ioe) {
            System.out.println("The bind failed to port " + port + ". See error message for more details - " + ioe.getMessage());
		}
	}


 	// Method for managing the countdown timer and its various operations before and after it reaches 0 
   	public void timer() {
      	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
      	executor.scheduleAtFixedRate(new Runnable() {
         	public void run() {
				// The time is displayed up until the timer reaches 0
	            if (time >= 0) {
	               System.out.print('\r');
	               System.out.print("Time: " + time--);
	            }

				if (time == 30) {
					for (int i = 0; i < clientCount; i++) { 
						clients[i].send("--------------------------------------");
						clients[i].send("30 Seconds Remaining...");
						clients[i].send("--------------------------------------");;
					 }
				}

				if (time == 15) {
					for (int i = 0; i < clientCount; i++) { 
						clients[i].send("--------------------------------------");
						clients[i].send("15 Seconds Remaining...");
						clients[i].send("--------------------------------------");;
					 }
				}

				if (time == 10) {
					for (int i = 0; i < clientCount; i++) { 
						clients[i].send("--------------------------------------");
						clients[i].send("10 Seconds Remaining...");
						clients[i].send("--------------------------------------");;
					 }
				}

				if (time == 5) {
					for (int i = 0; i < clientCount; i++) { 
						clients[i].send("--------------------------------------");
						clients[i].send("5 Seconds Remaining...");
						clients[i].send("--------------------------------------");;
					 }
				}

				// Various checks are peformed if the timer reaches 0
	            if (time < 0) {
	               	System.out.print('\r');
	               	// If the highest bid is less than the reserve bidding price and depending if there's still cars left in the list, the current car will be modified
	               	if (currentItem.currentBidPrice<currentItem.reserveBidPrice) {
	                  	if (currentItem.getIndex() + 1 < cars.size()) {
	                     	currentItem = cars.get(currentItem.getIndex()+1);
	                  	} else if (cars.size()==2) {
							if (currentItem.getIndex() == 0) {
								currentItem = cars.get(currentItem.getIndex()+1);
							} else {
								currentItem = cars.get(0);
							}	
	                  	} else {                   
	                        currentItem = cars.get(0);
	                  	}
						
						// The cars list is updated to reflect the latest changes and is displayed to the server
	                  	System.out.println("\nThe car hasn't been sold!\n");
	                  	System.out.print('\r');

	                  	System.out.println("---------------------------------------------");
	                  	for(Car c: cars)
	                  	{
	                     	System.out.println(c);
	                  	}
	                  	System.out.println("---------------------------------------------");  
	                  	System.out.print('\r');
	                  	System.out.println("Current " + currentItem);
	                  	
						// The timer is reset so that the clients have 60 seconds to bid
						time = timeDefault;

	                  	// All clients are updated with a notice that the car hasn't been sold
	                  	for (int i = 0; i < clientCount; i++) {
	                     	clients[i].send("\nThe car wasn't sold. It will be re-introduced in the next round!\n");
							clients[i].send("---------------------------------------------");
	                     	clients[i].send("Current " + currentItem  + " | Time left: " + time + " seconds");
	                     	clients[i].send("\nPlease enter a bid for the car: ");
	                  	}
	               	} else if (currentItem.currentBidPrice >= currentItem.reserveBidPrice) { // If the highest bid price is greater than or equal to the reserve bid price, the car will been sold, removed, and the next car will become the current car
						if (currentItem.getIndex() + 1 < cars.size()) {
							cars.remove(currentItem);
							currentItem = cars.get(currentItem.getIndex());
							currentItem.index = cars.indexOf(currentItem);
	                  	} else {
							cars.remove(currentItem);
							currentItem = cars.get(0);
							currentItem.index = cars.indexOf(currentItem);   
						}

						// The cars list is updated to reflect the latest changes and is displayed to the server
	                  	for (Car item : cars) {
	                     	item.index = cars.indexOf(item);
	                  	}

	                  	System.out.println("\nThe car has been sold!\n");
	                  	System.out.print('\r');

	                 	System.out.println("---------------------------------------------");
	                  	for (Car c: cars) {
	                     	System.out.println(c);
	                  	}
	                  	System.out.println("---------------------------------------------");
	                  	System.out.print('\r');
	                  	System.out.println("Current " + currentItem);
	                  	
						// The timer is reset so that the clients have 60 seconds to bid
	                  	time = timeDefault;
	                  	
						// All clients are updated with a notice that the car has been sold
	                  	for (int i = 0; i < clientCount; i++) { 
	                     	clients[i].send("\nThe car has been sold!");
	                     	clients[i].send("Current " + currentItem  + " | Time left: " + time + " seconds\n");
	                     	clients[i].send("\nPlease enter a bid: ");
	                  	}
	               	}
	               	
	               	// If there's no more cars left to auction, then the program will end
	               	if (cars.size() == 0) {
	               		for (int i = 0; i < clientCount; i++) {
	               			clients[i].send("\nThe auction has ended! Thank you for participating. Please press the Ctrl key + C key to exit!\n");	
	               		}
	               		System.exit(0);
	               	}
            	}
         	}	
      	}, 0, 1, TimeUnit.SECONDS);
   	}


	// Method to search for clients and either accept or reject them when they try to connect
	public void run() {
		while (thread != null) {
			try {
				// Listen on port for incoming connections and add them to the threads list
				System.out.println("Listening for a bidder to join in the auction...\n");
				addThread(serverSocket.accept());

				// Giving the other threads a chance to connect
				int pause = (int)(Math.random()*3000);
				Thread.sleep(pause);
			} catch(IOException ioe) {
				System.out.println("\nAn error has occured with the server being accepted. See error message for more details - " + ioe.getMessage());
				stop();
			} catch (InterruptedException ie) {
				System.out.println(ie);
			}
		}
	}


   	// Method to start a thread
	public void start() {
      	if (thread == null) {
			thread = new Thread(this);
			thread.start();
      	}
   	}


	// Method to stop and close a thread
	public void stop() {
     	thread = null;
	}	


	// Method to find a client by it ID
   	private int findClient(int clientID) {
		for (int i = 0; i < clientCount; i++) {
			if (clients[i].getID() == clientID) {
				return i;
			}
		}	
      	return -1;
   	}


   	// Method to send messages to all clients
	public synchronized void broadcast(int clientID, String input) {
  		int newBid = Integer.parseInt(input);
      	boolean newHigh = false;
      	System.out.println("\n\nA bid has been placed by Client ID: " + clientID + " " + input + " Euro");

      	// If the new bid price is higher than the current bid, reset the timer and set newHigh to true
      	if (newBid > currentItem.currentBidPrice) {
         	newHigh = true;
         	time = timeDefault;
      	} for (int i = 0; i < clientCount; i++) {
      		// Sending an update message to all clients about the new bid except to the client who placed the new bid
         	if (clients[i].getID() != clientID && newHigh) {
            	currentItem.currentBidPrice = newBid;
            	clients[i].send("\nClient ID: "+ clientID + " is the highest bidder with: " + input + " Euro | Reserve Bid Price: " + currentItem.reserveBidPrice + " Euro");
         		clients[i].send("Current " + currentItem + " | Time left: " + time + " seconds");
         	} if (clients[i].getID() == clientID && newHigh) { // Sending an update message to the client who placed the new bid
            	currentItem.currentBidPrice = newBid;
            	clients[i].send("\nCongratulations! You are the new highest bidder!");
            	clients[i].send("Current " + currentItem + " | Time left: " + time + " seconds");

				// Sending an update message to the server about the new bid placed by the client
            	System.out.println("\nClient ID: "+ clientID + " is the highest bidder with: " + input + " Euro | Reserve Bid Price: " + currentItem.reserveBidPrice + " Euro");
         	} else if (clients[i].getID() == clientID && !newHigh) { // Sending an update message to the clients who placed a bid that is less than or equal to the highest bid
            	clients[i].send("\nThe bid that you entered is less than or equal to the current highest bid. Please enter a higher bid:");
            	clients[i].send("Current " + currentItem + " | Time left: " + time + " seconds");
         	}
      	}
      	// Notify all threads about broadcast messages
      	notifyAll();
    }


    // Method that deals with a client quitting the auction
   	public synchronized void remove(int clientID) {
    	int pos = findClient(clientID);
      	
		// If the position is found using their client ID, then the client is removed
		if (pos >= 0) {
         	ServerThread toTerminate = clients[pos];
         	System.out.println("\nRemoving Client ID: " + clientID + " at " + pos );
         	if (pos < clientCount-1) {
            	for (int i = pos+1; i < clientCount; i++) {
               		clients[i-1] = clients[i];
				}
         		clientCount--;
         		try {
         			toTerminate.close();
         		} catch(IOException ioe) {
         			System.out.println("Error closing thread: " + ioe);
         		}
         		toTerminate = null;

         		// The bid and timer is reset when a bidder leaves the auction 
            	currentItem.currentBidPrice = 0;
         		time = timeDefault;

				// Sending an update message to all clients about the client quitting the auction
         		for (int i = 0; i < clientCount; i++) {
            		clients[i].send("\nA bidder has left the auction, this bid is reset");
               		clients[i].send("Current " + currentItem  + " | Time left: " + time + " seconds");
               		clients[i].send("Please enter a bid: ");
            	}
								
				// Sending an update message to the server about the client quitting the auction
         		System.out.println("\nClient at " + pos + " has left the auction, therefore the bid has been reset!");
			}

			// Notify all threads about broadcast messages
			notifyAll();
      	}
   	}


   	// Method to add a thread whenever a new client enters, depending if there's space in the auction room or not
   	private void addThread(Socket socket) {
      	if (clientCount < clients.length) {
			// Sending an update message to the server about the new client that joined the auction & adding the client into the server thread
         	System.out.println("\nA new bidder has entered the auction!");
         	clients[clientCount] = new ServerThread(this, socket);
         	try {
            	clients[clientCount].open();
            	clients[clientCount].start();
            	clientCount++;

				// Sending an update message to the clients about the new client that joined the auction 
            	for (int i = 0; i < clientCount; i++) {
            		clients[i].send("\nA new bidder has entered the auction");
               		clients[i].send("Current " + currentItem  + " | Time left: " + time + " seconds");
               		clients[i].send("Please enter a bid: ");
            	}
         	} catch(IOException ioe) {
       			System.out.println("An error has occured in opening the thread. See error message for more details - " + ioe.getMessage());
         	}
      	} else {
			System.out.println("The client has been rejected due to the auction room reaching its maximum capacity of " + clients.length);
	   	}	
	}


    // Main method perfors a check on command line arguments entered by the user
	public static void main(String args[]) {
      	Server server = null;
      	if (args.length != 1) {
         	System.out.println(" Usage: java Server port");
		} else {
         	server = new Server(Integer.parseInt(args[0]));
		}
	}
}