package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ClientSocket extends Thread
{
	
	private Socket clientSocket;
	private String clientID;
	private List<String> commands;
	//private String command;
	private PrintWriter out;
    private BufferedReader in;
    private SocketResponse responseController;
    private boolean ready = false;
	
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
            clientID = client;
            commands = new ArrayList<String>();
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
    	
    	String command = "{\"client\":\""+getClient()+"\",\"method\":\""+method+"\",\"arguments\":[\""+argument+"\"]}";

    	commands.add(command);
    	
		responseController = controller;
    }
    
    public void send(String method, Collection<String> arguments, SocketResponse controller)
    {

    	assert clientSocket != null;
    	
    	String command = "{\"client\":\""+getClient()+"\",\"method\":\""+method+"\",\"arguments\":";
    	
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
    	
    	commands.add(command);
		
		responseController = controller;
    }
    
    private void processResponse(String response)
    {
    	//System.out.println("He obtenido respuesta:" + response);
    	if(response.equals("who are you?"))
    	{
    		out.write("{\"client\":\""+getClient()+"\"}");
            out.flush();
            
            ready = true;
    	}
    	else
    	{
    		responseController.setResponse(response);
    		
    		new Thread(responseController).start();
    	}
    }
    
    @Override
    public void run()
    {
    	
    	try 
    	{
			clientSocket = new Socket(IP, PORT); 
			
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} 
    	catch (UnknownHostException e1) 
    	{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
    	catch (IOException e1) 
    	{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
 			   
 			   if(ready == true && commands.size() > 0)
 			   {
 				  for(String command: commands)
 				  {
 					  out.write(command);
 					  out.flush();
 				  }
 				   
 				  commands.clear();
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
	
	private final String IP = "192.168.1.130";
    private final int PORT = 3000;
    private final int INTERVAL = 1000;
}
