package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI;

  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }
  
  public EchoServer(int port, ChatIF serverUI) 
		    throws IOException 
		  {
		    super(port); //Call the superclass constructor
		    this.serverUI = serverUI;
		  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  
	  String message = (String) msg;
	  String id = portString(message);
	  
	  if (message.startsWith("#login")) {
		  if (client.getInfo("loginId") == null) {				// first time #login is received
			  client.setInfo("loginId", id);
			  System.out.println(message);
		  } else {
			  try {
				client.sendToClient("Login Command cannot be used again! Terminating client session.");
				client.close();
			} catch (IOException e) {}
		  }
		  
	  
	  } else {
		  System.out.println("Message received: " + message + " from <" + client.getInfo("loginId") + ">");
		  this.sendToAllClients(client.getInfo("loginId") + ">" + message);
	  }
	 
	    
  }
  
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message){
	
		if (message.startsWith("#")) {
			if (message.trim().equals("#quit")) {
				serverUI.display("Quiting...");
				try{
					close();
					
				} catch (IOException e) {}
				System.exit(0);
				
			} else if (message.trim().equals("#stop")) {
			      stopListening();
			      serverUI.display("Stop listening for new clients...");
				
				
			} else if (message.trim().equals("#close")) {
				try{
				  stopListening();
			      close();
			      serverUI.display("Disconnecting from all clients...");
			    }
			    catch(IOException e) {}
				
				
			} else if (message.trim().startsWith("#setport")) {
				if ( !(isListening()) && getNumberOfClients() ==0 ) {
					try {
						int port = Integer.parseInt(portString(message).trim());
						setPort(port);
						serverUI.display( "New port: " + String.valueOf(getPort()) );  
					} catch (NumberFormatException e) {
						serverUI.display("Port number has to made up of integers!");  
					}
					
				} else {
					serverUI.display("To change port server has to be closed first. Do #close "); 
				}
				
				
				
			} else if (message.trim().equals("#start")) {   // NOT WORKING??? after change of port or host
				if ( !(isListening()) ) {
					try {
						listen();
					} catch (Exception e) {
						serverUI.display("Listening  for new clients failed. Try again."); 
					}
				} else {
					serverUI.display("Server is already listening!");
				}	
				
				
			} else if (message.trim().equals("#getport")) {
				serverUI.display("Port: " + String.valueOf(getPort()) );
			
				
			
			} else {
				serverUI.display("Command not recognised. Try again.");
			}
			
			
		} else {
			
				serverUI.display("SERVER MSG> " + message);
				this.sendToAllClients("SERVER MSG> " + message);	
		}
 
  }
  
  
  private String portString (String message) {
	  boolean valid = false;
	  String port = "" ;
	  
	  for (int i = 0 ; i < message.trim().length(); i ++) {
		  if (message.charAt(i) == '>') {
			  valid = false;
		  }
		  if (valid) {
			  port = port + message.charAt(i);
		  }
		  if (message.charAt(i) == '<') {
			  valid = true;
		  }
	  }
	  return port;
  }
  
  
  
  
  
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
    
    
  }
  
  
//OCSF Overridden methods ************************************************
  
  /**
   * Hook method called each time a new client connection is
   * accepted.
   * 
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
	    System.out.println("A new client has connected to the server.");
	    this.sendToAllClients("Client " + client + " is connected.");
	    
  }

  
  /**
   * Hook method called each time a client disconnects.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println("Client <" + client.getInfo("loginId") + "> has disconnected.");
	  this.sendToAllClients("Client " + client + " has disconnected.");
  }

  
  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  System.out.println("A client has disconnected from the server.");
	  this.sendToAllClients("A client has disconnected from the server.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
