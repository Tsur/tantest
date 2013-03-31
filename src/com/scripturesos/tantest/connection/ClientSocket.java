package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

public class ClientSocket extends Thread
{
	
	private Socket clientSocket;
	private String clientID;
	//private List<String> commands;
	//private String command;
	private PrintWriter out;
    private BufferedReader in;
    //private SocketResponse responseController;
	private Map<String,ClientResponse> responseHandlers;
	boolean connected = false;
	
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
        //assert clientSocket == null;
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
		
		if(clientSocket != null)
		{
			return this;
		}
        
    	try 
        {
            clientID = client;
            //commands = new ArrayList<String>();
            responseHandlers = new HashMap<String,ClientResponse>();
            Log.i("tantest", "Conectando");
            clientSocket = new Socket(IP, PORT); 
            Log.i("tantest", "Conectado");
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			Log.i("tantest", "Buffers arrancados");
            start();
            
            return this;
		} 
        catch (Exception e) 
        {
        	Log.i("tantest", "Error conexion");
        }

    	return null;
    }
    
    public String getClient()
    {
    	return clientID;
    }
    
    /*public void send(String method, String argument, SocketResponse controller)
    {
    	assert clientSocket != null;
    	
    	String command = "{\"client\":\""+getClient()+"\",\"method\":\""+method+"\",\"arguments\":[\""+argument+"\"]}";

    	commands.add(command);
    	
		responseController = controller;
    }*/
    
    public void send(String method, String argument, ClientResponse controller)
    {

    	String id = UUID.randomUUID().toString();

    	responseHandlers.put(id, controller);

		out.write("{\"client\":\""+getClient()+
    			"\",\"method\":\""+method+
    			"\",\"id\":\""+id+
    			"\",\"arguments\":[\""+argument+"\"]}");
		
		out.flush();
		
		Log.i("tantest", "Mensaje enviado");
    	
    }
    
    public void send(String method, Collection<String> arguments, ClientResponse controller)
    {
    	
    	String id = UUID.randomUUID().toString();
    	
    	String command = "{\"client\":\""+getClient()+
    			"\",\"method\":\""+method+
    			"\",\"id\":\""+id+
    			"\",\"arguments\":";
    	
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
    	
    	responseHandlers.put(id, controller);
    	
    	//commands.add(command);
    	
    	out.write(command);
		out.flush();
    }
    
    private void processResponse(String response)
    {

		//responseController.setResponse(response);
		
		//new Thread(responseController).start();

    	final String res = response;
    	Log.i("tantest", "Respuesta del servidor: "+res);
    	
    	(new Thread() {
		    
			public void run() 
			{
				try 
				{
					//Parse response to JSON object
					JSONObject jres = new JSONObject(res);
					String id = jres.getString("id");
					
					//Get the handler information
					ClientResponse response = responseHandlers.get(id);
					
					//Compose the message including the JSON object
					Message msg = new Message();
					msg.obj = jres;
					msg.what = response.what;
					
					//Send Message to handler
					Log.i("tantest", "Enviamos mensaje al manejador");
					response.handler.sendMessage(msg);
					
					responseHandlers.remove(id);
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
			
		}).start();
		
    }
    
    @Override
    public void run()
    {
    	
    	try 
    	{

    		Log.i("tantest", "ESperando respuesta");
    		
	 	    while(clientSocket.isConnected())
	 	    {
	 	    	
	 	    	if(in.ready())
	 	    	{
 				   //System.out.println("obtengo respuesta");
	 	    		Log.i("tantest", "Tenemos respuesta!");
 				   processResponse(in.readLine());
	 	    	}
 			   
	 	    	/*if(commands.size() > 0)
	 	    	{
 				  for(String command: commands)
 				  {
 					  out.write(command);
 					  out.flush();
 				  }
 				   
 				  commands.clear();
	 	    	}*/
 			   
	 	    	sleep(INTERVAL);
	 	    }
		} 
    	catch (InterruptedException e) 
		{		
			// TODO Auto-generated catch block
			e.printStackTrace();
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
 	   
    }
	
	private final String IP = "192.168.1.132";
    private final int PORT = 3000;
    private final int INTERVAL = 1000;
    //private final int CON_INTERVAL = 100;
}
