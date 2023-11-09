// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String loginId;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message){

		if (message.startsWith("#")) {
			if (message.trim().equals("#quit")) {
				clientUI.display("Quiting session...");
				System.exit(0);
				
				
				
			} else if (message.trim().equals("#logoff")) {
				try{
			      closeConnection();
			      clientUI.display("Logging off...");
			    }
			    catch(IOException e) {}
				
				
				
			} else if (message.trim().startsWith("#sethost")) {
				if (!(isConnected())) {
					String host = hostAndPortString(message.trim());
					setHost(host);
					clientUI.display("New host:" + getHost());  
				} else {
					clientUI.display("To change host you have to be logged off first. Do #logoff "); 
				}
				
				
				
			} else if (message.trim().startsWith("#setport")) {
				if (!(isConnected())) {
					try {
						int port = Integer.parseInt(hostAndPortString(message).trim());
						setPort(port);
						clientUI.display("New port: " + getPort());  
					} catch (NumberFormatException e) {
						clientUI.display("Port number has to made up of integers & has to be between <>!");  
					}
					
				} else {
					clientUI.display("To change port you have to be logged off first. Do #logoff "); 
				}
				
				
				
			} else if (message.trim().equals("#login")) {   // NOT WORKING??? after change of port or host
				if (!(isConnected())) {
					try {
						openConnection();
						connectionEstablished();
					} catch (Exception e) {
						clientUI.display("Connection to server failed. Try again."); 
					}
				} else {
					clientUI.display("You are already logged in!");
					try {
						sendToServer(message);
					} catch (IOException e) {}
				}	
				
				
				
			} else if (message.trim().equals("#gethost")) {
				clientUI.display("Host: "+ getHost());
			
			
				
			} else if (message.trim().equals("#getport")) {
				clientUI.display("Port: " + String.valueOf(getPort()) );
			
				
			
			} else {
				clientUI.display("Command not recognised. Try again.");
			}	
			
			
		} else {
			
			try {
		      sendToServer(message);
		    } catch(IOException e) {
		      clientUI.display("Could not send message to server.  Try again.");
		    }
			
		}
	
	
 
  }
  
  
  private String hostAndPortString (String message) {
	  boolean valid = false;
	  String hostOrPort = "" ;
	  
	  for (int i = 0 ; i < message.trim().length(); i ++) {
		  if (message.charAt(i) == '>') {
			  valid = false;
		  }
		  if (valid) {
			  hostOrPort = hostOrPort + message.charAt(i);
		  }
		  if (message.charAt(i) == '<') {
			  valid = true;
		  }
	  }
	  return hostOrPort;
  }
  
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
	public String getLoginId() {
		return loginId;
	}


	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
  
  
//OCSF Overridden methods ************************************************
  
  /**
	 * Hook method from OCSF called after the connection has been closed.
	 */
	protected void connectionClosed() {
		clientUI.display("Connection to server(host: " + getHost() + ", port: " + getPort() + ") has been severed.");
	}

	/**
	 * Hook method from OCSF called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. 
	 * @param exception : the exception raised.
	 */
	protected void connectionException(Exception exception) {
		clientUI.display("The server has shut down.");
		System.exit(0);
	}
	
	/**
	 * Hook method called after a connection has been established. 
	 */
	protected void connectionEstablished() {
		try {
			String msg = "#login <" + loginId + "> has logged on";
			sendToServer(msg);
		} catch (IOException e) {
		}
	}
  
  
}
//End of ChatClient class
