package edu.seg2105.edu.server.gui;

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.client.ui.ClientConsole;
import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF {
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the server that created this ConsoleChat.
   */
  EchoServer server;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param port The port to connect on.
   */
  public ServerConsole(int port) 
  {
	
    try 
    { 
    server = new EchoServer(port, this);
      server.listen();
      
    } 
    catch(Exception exception) 
    {
      System.out.println("ERROR - Could not listen for clients!.");
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
//Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {
      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        server.handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }  
  
  

@Override
public void display(String message) {
	// TODO Auto-generated method stub
	System.out.println("> " + message);
}

//Class methods ***************************************************

/**
 * This method is responsible for the creation of the Client UI.
 *
 * @param args[0] The port to connect to.
 */
public static void main(String[] args){
  int port = 0 ; 

  // Catching command line arguments exception

 
  try {
  	port = Integer.parseInt(args[0]);
  } catch(ArrayIndexOutOfBoundsException e) {
  	port = DEFAULT_PORT;
  } catch (NumberFormatException e) {
  	port = DEFAULT_PORT;
  }
  
  ServerConsole serverchat = new ServerConsole(port);
  serverchat.accept();  //Wait for console data
}
}
//End of ConsoleChat class

