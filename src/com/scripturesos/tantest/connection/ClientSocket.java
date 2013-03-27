package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;

public class ClientSocket extends Thread
{
	
	private Socket clientSocket;
	private String clientID;
	private String command;
	private PrintWriter out;
    private BufferedReader in;
    private SocketResponse responseController;
	
	//Bill Pugh Singleton thread safe solution
	private ClientSocket(){}
	
	private static class InstanceHolder 
	{
	    private static final ClientSocket instance = new ClientSocket();
	}

	public static ClientSocket getInstance() 
	{
	    return InstanceHolder.instance;
	}
	
	public final ClientSocket init(String client) 
    {
        assert clientSocket == null;
		/*if(clientSocket != null)
    	{
        	try 
        	{
				clientSocket.close();
			} 
        	catch (IOException e) 
        	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}*/
        
    	try 
        {
            clientSocket = new Socket(IP, PORT);

            clientID = client;
            
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            start();
		} 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

    	return this;
    }
    
    public String getClient()
    {
    	return clientID;
    }
    
    public void send(String method, String argument, SocketResponse controller)
    {
    	assert clientSocket != null;
    	
    	command = "{\"client\":\""+getClient()+"\",\"method\":\""+method+"\",\"arguments\":[\""+argument+"\"]}";
    	
    	out.write(command);
		out.flush();
		
		responseController = controller;

    }
    
    public void send(String method, Collection<String> arguments, SocketResponse controller)
    {

    	assert clientSocket != null;
    	
    	command = "{\"client\":\""+getClient()+"\",\"method\":\""+method+"\",\"arguments\":";
    	
    	String args = "[";
    	
    	Iterator<String> it = arguments.iterator();
    	
    	while(it.hasNext())
    	{
    		args += "\""+it.next()+"\",";
    	}
    	
    	if(arguments.size() > 0)
    	{
    		args = args.substring(0,args.length()-1);
    	}

    	args += "]}";
    	
    	command += args;
    	
    	out.write(command);
		out.flush();
		
		responseController = controller;
    }
    
    private void processResponse(String response)
    {
    	//System.out.println("He obtenido respuesta:" + response);
    	if(response.equals("who are you?"))
    	{
    		out.write("{\"client\":\""+getClient()+"\"}");
            out.flush();
    	}
    	else
    	{
    		responseController.setResponse(response);
    		
    		Thread responseControllerThread = new Thread(responseController);
    		
    		responseControllerThread.start();
    	}
    }
    
    @Override
    public void run()
    {
    	String response;
    	
 	    while(clientSocket.isConnected())
 	    {
 		     
 		   try 
 		   {
 			   //System.out.println("dando vueltas");
 			   if(in.ready())
 			   {
 				   //System.out.println("obtengo respuesta");
 				   response = in.readLine();
 				   processResponse(response);
 			   }
 			   
 			   sleep(INTERVAL);
 			   
 		   } 
 		   catch (InterruptedException e) 
 		   {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 		   catch (IOException e) 
 		   {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		   }
 		   
 	    }
 	   
    }
	
	private final String IP = "localhost";
    private final int PORT = 3000;
    private final int INTERVAL = 1000;
}
