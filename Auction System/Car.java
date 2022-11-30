/**
* Student Name: Djibril Coulybaly
* Student Number: C18423664
* Module: Distributed Systems
* 
* Car.java
**/

// Importing packages 
import java.io.*;
import java.net.*;


public class Car {
    // Declaring variables used for each car being bidded
    String carName;
    int currentBidPrice;
    int reserveBidPrice;
    int index;


    // Constructor for creating an instance of the class Car
    public Car (String CarName, int CurrentBidPrice, int ReservePrice, int Index) {
        index = Index; 
        carName = CarName;
        currentBidPrice = CurrentBidPrice;
        reserveBidPrice = ReservePrice; 
    }


    // Getter to retrive the index of the item in the auction list
    public int getIndex() {
        return index;
    }


    // Getter to retrive the current bidding price of the item being auctioned
    public int getCurrentBid() { 
        return currentBidPrice; 
    }  


    // Getter to retrive the reserved bidding price of the item being auctioned
    public int getReservePrice() { 
        return reserveBidPrice; 
    }


    // Getter to retrive the name of the item being auctioned
    public String getName() { 
        return carName; 
    }

    
    // Method to display the current information on the item being autioned to the client and server
    public String toString() {
        return ("Car: " + carName + " | Current Bid Price: " + currentBidPrice + " Euro | Reserve Bid Price: " + reserveBidPrice + " Euro");
    }
}